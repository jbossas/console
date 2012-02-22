/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package org.jboss.as.console.client.shared.subsys.infinispan;

import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.infinispan.model.LocalCache;
import org.jboss.as.console.client.shared.subsys.infinispan.model.ReplicatedCache;
import org.jboss.as.console.client.shared.viewframework.DmrCallback;
import org.jboss.as.console.client.shared.viewframework.EntityToDmrBridgeImpl;
import org.jboss.as.console.client.shared.viewframework.FrameworkView;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.as.console.client.widgets.forms.PropertyBinding;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.Property;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * This EntityToDmrBridge has special logic to handle the singleton-object pattern used with the cache
 * models.  It also has logic to set a cache as the default container if requested.
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2011 Red Hat Inc.
 */
public class CacheEntityToDmrBridge<T extends LocalCache> extends EntityToDmrBridgeImpl<T> {

    private static final Set<String> singletons = new HashSet<String>();
    private static final Set<String> addRemoveSingletonCheckboxes = new HashSet<String>();

    static {
        singletons.add("store");
        singletons.add("locking");
        singletons.add("eviction");
        singletons.add("expiration");
        singletons.add("transaction");
        singletons.add("file-store");
        singletons.add("remote-store");
        singletons.add("jdbc-store");
        singletons.add("state-transfer");

        addRemoveSingletonCheckboxes.add("hasStore");
        addRemoveSingletonCheckboxes.add("hasLocking");
        addRemoveSingletonCheckboxes.add("hasEviction");
        addRemoveSingletonCheckboxes.add("hasExpiration");
        addRemoveSingletonCheckboxes.add("hasTransaction");
        addRemoveSingletonCheckboxes.add("hasFileStore");
        addRemoveSingletonCheckboxes.add("hasRemoteStore");
        addRemoveSingletonCheckboxes.add("hasJdbcStore");
        addRemoveSingletonCheckboxes.add("hasStateTransfer");
    }

    public CacheEntityToDmrBridge(ApplicationMetaData propertyMetadata, Class<? extends T> type, FrameworkView view,
                                 DispatchAsync dispatcher) {
        super(propertyMetadata, type, view, dispatcher);
    }

    @Override
    public String getName(T entity) {
        return  entity.getCacheContainer() + "/" + entity.getName();
    }

    @Override
    protected ModelNode getResourceAddress(String name) {
        String[] compoundName = name.split("/");
        return address.asResource(Baseadress.get(), compoundName[0], compoundName[1]);
    }

    @Override
    public void loadEntities(String nameEditedOrAdded) {
        this.nameOfLastEdited = nameEditedOrAdded;

        ModelNode steps = new ModelNode();
        steps.get(OP).set(COMPOSITE);

        // get all the cache info
        ModelNode readRscOp = address.asResource(Baseadress.get(), "*", "*");
        readRscOp.get(OP).set(READ_RESOURCE_OPERATION);
        readRscOp.get(RECURSIVE).set(true);
        steps.get(STEPS).add(readRscOp);

        // for each container, find the default cache
        ModelNode readAttrOp = new ModelNode();
        readAttrOp.get(ADDRESS).set(Baseadress.get()).add("subsystem", "infinispan").add("cache-container", "*");
        readAttrOp.get(OP).set(READ_ATTRIBUTE_OPERATION);
        readAttrOp.get(NAME).set("default-cache");
        steps.get(STEPS).add(readAttrOp);

        System.out.println("load entities for " + this.type.getName());
        System.out.println(steps.toString());

        dispatcher.execute(new DMRAction(steps), new DmrCallback() {
            @Override
            public void onDmrSuccess(ModelNode response) {
               System.out.println("loadEntities response=");
               System.out.println(response.toString());
               onLoadEntitiesSuccess(response);
            }
        });

    }

