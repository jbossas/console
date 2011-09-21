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

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.ballroom.client.widgets.forms.FormAdapter;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.Property;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * This abstract class knows how to do DMR operations and refresh the view.  It's not simple in the
 * sense that its code is simple.  But rather, it operates on a simple, typical DMR data structure
 * where there is a list of Entities of a single type.
 * 
 * Note that the model type T must implement NamedEntity.
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2011 Red Hat Inc.
 */
public abstract class SimpleEntityToDmrBridge<T> implements EntityToDmrBridge<T> {

    protected DispatchAsync dispatcher;
    protected FrameworkView view;
    protected EntityAttributes attributes;
    protected SubsystemOpFactory opFactory;
    
    protected List<T> entityList = Collections.EMPTY_LIST;
    protected String nameOfLastEdited;
    
    protected Comparator entityComparator = new Comparator<NamedEntity>() {
        @Override
        public int compare(NamedEntity entity1, NamedEntity entity2) {
            return entity1.getName().toLowerCase().compareTo(entity2.getName().toLowerCase());
        }
    };
    
    public SimpleEntityToDmrBridge(DispatchAsync dispatcher, 
                                   FrameworkView view, 
                                   EntityAttributes attributes,
                                   SubsystemOpFactory opFactory) {
        this.dispatcher = dispatcher;
        this.view = view;
        this.attributes = attributes;
        this.opFactory = opFactory;
    }
    
    /**
     * Make a new Entity WITH ITS DEFAULT VALUES SET.
     * 
     * newEntity() must be abstract because it requires a direct method call on the BeanFactory.
     * 
     * @return An AutoBean<T> for the entity.
     */
    @Override
    abstract public T newEntity();
    
    /**
     * Make a new Entity with the given properties read from DMR.
     * 
     * makeEntity() must be abstract because it requires a direct method call on the BeanFactory.
     * 
     * @param props Properties of the Entity as read from DMR.
     * 
     * @return The Entity.
     */
    abstract public T makeEntity(Property props);

    @Override
    public EntityAttributes getEntityAttributes() {
        return this.attributes;
    }

    @Override
    public T findEntity(String name) {
        for (T entity : getEntityList()) {
            NamedEntity namedEntity = (NamedEntity)entity;
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
        return ((NamedEntity)entity).getName();
    }

    @Override
    public String getNameOfLastEdited() {
        return nameOfLastEdited;
    }

    @Override
    public void onAdd(FormAdapter<T> form) {
        NamedEntity entity = (NamedEntity)form.getUpdatedEntity();
        ModelNode operation = opFactory.makeUpdateOp(ADD, entity);
        
        Map<String, Object> changedValues = form.getChangedValues();
        for (AttributeMetadata attrib : attributes.getAllAttributes()) {
            if (changedValues.containsKey(attrib.getBeanPropName())) {
                operation.get(attrib.getDmrName()).set(changedValues.get(attrib.getBeanPropName()).toString());
            }
            else {
                operation.get(attrib.getDmrName()).set(attrib.getDefaultValue().toString());
            }
        }
        
        execute(operation, entity.getName(), "Success: Added " + entity.getName());
    }

    @Override
    public void onEdit() {
        view.setEditingEnabled(true);
    }

    @Override
    public void onRemove(FormAdapter<T> form) {
        NamedEntity entity = (NamedEntity)form.getEditedEntity();
        ModelNode operation = opFactory.makeUpdateOp(REMOVE, entity);
        execute(operation, null, "Success: Removed " + entity.getName());
    }

    @Override
    public void onSaveDetails(FormAdapter<T> form) {
        view.setEditingEnabled(false);
        
        NamedEntity entity = (NamedEntity)form.getEditedEntity();
        String name = entity.getName();
        
        Map<String, Object>changedValues = form.getChangedValues();
        if (changedValues.isEmpty()) return;

        ModelNode batch = new ModelNode();
        batch.get(OP).set(COMPOSITE);
        
        for (Map.Entry<String, Object> entry : changedValues.entrySet()) {
            AttributeMetadata attrib = attributes.findAttribute(entry.getKey());
            String dmrName = attrib.getDmrName();
            Object value = entry.getValue();
            ModelNode operation = opFactory.makeUpdateOp("write-attribute", entity);
            operation.get("name").set(dmrName);
            operation.get("value").set(value.toString());
            batch.get(STEPS).add(operation);
        }

        execute(batch, name, "Success: Updated " + name);
    }

    @Override
    public void loadEntities(String nameEditedOrAdded) {
        this.nameOfLastEdited = nameEditedOrAdded;

        final List<T> entities = new ArrayList<T>();

        ModelNode operation = opFactory.makeReadResource();

        dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {

            @Override
            public void onFailure(Throwable caught) {
                Log.error(Console.CONSTANTS.common_error_unknownError(), caught);
            }

            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = ModelNode.fromBase64(result.getResponseText());
                List<ModelNode> payload = response.get("result").asList();
                ModelNode entityNode = payload.get(0);
                List<ModelNode> entitiyList = entityNode.get(opFactory.getEntityName()).asList(); 
                for (final ModelNode node : entitiyList) {
                    entities.add(makeEntity(node.asPropertyList().get(0)));
                }
                SimpleEntityToDmrBridge.this.entityList = sortEntitties(entities);
                view.refresh();
            }

            private List<T> sortEntitties(List<T> entities) {
                Collections.sort(entities, entityComparator);
                return entities;
            }
        }); 
    }
    
    
    protected void execute(ModelNode operation, final String nameEditedOrAdded, final String successMessage) {
        dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {

            @Override
            public void onFailure(Throwable caught) {
                Log.error(Console.CONSTANTS.common_error_unknownError(), caught);
            }

            @Override
            public void onSuccess(DMRResponse result) {
                Console.info(successMessage);
                loadEntities(nameEditedOrAdded);
            }
        });
    }
}
