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

import static org.jboss.dmr.client.ModelDescriptionConstants.ADD;
import static org.jboss.dmr.client.ModelDescriptionConstants.INCLUDE_RUNTIME;
import static org.jboss.dmr.client.ModelDescriptionConstants.OP;
import static org.jboss.dmr.client.ModelDescriptionConstants.READ_CHILDREN_RESOURCES_OPERATION;
import static org.jboss.dmr.client.ModelDescriptionConstants.RECURSIVE;
import static org.jboss.dmr.client.ModelDescriptionConstants.REMOVE;
import static org.jboss.dmr.client.ModelDescriptionConstants.RESULT;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.widgets.forms.AddressBinding;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.as.console.client.widgets.forms.EntityAdapter;
import org.jboss.as.console.client.widgets.forms.FormMetaData;
import org.jboss.as.console.client.widgets.forms.Mutator;
import org.jboss.as.console.client.widgets.forms.PropertyBinding;
import org.jboss.ballroom.client.widgets.forms.FormAdapter;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.Property;

/**
 * This class knows how to do DMR operations and refresh the view.
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2011 Red Hat Inc.
 */
public class EntityToDmrBridgeImpl<T extends NamedEntity> implements EntityToDmrBridge<T> {

    protected ApplicationMetaData propertyMetadata;
    protected AddressBinding address;
    protected Class<?> type;
    protected EntityAdapter<T> entityAdapter;
    protected DispatchAsync dispatcher;
    protected FrameworkView view;
    protected FormMetaData formMetaData;
    protected List<T> entityList = Collections.emptyList();
    protected String nameOfLastEdited;

    protected Comparator entityComparator = new Comparator<NamedEntity>() {
        @Override
        public int compare(NamedEntity entity1, NamedEntity entity2) {
            return entity1.getName().toLowerCase().compareTo(entity2.getName().toLowerCase());
        }
    };

    public EntityToDmrBridgeImpl(ApplicationMetaData propertyMetadata, Class<? extends T> type, FrameworkView view,
                                 DispatchAsync dispatcher) {
        this.propertyMetadata = propertyMetadata;
        this.address = propertyMetadata.getBeanMetaData(type).getAddress();
        this.entityAdapter = new EntityAdapter<T>(type, propertyMetadata);
        this.type = type;
        this.view = view;
        this.dispatcher = dispatcher;
        this.formMetaData = propertyMetadata.getFormMetaData(type);
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
        for (PropertyBinding prop : propertyMetadata.getBindingsForType(type)) {
            mutator.setValue(entity, prop.getJavaName(), prop.getDefaultValue());
        }

        return entity;
    }

    @Override
    public FormMetaData getEntityAttributes() {
        return this.formMetaData;
    }

    @Override
    public T findEntity(String name) {
        for (T entity : getEntityList()) {
            NamedEntity namedEntity = entity;
            if (namedEntity.getName().equals(name)) {
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
        return ((NamedEntity) entity).getName();
    }

    @Override
    public String getNameOfLastEdited() {
        return nameOfLastEdited;
    }

    @Override
    public void onAdd(FormAdapter<T> form) {
        NamedEntity entity = form.getUpdatedEntity();
        String name = entity.getName();
        ModelNode operation = address.asResource(name);
        operation.get(OP).set(ADD);
        ModelNode attributes = this.entityAdapter.fromEntity(form.getUpdatedEntity());
        for (Property prop : attributes.asPropertyList()) {
            operation.get(prop.getName()).set(prop.getValue());
        }
        execute(operation, name, "Success: Added " + name);
    }

    @Override
    public void onEdit() {
        view.setEditingEnabled(true);
    }

    @Override
    public void onCancel() {
        view.setEditingEnabled(false);
    }

    @Override
    public void onRemove(FormAdapter<T> form) {
        NamedEntity entity = form.getEditedEntity();
        String name = entity.getName();
        ModelNode operation = address.asResource(name);
        operation.get(OP).set(REMOVE);

        execute(operation, null, "Success: Removed " + name);
    }

    @Override
    public void onSaveDetails(FormAdapter<T> form) {
        view.setEditingEnabled(false);

        NamedEntity entity = form.getEditedEntity();
        String name = entity.getName();

        ModelNode resourceAddress = address.asResource(name);

        Map<String, Object> changedValues = form.getChangedValues();
        if (changedValues.isEmpty()) {
            return;
        }

        ModelNode batch = entityAdapter.fromChangeset(changedValues, resourceAddress);

        execute(batch, name, "Success: Updated " + name);
    }

    @Override
    public void loadEntities(String nameEditedOrAdded) {
        loadEntities(nameEditedOrAdded, Baseadress.get());
    }

    void loadEntities(String nameEditedOrAdded, ModelNode baseAddress) {
        this.nameOfLastEdited = nameEditedOrAdded;

        ModelNode operation = address.asSubresource(baseAddress);
        operation.get(OP).set(READ_CHILDREN_RESOURCES_OPERATION);

        if (formMetaData.isFlattened()) {
            operation.get(RECURSIVE).set(true);
        } else {
            // Runtime information is only available in the DMR on non-recursive reads
            operation.get(INCLUDE_RUNTIME).set(true);
        }

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
        view.refresh();
    }

    // Not really needed if the table supports sorting...
    protected List<T> sortEntities(List<T> entities) {
        Collections.sort(entities, entityComparator);
        return entities;
    }

    protected void execute(ModelNode operation, final String nameEditedOrAdded, final String successMessage) {
        //System.out.println("execute:");
        //System.out.println(operation.toString());
        dispatcher.execute(new DMRAction(operation), new DmrCallback() {

            @Override
            public void onDmrSuccess(ModelNode response) {
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
