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
package org.jboss.as.console.client.shared.viewframework;

import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.widgets.forms.AddressBinding;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.as.console.client.widgets.forms.BeanMetaData;
import org.jboss.as.console.client.widgets.forms.EntityAdapter;
import org.jboss.as.console.client.widgets.forms.FormMetaData;
import org.jboss.as.console.client.widgets.forms.Mutator;
import org.jboss.as.console.client.widgets.forms.PropertyBinding;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.Property;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * This class knows how to do DMR operations and refresh the view.
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2011 Red Hat Inc.
 */
public class EntityToDmrBridgeImpl<T extends NamedEntity> implements EntityToDmrBridge<T> {

    protected ApplicationMetaData propertyMetadata;
    protected BeanMetaData beanMetaData;
    protected AddressBinding address;
    protected Class<?> type;
    protected EntityAdapter<T> entityAdapter;
    protected DispatchAsync dispatcher;
    protected FrameworkView view;
    protected FormMetaData formMetaData;
    protected List<T> entityList = Collections.emptyList();
    protected String nameOfLastEdited;

    protected Comparator entityComparator;

    /**
     * Create a new EntityToDmrBridgeImpl.
     *
     * The view is passed in for receiving callbacks.  It is therefore acceptable to pass in a null view if
     * these callbacks are not desired.
     *
     * @param propertyMetadata The main ApplicationMetaData object.
     * @param type The class that this bridge knows how to handle.
     * @param view The view that will receive FrameworkView callbacks.
     * @param dispatcher The dispatcher for sending commands to the server.
     */
    public EntityToDmrBridgeImpl(ApplicationMetaData propertyMetadata, Class<? extends T> type, FrameworkView view,
                                 DispatchAsync dispatcher) {
        this.propertyMetadata = propertyMetadata;
        this.beanMetaData = propertyMetadata.getBeanMetaData(type);
        this.address = beanMetaData.getAddress();
        this.entityAdapter = new EntityAdapter<T>(type, propertyMetadata);
        this.type = type;
        this.view = view;
        this.dispatcher = dispatcher;
        this.formMetaData = propertyMetadata.getFormMetaData(type);

        entityComparator = new Comparator<T>() {
            @Override
            public int compare(T entity1, T entity2) {
                return getName(entity1).toLowerCase().compareTo(getName(entity2).toLowerCase());
            }
        };
    }

    /**
     * Make a new Entity WITH ITS DEFAULT VALUES SET.
     *
     * @return An AutoBean<T> for the entity.
     */
    @Override
    public T newEntity() {
        T entity = (T) propertyMetadata.getFactory(type).create();
        Mutator mutator = propertyMetadata.getMutator(type);
        for (PropertyBinding prop : beanMetaData.getProperties()) {
            mutator.setValue(entity, prop.getJavaName(), prop.getDefaultValue());
        }

        return entity;
    }

    @Override
    public T findEntity(String name) {
        for (T entity : getEntityList()) {
            if (getName(entity).equals(name)) {
                return entity;
            }
        }

        return null;
    }

    @Override
    public List<T> getEntityList() {
        return this.entityList;
    }

    @Override
    public String getName(T entity) {
        return entity.getName();
    }

    @Override
    public String getNameOfLastEdited() {
        return nameOfLastEdited;
    }

    @Override
    public void onAdd(T entity) {
        String name = getName(entity);
        ModelNode operation = getResourceAddress(name);
        operation.get(OP).set(ADD);
        ModelNode attributes = this.entityAdapter.fromEntity(entity);
        for (Property prop : attributes.asPropertyList()) {
            operation.get(prop.getName()).set(prop.getValue());
        }
        execute(operation, name, Console.MESSAGES.added(name));
    }

    @Override
    public void onEdit() {
        if (view == null) return;
        view.setEditingEnabled(true);
    }

    @Override
    public void onCancel() {
        if (view == null) return;
        view.setEditingEnabled(false);
    }

