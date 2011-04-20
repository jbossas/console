/*
 * JBoss, Home of Professional Open Source
 * Copyright <YEAR> Red Hat Inc. and/or its affiliates and other contributors
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
import com.google.inject.Inject;
import org.jboss.as.console.client.domain.model.Host;
import org.jboss.as.console.client.domain.model.HostInformationStore;
import org.jboss.as.console.client.domain.model.Jvm;
import org.jboss.as.console.client.domain.model.Server;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.domain.model.ServerInstance;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.widgets.forms.ModelNodeAdapter;
import org.jboss.as.console.client.widgets.forms.PropertyBinding;
import org.jboss.as.console.client.widgets.forms.PropertyMetaData;
import org.jboss.dmr.client.ModelDescriptionConstants;
import org.jboss.dmr.client.ModelNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * @author Heiko Braun
 * @date 3/18/11
 */
public class HostInfoStoreImpl implements HostInformationStore {

    private DispatchAsync dispatcher;
    private BeanFactory factory;
    private PropertyMetaData propertyMetaData;

    @Inject
    public HostInfoStoreImpl(DispatchAsync dispatcher, BeanFactory factory, PropertyMetaData propertyMeta) {
        this.dispatcher = dispatcher;
        this.factory = factory;
        this.propertyMetaData = propertyMeta;
    }

