package org.jboss.as.console.client.shared.subsys.ws;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.ws.model.WebServiceEndpoint;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.Property;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;
import static org.jboss.dmr.client.ModelDescriptionConstants.RESULT;

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
        ModelNode operation = new ModelNode();
        operation.get(OP).set(READ_CHILDREN_RESOURCES_OPERATION);
        operation.get(ADDRESS).set(Baseadress.get());
        operation.get(ADDRESS).add("subsystem", "webservices");
        operation.get(CHILD_TYPE).set("endpoint");

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = ModelNode.fromBase64(result.getResponseText());

                List<WebServiceEndpoint> endpoints = new ArrayList<WebServiceEndpoint>();
                if(response.hasDefined(RESULT))
                {
                    List<Property> props = response.get(RESULT).asPropertyList();
                    for(Property prop : props)
                    {
                        ModelNode value = prop.getValue();
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
