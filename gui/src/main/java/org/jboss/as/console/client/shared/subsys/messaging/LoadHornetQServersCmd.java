package org.jboss.as.console.client.shared.subsys.messaging;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.dispatch.AsyncCommand;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.dmr.client.ModelNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * Loads a list of hornetq server instance names
 *
 * @author Heiko Braun
 * @date 9/20/11
 */
public class LoadHornetQServersCmd implements AsyncCommand<List<String>> {


    private DispatchAsync dispatcher;

    public LoadHornetQServersCmd(DispatchAsync dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public void execute(final AsyncCallback<List<String>> callback) {
        ModelNode operation = new ModelNode();
        operation.get(OP).set(READ_CHILDREN_NAMES_OPERATION);
        operation.get(ADDRESS).set(Baseadress.get());
        operation.get(ADDRESS).add("subsystem", "messaging");
        operation.get(CHILD_TYPE).set("hornetq-server");

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {

            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();

                if(response.isFailure())
                {
                    Log.error("Failed to load hornetq server", response.getFailureDescription());
                    callback.onSuccess(Collections.EMPTY_LIST);
                }
                else
                {
                    List<ModelNode> payload = response.get(RESULT).asList();
                    List<String> serverNames = new ArrayList<String>(payload.size());
                    for(ModelNode model : payload)
                    {
                        serverNames.add(model.asString());
                    }
                    callback.onSuccess(serverNames);
                }



            }
        });
    }
}