    @Override
    protected void onLoadEntitiesSuccess(ModelNode response) {
        List<T> entities = new ArrayList<T>();

        // TODO: https://issues.jboss.org/browse/AS7-3670
        for (ModelNode entity : response.get(RESULT).get("step-1").get(RESULT).asList()) {
            ModelNode result = entity.get(RESULT);
            for (Property addressProp : entity.get(ADDRESS).asPropertyList()) {
                result.get(addressProp.getName()).set(addressProp.getValue());
            }

            T cache = entityAdapter.fromDMR(result);

            cache.setHasEviction(result.get("eviction").isDefined());
            cache.setHasExpiration(result.get("expiration").isDefined());
            cache.setHasLocking(result.get("locking").isDefined());
            cache.setHasStore(result.get("store").isDefined());
            cache.setHasFileStore(result.get("file-store").isDefined());
            cache.setHasRemoteStore(result.get("remote-store").isDefined());
            cache.setHasJdbcStore(result.get("jdbc-store").isDefined());
            cache.setHasTransaction(result.get("transaction").isDefined());

            if (cache instanceof ReplicatedCache) {
                ReplicatedCache repl = (ReplicatedCache)cache;
                repl.setHasStateTransfer(result.get("state-transfer").isDefined());
            }

            entities.add(cache);
        }

        entityList = sortEntities(entities);

        setIsDefaultCacheAttribute(response.get(RESULT).get("step-2"));

        refreshView(response);
    }

    private void setIsDefaultCacheAttribute(ModelNode response) {
        Set<String> defaultCaches = new HashSet<String>();
        for (ModelNode defaultCache : response.get(RESULT).asList()) {
            List<ModelNode> addressList = defaultCache.get(ADDRESS).asList();
            String cacheContainer = addressList.get(addressList.size() - 1).get("cache-container").asString();
            String cache = defaultCache.get(RESULT).asString();
            defaultCaches.add(cacheContainer + "/" + cache);
        }

        for (T entity : entityList) {
            entity.setDefault(defaultCaches.contains(getName(entity)));
        }
    }

    @Override
    public void onSaveDetails(T entity, Map<String, Object> changedValues, ModelNode... extraSteps) {
        List<ModelNode> stepsList = new ArrayList<ModelNode>();
        stepsList.addAll(Arrays.asList(extraSteps));

        ModelNode setDefaultCacheStep = makeSetDefaultCacheStep(entity, changedValues);
        if ((setDefaultCacheStep != null) && setDefaultCacheStep.get("ERROR").isDefined()) return;
        if (setDefaultCacheStep != null) stepsList.add(setDefaultCacheStep);

        List<ModelNode> singletonEntitySteps = makeSingletonEntitySteps(entity, changedValues);
        stepsList.addAll(singletonEntitySteps);

        super.onSaveDetails(entity, changedValues, stepsList.toArray(new ModelNode[stepsList.size()]));
    }

    private List<ModelNode> makeSingletonEntitySteps(T entity, Map<String, Object> changedValues) {
        List<ModelNode> stepsList = new ArrayList<ModelNode>();

        String addRemoveAttrib = findAddRemoveSingletonAttribute(changedValues);
        if ((addRemoveAttrib != null) && (!(Boolean)changedValues.get(addRemoveAttrib))) { // removing singleton
            stepsList.add(makeAddOrRemoveSingletonStep(entity, singletonName(addRemoveAttrib), REMOVE));
            removeSingletonAttributes(changedValues);
            return stepsList;
        }

        if (addRemoveAttrib != null) { // adding singleton
            changedValues.remove(addRemoveAttrib); // remove the add singleton flag, which is not written to DMR model
            ModelNode addSingleton = makeAddOrRemoveSingletonStep(entity, singletonName(addRemoveAttrib), ADD);

            if (addRemoveAttrib.equals("hasStore")) {
                String storeImplClass = (String)changedValues.remove("storeClass");
                if (storeImplClass == null) {
                    Console.error("Must specify Store Impl Class when defining Store.");
                    removeSingletonAttributes(changedValues);
                    return stepsList;
                }
                addSingleton.get("class").set(storeImplClass);
            }

            if (addRemoveAttrib.equals("hasJdbcStore")) {
                String datasource = (String)changedValues.remove("jdbcStoreDatasource");
                if (datasource == null) {
                    Console.error("Must specify datasource when defining JDBC Store.");
                    removeSingletonAttributes(changedValues);
                    return stepsList;
                }
                addSingleton.get("datasource").set(datasource);
            }

            stepsList.add(addSingleton);
        }

        for (String javaName : changedValues.keySet()) {
            String singletonName = singletonName(javaName);
            if (!singletons.contains(singletonName)) continue;

            ModelNode writeSingletonAttributeStep = getResourceAddress(getName(entity));
            writeSingletonAttributeStep.get(ADDRESS).add(singletonName, singletonName.toUpperCase().replace('-', '_'));
            writeSingletonAttributeStep.get(OP).set(WRITE_ATTRIBUTE_OPERATION);
            writeSingletonAttributeStep.get(NAME).set(attributeName(javaName));

            if (this.formMetaData.findAttribute(javaName).getListType() != null) {
                for (PropertyRecord prop : (List<PropertyRecord>)changedValues.get(javaName)) {
                    writeSingletonAttributeStep.get(VALUE).add(prop.getKey(), prop.getValue());
                }
            } else {
                writeSingletonAttributeStep.get(VALUE).set(changedValues.get(javaName).toString());
            }
            stepsList.add(writeSingletonAttributeStep);
        }

        // Need to remove singleton attributes from normal processing.
        removeSingletonAttributes(changedValues);

        return stepsList;
    }