    @Override
    public void onRemove(T entity) {

        String name = getName(entity);
        ModelNode operation = getResourceAddress(name);
        operation.get(OP).set(REMOVE);

        execute(operation, null, Console.MESSAGES.deleted(name));
    }

    protected ModelNode getResourceAddress(String name) {
        if (address.getNumWildCards() == 0) return address.asResource(Baseadress.get());
        if (address.getNumWildCards() == 1) return address.asResource(Baseadress.get(), name);
        throw new IllegalStateException("This bridge doesn't know how to handle @Address with more than one wildcard.");
    }

    @Override
    public void onSaveDetails(T entity, Map<String, Object> changedValues, ModelNode... extraSteps) {
        if (view != null) view.setEditingEnabled(false);

        String name = getName(entity);

        ModelNode resourceAddress = getResourceAddress(name);

        if (changedValues.isEmpty() && (extraSteps.length == 0)) {
            return;
        }

        // must write back unchanged flattened values
        Mutator mutator = propertyMetadata.getMutator(type);
        for (PropertyBinding prop : beanMetaData.getProperties()) {
            String javaName = prop.getJavaName();
            Object value = mutator.getValue(entity, javaName);
            if (changedValuesContainsFlattenedSibling(prop, changedValues) &&
                    (value != null) && !changedValues.containsKey(javaName)) {
                changedValues.put(javaName, value);
            }
        }

        ModelNode batch = entityAdapter.fromChangeset(changedValues, resourceAddress, extraSteps);

        execute(batch, name, Console.MESSAGES.modified(name));
    }

    private boolean changedValuesContainsFlattenedSibling(PropertyBinding prop, Map<String, Object> changedValues) {
        if (!prop.isFlattened()) return false;

        String detypedName = prop.getDetypedName();
        String attributePath = detypedName.substring(0, detypedName.lastIndexOf("/"));
        for (String javaName : changedValues.keySet()) {
            PropertyBinding binding = formMetaData.findAttribute(javaName);
            if (binding.getDetypedName().startsWith(attributePath)) return true;
        }

        return false;
    }

    @Override
    public void loadEntities(String nameEditedOrAdded) {
        loadEntities(nameEditedOrAdded, Baseadress.get());
    }

    @Override
    public void loadEntities(String nameEditedOrAdded, ModelNode baseAddress) {
        this.nameOfLastEdited = nameEditedOrAdded;

        ModelNode operation = address.asSubresource(baseAddress);
        operation.get(OP).set(READ_CHILDREN_RESOURCES_OPERATION);
        operation.get(RECURSIVE).set(true);
        operation.get(INCLUDE_RUNTIME).set(true);

   //     System.out.println(operation);

        dispatcher.execute(new DMRAction(operation), new DmrCallback() {
            @Override
            public void onDmrSuccess(ModelNode response) {
                onLoadEntitiesSuccess(response);
            }
        });
    }

    protected void onLoadEntitiesSuccess(ModelNode response) {
        List<T> entities = entityAdapter.fromDMRList(response.get(RESULT).asList());
        entityList = sortEntities(entities);
        refreshView(response);
    }

    /**
     * Overriding this method allows a subclass to modify entities before the view is refreshed.
     */
    protected void refreshView(ModelNode response) {
        if (view != null) view.refresh();
    }

    // Not really needed if the table supports sorting...
    protected List<T> sortEntities(List<T> entities) {
        Collections.sort(entities, entityComparator);
        return entities;
    }

    protected void execute(ModelNode operation, final String nameEditedOrAdded, final String successMessage) {
   //     System.out.println("operation=");
   //     System.out.println(operation);
        dispatcher.execute(new DMRAction(operation), new DmrCallback() {

            @Override
            public void onDmrSuccess(ModelNode response) {
          //      System.out.println("execute onDmrSuccess=");
          //      System.out.println(response);
                Console.info(successMessage);
                loadEntities(nameEditedOrAdded);
            }

            @Override
            public void onDmrFailure(ModelNode response) {
                super.onDmrFailure(response);
                loadEntities(nameEditedOrAdded);
            }
        });
    }
}
