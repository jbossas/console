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

import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.infinispan.model.LocalCache;
import org.jboss.as.console.client.shared.viewframework.DmrCallback;
import org.jboss.as.console.client.shared.viewframework.EntityToDmrBridgeImpl;
import org.jboss.as.console.client.shared.viewframework.FrameworkView;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
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
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2011 Red Hat Inc.
 */
public class CacheEntityToDmrBridge<T extends LocalCache> extends EntityToDmrBridgeImpl<T> {

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
        readAttrOp.get(ADDRESS).add(Baseadress.get()).add("subsystem", "infinispan").add("cache-container", "*");
        readAttrOp.get(OP).set(READ_ATTRIBUTE_OPERATION);
        readAttrOp.get(NAME).set("default-cache");
        steps.get(STEPS).add(readAttrOp);

    //    System.out.println("load entities for " + this.type.getName());
    //    System.out.println(steps.toString());

        dispatcher.execute(new DMRAction(steps), new DmrCallback() {
            @Override
            public void onDmrSuccess(ModelNode response) {
         //      System.out.println("response=");
         //      System.out.println(response.toString());
               onLoadEntitiesSuccess(response);
            }
        });

    }

    @Override
    protected void onLoadEntitiesSuccess(ModelNode response) {
        List<T> entities = new ArrayList<T>();
        for (ModelNode entity : response.get(RESULT).get("step-1").get(RESULT).asList()) {
            for (Property addressProp : entity.get(ADDRESS).asPropertyList()) {
                entity.get(RESULT).get(addressProp.getName()).set(addressProp.getValue());
            }

            entities.add(entityAdapter.fromDMR(entity.get(RESULT)));
        }

        entityList = sortEntities(entities);

        setIsDefaultCacheAttribute(response.get(RESULT).get("step-2"));

        if (view != null) view.refresh();
    }

    private void setIsDefaultCacheAttribute(ModelNode response) {
        Set<String> defaultCaches = new HashSet<String>();
        for (ModelNode defaultCache : response.get(RESULT).asList()) {
            String cacheContainer = defaultCache.get(ADDRESS).asList().get(1).get("cache-container").asString();
            String cache = defaultCache.get(RESULT).asString();
            defaultCaches.add(cacheContainer + "/" + cache);
        }

        for (T entity : entityList) {
            entity.setDefault(defaultCaches.contains(getName(entity)));
        }
    }

    @Override
    public void onSaveDetails(T entity, Map<String, Object> changedValues, ModelNode... extraSteps) {
        Boolean isDefault = (Boolean)changedValues.remove("default");
        if (isDefault == null) {
            super.onSaveDetails(entity, changedValues, extraSteps);
            return;
        }

        String cacheContainer = entity.getCacheContainer();
        ModelNode setDefaultCacheStep = new ModelNode();
        setDefaultCacheStep.get(ADDRESS).add(Baseadress.get()).add("subsystem", "infinispan").add("cache-container", cacheContainer);
        setDefaultCacheStep.get(OP).set(WRITE_ATTRIBUTE_OPERATION);
        setDefaultCacheStep.get(NAME).set("default-cache");
        setDefaultCacheStep.get(VALUE).set(entity.getName());

        List<ModelNode> stepsList = new ArrayList<ModelNode>();
        stepsList.addAll(Arrays.asList(extraSteps));
        stepsList.add(setDefaultCacheStep);
        super.onSaveDetails(entity, changedValues, stepsList.toArray(new ModelNode[stepsList.size()]));
    }

}
