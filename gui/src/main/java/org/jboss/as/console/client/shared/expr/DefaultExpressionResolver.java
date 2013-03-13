package org.jboss.as.console.client.shared.expr;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.BootstrapContext;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.dmr.client.dispatch.DispatchAsync;
import org.jboss.dmr.client.dispatch.impl.DMRAction;
import org.jboss.dmr.client.dispatch.impl.DMRResponse;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.Property;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * See https://issues.jboss.org/browse/AS7-2139
 * @author Heiko Braun
 * @date 10/4/11
 */
public class DefaultExpressionResolver extends ExpressionResolver {

    private DispatchAsync dispatcher;
    private boolean isStandalone;

    @Inject
    public DefaultExpressionResolver(DispatchAsync dispatcher, BootstrapContext bootstrap) {
        this.dispatcher = dispatcher;
        this.isStandalone = bootstrap.isStandalone();
    }

    @Override
    public void resolveValue(final Expression expr, final AsyncCallback<Map<String,String>> callback) {


        ModelNode operation = new ModelNode();
        operation.get(ADDRESS).setEmptyList();
        String opName = isStandalone ?  "resolve-expression" : "resolve-expression-on-domain";
        operation.get(OP).set(opName);
        operation.get("expression").set(expr.toString());

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse dmrResponse) {
                ModelNode response = dmrResponse.get();
                if(response.isFailure())
                {
                    Console.error("Failed to resolve expression", response.getFailureDescription());
                }
                else
                {
                    parseResponse(response, callback);
                }
            }
        });

    }

    /**
     * Distinguish domain and standalone response values
     *
     * @param response
     * @param callback
     */
    private void parseResponse(ModelNode response, AsyncCallback<Map<String,String>> callback) {

        //System.out.println(response.toString());

        Map<String, String> serverValues = new HashMap<String,String>();

        if(isStandalone)
        {
            serverValues.put("Standalone Server", response.get(RESULT).asString());
        }
        else if(response.hasDefined("server-groups"))
        {

            List<Property> groups = response.get("server-groups").asPropertyList();
            for(Property serverGroup : groups)
            {
                List<Property> hosts = serverGroup.getValue().get("host").asPropertyList();
                for(Property host : hosts)
                {
                    List<Property> servers = host.getValue().asPropertyList();
                    for(Property server : servers)
                    {
                        serverValues.put(server.getName(),
                                server.getValue().get("response").get("result").asString()
                        );
                    }
                }
            }
        }

        callback.onSuccess(serverValues);
    }
}