    @Override
    public void getHosts(final AsyncCallback<List<Host>> callback) {
        final ModelNode operation = new ModelNode();
        operation.get(OP).set(READ_CHILDREN_NAMES_OPERATION);
        operation.get(CHILD_TYPE).set("host");
        operation.get(ADDRESS).setEmptyList();

        dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = ModelNode.fromBase64(result.getResponseText());
                List<ModelNode> payload = response.get("result").asList();

                List<Host> records = new ArrayList<Host>(payload.size());
                for(int i=0; i<payload.size(); i++)
                {
                    Host record = factory.host().as();
                    record.setName(payload.get(i).asString());
                    records.add(record);
                }

                callback.onSuccess(records);
            }

        });
    }

    @Override
    public void getServerConfigurations(String host, final AsyncCallback<List<Server>> callback) {

        assert host!=null : "Host parameter is null!";

        final ModelNode operation = new ModelNode();
        operation.get(OP).set(READ_CHILDREN_RESOURCES_OPERATION);
        operation.get(CHILD_TYPE).set("server-config");
        operation.get(ADDRESS).setEmptyList();
        operation.get(ADDRESS).add("host", host);
        operation.get(ModelDescriptionConstants.INCLUDE_RUNTIME).set(true);

        dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(DMRResponse result) {

                ModelNode response = ModelNode.fromBase64(result.getResponseText());
                List<ModelNode> payload = response.get("result").asList();

                //System.out.println(response.toString());

                List<Server> records = new ArrayList<Server>(payload.size());
                for(ModelNode item : payload)
                {
                    Server record = factory.server().as();

                    ModelNode server = item.asProperty().getValue();

                    record.setName(server.get("name").asString());
                    record.setGroup(server.get("group").asString());
                    record.setSocketBinding(server.get("socket-binding-group").asString());

                    try {
                        record.setPortOffset(server.get("socket-binding-port-offset").asInt());
                    } catch (IllegalArgumentException e) {
                        //
                    }

                    try {
                        record.setAutoStart(server.get("auto-start").asBoolean());
                    } catch (IllegalArgumentException e) {
                        // TODO: https://issues.jboss.org/browse/JBAS-9163

                    }

                    record.setStarted(server.get("status").asString().equals("STARTED"));

                    /*try {
                        if(server.get("jvm").isDefined())
                        {
                            ModelNode jvmModel = server.get("jvm").asObject();
                            Jvm jvm = factory.jvm().as();
                            jvm.setName(jvmModel.keys().iterator().next()); // TODO: does blow up easily
                            record.setJvm(jvm);
                        }
                    } catch (IllegalArgumentException e) {
                        // ignore
                    }*/

                    records.add(record);
                }

                callback.onSuccess(records);
            }

        });
    }

    public void getVirtualMachines(String host, final AsyncCallback<List<String>> callback) {
        final ModelNode operation = new ModelNode();
        operation.get(OP).set(READ_CHILDREN_NAMES_OPERATION);
        operation.get(CHILD_TYPE).set("jvm");
        operation.get(ADDRESS).setEmptyList();
        operation.get(ADDRESS).add("host", host);

        dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(DMRResponse result) {

                ModelNode response = ModelNode.fromBase64(result.getResponseText());
                List<ModelNode> payload = response.get("result").asList();

                List<String> records = new ArrayList<String>(payload.size());

                for(ModelNode jvm : payload)
                    records.add(jvm.asString());

                callback.onSuccess(records);
            }

        });
    }

    @Override
    public void getServerInstances(final String host, final AsyncCallback<List<ServerInstance>> callback) {

        final List<ServerInstance> instanceList = new ArrayList<ServerInstance>();

        getServerConfigurations(host, new SimpleCallback<List<Server>>() {
            @Override
            public void onSuccess(final List<Server> serverNames) {
                for(final Server handle : serverNames)
                {
                    ServerInstance instance = factory.serverInstance().as();
                    instance.setName(handle.getName());
                    instance.setRunning(handle.isStarted());
                    instance.setServer(handle.getName());

                    instanceList.add(instance);

                }

                callback.onSuccess(instanceList);
            }
        });
    }

    @Override
    public void startServer(final String host, final String configName, boolean startIt, final AsyncCallback<Boolean> callback) {
        final String actualOp = startIt ? "start" : "stop";

        final ModelNode operation = new ModelNode();
        operation.get(OP).set(actualOp);
        operation.get(ADDRESS).add("host", host);
        operation.get(ADDRESS).add("server-config", configName);

        dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = ModelNode.fromBase64(result.getResponseText());
                if(response.get("outcome").asString().equals("success"))
                {
                    callback.onSuccess(Boolean.TRUE);
                }
                else
                {
                    callback.onSuccess(Boolean.FALSE);
                }
            }

            @Override
            public void onFailure(Throwable caught) {
                callback.onSuccess(Boolean.FALSE);
                Log.error("Failed to "+actualOp + " server " +configName);
            }
        });

    }

    @Override
    public void createServerConfig(String host, Server record, final AsyncCallback<Boolean> callback) {
        final ModelNode serverConfig = new ModelNode();
        serverConfig.get(OP).set(ModelDescriptionConstants.ADD);
        serverConfig.get(ADDRESS).add("host", host);
        serverConfig.get(ADDRESS).add(ModelDescriptionConstants.SERVER_CONFIG, record.getName());

        serverConfig.get("name").set(record.getName());
        serverConfig.get("group").set(record.getGroup());
        serverConfig.get("auto-start").set(record.isAutoStart());

        // TODO: can be null?
        if(record.getJvm()!=null)
            serverConfig.get("jvm").set(record.getJvm().getName());
        else
            Log.warn("JVM null for server "+record.getName());

        serverConfig.get("socket-binding-group").set(record.getSocketBinding());
        serverConfig.get("socket-binding-port-offset").set(record.getPortOffset());


        //System.out.println(serverConfig.toJSONString());

        dispatcher.execute(new DMRAction(serverConfig), new AsyncCallback<DMRResponse>() {
            @Override
            public void onFailure(Throwable caught) {
                Log.error("Failed to create server config: " + caught);
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
    public void saveServerConfig(String host, String name, Map<String, Object> changedValues, final AsyncCallback<Boolean> callback) {
        ModelNode proto = new ModelNode();
        proto.get(OP).set(WRITE_ATTRIBUTE_OPERATION);
        proto.get(ADDRESS).add("host", host);
        proto.get(ADDRESS).add(ModelDescriptionConstants.SERVER_CONFIG, name);

        List<PropertyBinding> bindings = propertyMetaData.getBindingsForType(Server.class);
        ModelNode operation  = ModelNodeAdapter.detypedFromChangeset(proto, changedValues, bindings);

        System.out.println(operation.toString());

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
    public void deleteServerConfig(String host, Server record, final AsyncCallback<Boolean> callback) {
        final ModelNode serverConfig = new ModelNode();
        serverConfig.get(OP).set(ModelDescriptionConstants.REMOVE);
        serverConfig.get(ADDRESS).add("host", host);
        serverConfig.get(ADDRESS).add(ModelDescriptionConstants.SERVER_CONFIG, record.getName());


        dispatcher.execute(new DMRAction(serverConfig), new AsyncCallback<DMRResponse>() {
            @Override
            public void onFailure(Throwable caught) {
                Log.error("Failed to create server config: " + caught);
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
}
