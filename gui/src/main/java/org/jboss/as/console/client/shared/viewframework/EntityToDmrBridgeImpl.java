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

import org.jboss.as.console.client.widgets.forms.FormMetaData;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.widgets.forms.AddressBinding;
import org.jboss.as.console.client.widgets.forms.EntityAdapter;
import org.jboss.as.console.client.widgets.forms.Mutator;
import org.jboss.as.console.client.widgets.forms.PropertyBinding;
import org.jboss.as.console.client.widgets.forms.PropertyMetaData;
import org.jboss.ballroom.client.widgets.forms.FormAdapter;
import org.jboss.dmr.client.ModelNode;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * This class knows how to do DMR operations and refresh the view.  
 * 
 * @author Stan Silvert ssilvert@redhat.com (C) 2011 Red Hat Inc.
 */
public class EntityToDmrBridgeImpl<T extends NamedEntity> implements EntityToDmrBridge<T> {

    protected PropertyMetaData propertyMetadata;
    protected AddressBinding address;
    protected Class<?> type;
    protected EntityAdapter<T> entityAdapter;
    protected DispatchAsync dispatcher;
    protected FrameworkView view;
    protected FormMetaData attributes;
    protected List<T> entityList = Collections.EMPTY_LIST;
    protected String nameOfLastEdited;
    
    protected Comparator entityComparator = new Comparator<NamedEntity>() {
        @Override
        public int compare(NamedEntity entity1, NamedEntity entity2) {
            return entity1.getName().toLowerCase().compareTo(entity2.getName().toLowerCase());
        }
    };

    public EntityToDmrBridgeImpl(PropertyMetaData propertyMetadata, Class<? extends T> type, FrameworkView view,
                                 DispatchAsync dispatcher) {
        this.propertyMetadata = propertyMetadata;
        this.address = propertyMetadata.getBeanMetaData(type).getAddress();
        this.entityAdapter = new EntityAdapter<T>(type, propertyMetadata);
        this.type = type;
        this.view = view;
        this.dispatcher = dispatcher;
        this.attributes = propertyMetadata.getBeanMetaData(type).getFormMetaData();
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
        return this.attributes;
    }

    @Override
    public T findEntity(String name) {
        for (T entity : getEntityList()) {
            NamedEntity namedEntity = (NamedEntity) entity;
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
        NamedEntity entity = (NamedEntity) form.getUpdatedEntity();
        String name = entity.getName();
        ModelNode operation = address.asResource(name);
        operation.get(OP).set(ADD);

        Map<String, Object> changedValues = form.getChangedValues();
        for (PropertyBinding attrib : attributes.getBaseAttributes()) {
            if (changedValues.containsKey(attrib.getJavaName())) {
                operation.get(attrib.getDetypedName()).set(changedValues.get(attrib.getJavaName()).toString());
            } else if (attrib.getDefaultValue() != null) {
                operation.get(attrib.getDetypedName()).set(attrib.getDefaultValue().toString());
            }
        }

        execute(operation, name, "Success: Added " + name);
    }

    @Override
    public void onEdit() {
        view.setEditingEnabled(true);
    }

    @Override
    public void onRemove(FormAdapter<T> form) {
        NamedEntity entity = (NamedEntity) form.getEditedEntity();
        String name = entity.getName();
        ModelNode operation = address.asResource(name);
        operation.get(OP).set(REMOVE);

        execute(operation, null, "Success: Removed " + name);
    }

    @Override
    public void onSaveDetails(FormAdapter<T> form) {
        view.setEditingEnabled(false);

        NamedEntity entity = (NamedEntity) form.getEditedEntity();
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
        this.nameOfLastEdited = nameEditedOrAdded;

        ModelNode operation = address.asSubresource(Baseadress.get());
        operation.get(OP).set(READ_CHILDREN_RESOURCES_OPERATION);
        operation.get(RECURSIVE).set(true);

        dispatcher.execute(new DMRAction(operation), new DmrCallback() {

            @Override
            public void onDmrSuccess(ModelNode response) {
                List<T> entities = entityAdapter.fromDMRList(response.get(RESULT).asList());
                EntityToDmrBridgeImpl.this.entityList = sortEntitties(entities);
                view.refresh();
            }

            private List<T> sortEntitties(List<T> entities) {
                Collections.sort(entities, entityComparator);
                return entities;
            }
        });
    }

    protected void execute(ModelNode operation, final String nameEditedOrAdded, final String successMessage) {
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
