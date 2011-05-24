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

package org.jboss.as.console.client.domain.model.impl;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.domain.model.ServerGroupStore;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.shared.jvm.Jvm;
import org.jboss.as.console.client.shared.model.ModelAdapter;
import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.jboss.as.console.client.widgets.forms.PropertyBinding;
import org.jboss.as.console.client.widgets.forms.PropertyMetaData;
import org.jboss.dmr.client.ModelDescriptionConstants;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.Property;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * @author Heiko Braun
 * @date 3/18/11
 */
public class ServerGroupStoreImpl implements ServerGroupStore {

    private DispatchAsync dispatcher;
    private BeanFactory factory;
    private PropertyMetaData propertyMetaData;

    @Inject
    public ServerGroupStoreImpl(DispatchAsync dispatcher, BeanFactory factory, PropertyMetaData propertyMetaData) {
        this.dispatcher = dispatcher;
        this.factory = factory;
        this.propertyMetaData = propertyMetaData;
    }

    @Override
    public void loadServerGroups(final AsyncCallback<List<ServerGroupRecord>> callback) {

        // :read-children-resources(child-type=server-group)

        final ModelNode operation = new ModelNode();
        operation.get(OP).set(ModelDescriptionConstants.READ_CHILDREN_RESOURCES_OPERATION);
        operation.get(RECURSIVE).set(Boolean.TRUE);
        operation.get(CHILD_TYPE).set("server-group");
        operation.get(ModelDescriptionConstants.ADDRESS).setEmptyList();


        dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(DMRResponse result) {

                ModelNode response = ModelNode.fromBase64(result.getResponseText());

                List<ModelNode> propertyList= response.get("result").asList();

                List<ServerGroupRecord> records = new ArrayList<ServerGroupRecord>(propertyList.size());

                for(int i=0; i<propertyList.size(); i++)
                {

                    Property property = propertyList.get(i).asProperty();
                    ServerGroupRecord record = model2ServerGroup(
                            property.getName(),
                            property.getValue()
                    );
                    records.add(record);
                }

                callback.onSuccess(records);
            }
        });

    }

    /**
     * Turns a server group DMR model into a strongly typed entity
     * @param groupName
     * @param model
     * @return
     */
    private ServerGroupRecord model2ServerGroup(String groupName, ModelNode model) {
        ServerGroupRecord record = factory.serverGroup().as();

        record.setGroupName(groupName);
        record.setProfileName(model.get("profile").asString());
        record.setSocketBinding(model.get("socket-binding-group").asString());

        Jvm jvm = ModelAdapter.model2JVM(factory, model);
        record.setJvm(jvm);

        List<PropertyRecord> propertyRecords = ModelAdapter.model2Property(factory, model);
        record.setProperties(propertyRecords);

        return record;
    }


    @Override
    public void loadServerGroup(final String name, final AsyncCallback<ServerGroupRecord> callback) {
        ModelNode op = new ModelNode();
        op.get(ModelDescriptionConstants.OP).set(ModelDescriptionConstants.READ_RESOURCE_OPERATION);
        op.get(ModelDescriptionConstants.ADDRESS).add("server-group", name);
        op.get(RECURSIVE).set(true);
        op.get(INCLUDE_RUNTIME).set(true);

        dispatcher.execute(new DMRAction(op), new AsyncCallback<DMRResponse>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = ModelNode.fromBase64(result.getResponseText());
                ModelNode payload = response.get("result").asObject();

                ServerGroupRecord record = model2ServerGroup(name, payload);

                callback.onSuccess(record);

            }
        });

    }

    public void loadSocketBindingGroupNames(final AsyncCallback<List<String>> callback)
    {
        ModelNode op = new ModelNode();
        op.get(ModelDescriptionConstants.OP).set(ModelDescriptionConstants.READ_CHILDREN_NAMES_OPERATION);
        op.get(ModelDescriptionConstants.OP_ADDR).setEmptyList();
        op.get(ModelDescriptionConstants.CHILD_TYPE).set("socket-binding-group");

        dispatcher.execute(new DMRAction(op), new AsyncCallback<DMRResponse>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = ModelNode.fromBase64(result.getResponseText());
                List<ModelNode> payload = response.get("result").asList();

                List<String> records = new ArrayList<String>(payload.size());
                for(ModelNode binding : payload)
                {
                    records.add(binding.asString());
                }

                callback.onSuccess(records);
            }
        });
    }

    @Override
    public void save(String name, Map<String,Object> changeset , final AsyncCallback<Boolean> callback) {
        ModelNode proto = new ModelNode();
        proto.get(OP).set(WRITE_ATTRIBUTE_OPERATION);
        proto.get(ADDRESS).add(SERVER_GROUP, name);

        List<PropertyBinding> bindings = propertyMetaData.getBindingsForType(ServerGroupRecord.class);
        ModelNode operation  = ModelAdapter.detypedFromChangeset(proto, changeset, bindings);

        dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = ModelNode.fromBase64(result.getResponseText());
                callback.onSuccess(response.get(OUTCOME).asString().equals(SUCCESS));
            }
        });
    }

    @Override
    public void create(ServerGroupRecord record, final AsyncCallback<Boolean> callback) {

        final ModelNode group = new ModelNode();
        group.get(OP).set(ModelDescriptionConstants.ADD);
        group.get(ADDRESS).add(ModelDescriptionConstants.SERVER_GROUP, record.getGroupName());

        group.get("profile").set(record.getProfileName());
        group.get("socket-binding-group").set(record.getSocketBinding());

        dispatcher.execute(new DMRAction(group), new AsyncCallback<DMRResponse>() {
            @Override
            public void onFailure(Throwable caught) {
                Log.error("Failed to create server group: " + caught);
                callback.onSuccess(Boolean.FALSE);
            }

            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = ModelNode.fromBase64(result.getResponseText());
                String outcome = response.get("outcome").asString();

                Boolean wasSuccessful = outcome.equals("success") ? Boolean.TRUE : Boolean.FALSE;
                callback.onSuccess(wasSuccessful);
            }
        });
    }

    @Override
    public void delete(ServerGroupRecord record, final AsyncCallback<Boolean> callback) {
        final ModelNode group = new ModelNode();
        group.get(OP).set(ModelDescriptionConstants.REMOVE);
        group.get(ADDRESS).add(ModelDescriptionConstants.SERVER_GROUP, record.getGroupName());

        dispatcher.execute(new DMRAction(group), new AsyncCallback<DMRResponse>() {
            @Override
            public void onFailure(Throwable caught) {
                Log.error("Failed to remove server group: " + caught);
                callback.onSuccess(Boolean.FALSE);
            }

            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = ModelNode.fromBase64(result.getResponseText());
                String outcome = response.get("outcome").asString();

                Boolean wasSuccessful = outcome.equals("success") ? Boolean.TRUE : Boolean.FALSE;
                callback.onSuccess(wasSuccessful);
            }
        });
    }

    @Override
    public void saveJvm(String groupName, String jvmName, Map<String, Object> changedValues, final AsyncCallback<Boolean> callback) {
        ModelNode proto = new ModelNode();
        proto.get(OP).set(WRITE_ATTRIBUTE_OPERATION);
        proto.get(ADDRESS).add(SERVER_GROUP, groupName);
        proto.get(ADDRESS).add(JVM, jvmName);

        List<PropertyBinding> bindings = propertyMetaData.getBindingsForType(Jvm.class);
        ModelNode operation  = ModelAdapter.detypedFromChangeset(proto, changedValues, bindings);

        dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = ModelNode.fromBase64(result.getResponseText());
                callback.onSuccess(response.get(OUTCOME).asString().equals(SUCCESS));
            }
        });
    }
}