    // Find the request to add or remove a singleton
    private String findAddRemoveSingletonAttribute(Map<String, Object> changedValues) {
        for (Map.Entry<String, Object> entry : changedValues.entrySet()) {
            if (addRemoveSingletonCheckboxes.contains(entry.getKey())) {
                return entry.getKey();
            }
        }

        return null;
    }

    // If singleton attribute, such as "/transaction/TRANSACTION/foo-attribute",
    // singleton name will be the first name in the path.
    private String singletonName(String javaName) {
        PropertyBinding binding = formMetaData.findAttribute(javaName);
        String[] splitDetypedName = binding.getDetypedName().split("/");
        return splitDetypedName[0];
    }

    // If singleton attribute, such as "/transaction/TRANSACTION/foo-attribute",
    // singleton name will be the last name in the path.
    private String attributeName(String javaName) {
        PropertyBinding binding = formMetaData.findAttribute(javaName);
        String[] splitDetypedName = binding.getDetypedName().split("/");
        return splitDetypedName[splitDetypedName.length - 1];
    }

    // remove all singleton attributes from changedValues so they are not
    // processed by the superclass
    private void removeSingletonAttributes(Map<String, Object> changedValues) {
        List<String> removals = new ArrayList<String>();
        for (String javaName : changedValues.keySet()) {
            String singletonName = singletonName(javaName);
            if (!singletons.contains(singletonName)) continue;
            removals.add(javaName);
        }

        // remove outside the loop to avoid ConcurrentModificationException
        for (String removal : removals) {
            changedValues.remove(removal);
        }
    }

    // add or remove a singleton in the model.  operation param will == ADD or REMOVE.
    private ModelNode makeAddOrRemoveSingletonStep(T entity, String singletonName, String operation) {
        ModelNode addOrRemoveSingletonStep = getResourceAddress(getName(entity));
        addOrRemoveSingletonStep.get(ADDRESS).add(singletonName, singletonName.toUpperCase().replace('-', '_'));
        addOrRemoveSingletonStep.get(OP).set(operation);
        return addOrRemoveSingletonStep;
    }

    // If user asks a cache to become the default for the cache container, set it as default in the cache-container's
    // model.
    private ModelNode makeSetDefaultCacheStep(T entity, Map<String, Object> changedValues) {
        Boolean isDefault = (Boolean)changedValues.remove("default");
        if (isDefault == null) {
            return null;
        }

        ModelNode setDefaultCacheStep = new ModelNode();

        if (entity.isDefault()) {
            Console.error("Can not unset 'Default for cache container'.  Instead, set a different cache to be the default.");
            setDefaultCacheStep.get("ERROR").set("ERROR");
            return setDefaultCacheStep;
        }

        String cacheContainer = entity.getCacheContainer();

        setDefaultCacheStep.get(ADDRESS).set(Baseadress.get()).add("subsystem", "infinispan").add("cache-container", cacheContainer);
        setDefaultCacheStep.get(OP).set(WRITE_ATTRIBUTE_OPERATION);
        setDefaultCacheStep.get(NAME).set("default-cache");
        setDefaultCacheStep.get(VALUE).set(entity.getName());

        return setDefaultCacheStep;
    }

}
