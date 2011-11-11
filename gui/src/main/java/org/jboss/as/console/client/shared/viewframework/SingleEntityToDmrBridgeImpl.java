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

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.widgets.forms.AddressBinding;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.as.console.client.widgets.forms.BeanMetaData;
import org.jboss.as.console.client.widgets.forms.EntityAdapter;
import org.jboss.as.console.client.widgets.forms.FormMetaData;
import org.jboss.ballroom.client.widgets.forms.FormAdapter;
import org.jboss.dmr.client.ModelDescriptionConstants;
import org.jboss.dmr.client.ModelNode;

/**
 * @author David Bosschaert
 */
public class SingleEntityToDmrBridgeImpl<T> implements EntityToDmrBridge<T> {
    protected final AddressBinding address;
    protected final FormMetaData attributes;
    protected final DispatchAsync dispatcher;
    protected final EntityAdapter<T> entityAdapter;
    protected final ApplicationMetaData propertyMetaData;
    protected final Class<? extends T> type;
    protected final FrameworkView view;
    protected T entity;

    public SingleEntityToDmrBridgeImpl(ApplicationMetaData propertyMetaData, Class<? extends T> type,
        FrameworkView view, DispatchAsync dispatcher) {
        BeanMetaData beanMetaData = propertyMetaData.getBeanMetaData(type);

        this.propertyMetaData = propertyMetaData;
        this.address = beanMetaData.getAddress();
        this.attributes = propertyMetaData.getFormMetaData(type);
        this.entityAdapter = new EntityAdapter<T>(type, propertyMetaData);
        this.type = type;
        this.view = view;
        this.dispatcher = dispatcher;
    }

    @Override
    public FormMetaData getEntityAttributes() {
        return attributes;
    }

    @Override
    public void loadEntities(String nameEditedOrAdded) {
        ModelNode operation = address.asResource(Baseadress.get());
        operation.get(ModelDescriptionConstants.OP).set(ModelDescriptionConstants.READ_RESOURCE_OPERATION);
        operation.get(ModelDescriptionConstants.INCLUDE_RUNTIME).set(true);

        dispatcher.execute(new DMRAction(operation), new DmrCallback() {
            @Override
            public void onDmrSuccess(ModelNode response) {
                entity = entityAdapter.fromDMR(response.get(ModelDescriptionConstants.RESULT));
                view.refresh();
            }
        });
    }

    @Override
    public String getNameOfLastEdited() {
        return type.getName();
    }

    @Override
    public List<T> getEntityList() {
        return Collections.singletonList(entity);
    }

    @Override
    public T findEntity(String name) {
        return entity;
    }

    @Override
    public void onAdd(FormAdapter<T> form) {
        throw new UnsupportedOperationException();
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
    public void onSaveDetails(FormAdapter<T> form) {
        view.setEditingEnabled(false);

        ModelNode resourceAddress = address.asResource(Baseadress.get());
        Map<String, Object> changedValues = form.getChangedValues();
        if (changedValues.isEmpty())
            return;

        ModelNode batch = entityAdapter.fromChangeset(changedValues, resourceAddress);
        dispatcher.execute(new DMRAction(batch), new DmrCallback() {
            @Override
            public void onDmrSuccess(ModelNode response) {
                Console.info("Success: updated Timer Service");
                loadEntities(null);
            }

            @Override
            public void onDmrFailure(ModelNode response) {
                super.onDmrFailure(response);
                loadEntities(null);
            }
        });
    }

    @Override
    public void onRemove(FormAdapter<T> form) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getName(T entity) {
        return type.getName();
    }

    @Override
    public T newEntity() {
        throw new UnsupportedOperationException();
    }
}
