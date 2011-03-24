package org.jboss.as.console.client.domain.model.impl;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import org.jboss.as.console.client.domain.model.Host;
import org.jboss.as.console.client.domain.model.HostInformationStore;
import org.jboss.as.console.client.domain.model.Server;
import org.jboss.as.console.client.domain.model.ServerInstance;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.dmr.client.ModelDescriptionConstants;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.ModelType;

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

    // TODO: parse full server config resource
    @Override
    public void getServerConfigurations(String host, final AsyncCallback<List<Server>> callback) {
        final ModelNode operation = new ModelNode();
        operation.get(OP).set(READ_CHILDREN_NAMES_OPERATION);
        operation.get(CHILD_TYPE).set("server-config");
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

                List<Server> records = new ArrayList<Server>(payload.size());
                for(int i=0; i<payload.size(); i++)
                {
                    Server record = factory.server().as();
                    record.setName(payload.get(i).asString());
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


    public void loadServerConfig(String host, String serverConfig, final AsyncCallback<Server> callback) {
        final ModelNode operation = new ModelNode();
        operation.get(OP).set(READ_RESOURCE_OPERATION);
        operation.get(ADDRESS).setEmptyList();
        operation.get(RECURSIVE).set(true);
        operation.get(ADDRESS).add("host", host);
        operation.get(ADDRESS).add("server-config", serverConfig);

        dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(DMRResponse result) {

                ModelNode response = ModelNode.fromBase64(result.getResponseText());
                ModelNode payload = response.get("result").asObject();

                Server record = factory.server().as();
                record.setName(payload.get("name").asString());
                record.setGroup(payload.get("group").asString());
                record.setStarted(payload.get("auto-start").asBoolean());

                //System.out.println(payload.toJSONString(false));

                if(payload.get("jvm").isDefined())
                {
                    ModelNode jvm = payload.get("jvm").asObject();
                    record.setJvm(jvm.keys().iterator().next()); // TODO: does blow up easily
                }

                callback.onSuccess(record);
            }

        });
    }

    @Override
    public void getServerInstances(String serverConfig, AsyncCallback<List<ServerInstance>> callback) {

    }
}
