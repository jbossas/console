package org.jboss.as.console.client.shared.general.model;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.dispatch.AsyncCommand;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.widgets.forms.EntityAdapter;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
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
    private BeanFactory factory;
    private ApplicationMetaData metaData;
    private EntityAdapter<SocketBinding> entityAdapter;

    public LoadSocketBindingsCmd(DispatchAsync dispatcher, BeanFactory factory, ApplicationMetaData metaData) {
        this.dispatcher = dispatcher;
        this.factory = factory;
        this.metaData = metaData;
        this.entityAdapter = new EntityAdapter<SocketBinding>(SocketBinding.class, metaData);
    }

    public void execute(final String groupName, final AsyncCallback<List<SocketBinding>> callback) {
        // /socket-binding-group=standard-sockets:read-resource(recursive=true)
        ModelNode operation = new ModelNode();
        operation.get(ADDRESS).add("socket-binding-group", groupName);
        operation.get(OP).set(READ_CHILDREN_RESOURCES_OPERATION);
        operation.get(CHILD_TYPE).set("socket-binding");
        operation.get(RECURSIVE).set(true);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {

            @Override
            public void onSuccess(DMRResponse result) {

                ModelNode response = result.get();
                ModelNode payload = response.get("result").asObject();

                List<ModelNode> socketDescriptions= payload.asList();

                String defaultInterface = payload.get("default-interface").asString();
                List<SocketBinding> bindings = new ArrayList<SocketBinding>();
                for(ModelNode socket : socketDescriptions)
                {

                    ModelNode value = socket.asProperty().getValue();

                    SocketBinding socketBinding = entityAdapter.fromDMR(value);
                    socketBinding.setGroup(groupName);
                    socketBinding.setDefaultInterface(
                            socketBinding.getInterface()!=null ?
                                    socketBinding.getInterface():defaultInterface
                    );
                    socketBinding.setDefaultInterface(defaultInterface);
                    bindings.add(socketBinding);
                }

                callback.onSuccess(bindings);

            }
        });
    }


    @Override
    public void execute(AsyncCallback<List<SocketBinding>> listAsyncCallback) {
        throw new RuntimeException("Use overridden method instead!");
    }
}
