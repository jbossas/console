package org.jboss.as.console.client.shared.general.model;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.dispatch.AsyncCommand;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.dmr.client.ModelNode;

import java.util.ArrayList;
import java.util.List;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * @author Heiko Braun
 * @date 6/7/11
 */
public class LoadSocketBindingsCmd implements AsyncCommand<List<SocketBinding>> {

    private DispatchAsync dispatcher;
    private String groupName;
    private BeanFactory factory;

    public LoadSocketBindingsCmd(DispatchAsync dispatcher, BeanFactory factory, String groupName) {
        this.dispatcher = dispatcher;
        this.groupName = groupName;
        this.factory = factory;
    }

    @Override
    public void execute(final AsyncCallback<List<SocketBinding>> callback) {
        // /socket-binding-group=standard-sockets:read-resource(recursive=true)
        ModelNode operation = new ModelNode();
        operation.get(ADDRESS).add("socket-binding-group", groupName);
        operation.get(OP).set(READ_RESOURCE_OPERATION);
        operation.get(RECURSIVE).set(true);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {

            @Override
            public void onSuccess(DMRResponse result) {

                ModelNode response = ModelNode.fromBase64(result.getResponseText());
                ModelNode payload = response.get("result").asObject();

                List<ModelNode> socketDescriptions= payload.get("socket-binding").asList();

                List<SocketBinding> bindings = new ArrayList<SocketBinding>();
                for(ModelNode socket : socketDescriptions)
                {

                    ModelNode value = socket.asProperty().getValue();

                    SocketBinding sb = factory.socketBinding().as();

                    sb.setName(value.get("name").asString());
                    sb.setGroup(groupName);
                    sb.setPort(value.get("port").asInt());
                    String interfaceValue = value.get("interface").isDefined() ?
                            value.get("interface").asString() : "not set";

                    sb.setInterface(interfaceValue);
                    // TODO: multicast properties
                    sb.setMultiCastAddress("not set");
                    sb.setMultiCastPort(-1);

                    bindings.add(sb);
                }

                callback.onSuccess(bindings);

            }
        });
    }
}
