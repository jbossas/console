package org.jboss.as.console.client.shared.subsys.ws;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.shared.subsys.ws.model.WebServiceEndpoint;
import org.jboss.dmr.client.ModelNode;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * @author Heiko Braun
 * @date 6/10/11
 */
public class StandaloneEndpointStrategy implements EndpointStrategy {


    DispatchAsync dispatcher;
    BeanFactory factory;

    @Inject
    public StandaloneEndpointStrategy(DispatchAsync dispatcher, BeanFactory factory) {
        this.dispatcher = dispatcher;
        this.factory = factory;
    }

    @Override
    public void refreshEndpoints(final AsyncCallback<List<WebServiceEndpoint>> callback) {

        // /deployment="*"/subsystem=webservices/endpoint="*":read-resource

        ModelNode operation = new ModelNode();
        operation.get(OP).set(READ_RESOURCE_OPERATION);
        operation.get(ADDRESS).add("deployment", "*");
        operation.get(ADDRESS).add("subsystem", "webservices");
        operation.get(ADDRESS).add("endpoint", "*");

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();

                List<WebServiceEndpoint> endpoints = new ArrayList<WebServiceEndpoint>();
                if(response.hasDefined(RESULT))
                {
                    List<ModelNode> modelNodes = response.get(RESULT).asList();

                    for(ModelNode node : modelNodes)
                    {
                        ModelNode value = node.get(RESULT).asObject();
                        WebServiceEndpoint endpoint = factory.webServiceEndpoint().as();
                        endpoint.setName(value.get("name").asString());
                        endpoint.setClassName(value.get("class").asString());
                        endpoint.setContext(value.get("context").asString());
                        endpoint.setType(value.get("type").asString());
                        endpoint.setWsdl(value.get("wsdl-url").asString());

                        endpoints.add(endpoint);
                    }
                }

                callback.onSuccess(endpoints);
            }
        });
    }
}
