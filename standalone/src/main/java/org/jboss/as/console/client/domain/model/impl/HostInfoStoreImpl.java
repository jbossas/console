package org.jboss.as.console.client.domain.model.impl;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import org.jboss.as.console.client.domain.model.Host;
import org.jboss.as.console.client.domain.model.HostInformationStore;
import org.jboss.as.console.client.domain.model.Server;
import org.jboss.as.console.client.domain.model.ServerInstance;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.dmr.client.ModelDescriptionConstants;
import org.jboss.dmr.client.ModelNode;

import java.util.ArrayList;
import java.util.List;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * @author Heiko Braun
 * @date 3/18/11
 */
public class HostInfoStoreImpl implements HostInformationStore {

    private DispatchAsync dispatcher;
    private BeanFactory factory = GWT.create(BeanFactory.class);

    @Inject
    public HostInfoStoreImpl(DispatchAsync dispatcher) {
        this.dispatcher = dispatcher;
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

                    try {
                        if(server.get("jvm").isDefined())
                        {
                            ModelNode jvm = server.get("jvm").asObject();
                            record.setJvm(jvm.keys().iterator().next()); // TODO: does blow up easily
                        }
                    } catch (IllegalArgumentException e) {
                        // ignore
                    }
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
        serverConfig.get("socket-binding-port-offset").set(record.getPortOffset());

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
    public void saveServerConfig(String host, Server updatedEntity) {
        Log.warn("Save server config not implemented yet!");
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
