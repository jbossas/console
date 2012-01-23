package org.jboss.as.console.client.shared.subsys.ws;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.domain.model.Host;
import org.jboss.as.console.client.domain.model.HostInformationStore;
import org.jboss.as.console.client.domain.model.ServerInstance;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.shared.state.CurrentServerSelection;
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
public class DomainEndpointStrategy implements EndpointStrategy {

    DispatchAsync dispatcher;
    BeanFactory factory;

    final List<WebServiceEndpoint> endpoints = new ArrayList<WebServiceEndpoint>();
    int numRequests = 0;
    int numResponses = 0;
    private HostInformationStore hostInformationStore;
    private CurrentServerSelection serverSelection;

    @Inject
    public DomainEndpointStrategy(
            DispatchAsync dispatcher,
            BeanFactory factory,
            HostInformationStore hostInformationStore, CurrentServerSelection serverSelection) {

        this.dispatcher = dispatcher;
        this.factory = factory;
        this.hostInformationStore = hostInformationStore;
        this.serverSelection = serverSelection;
    }

    @Override
    public void refreshEndpoints(final AsyncCallback<List<WebServiceEndpoint>> callback) {

        endpoints.clear();
        numRequests=0;
        numResponses=0;


        hostInformationStore.getHosts(new SimpleCallback<List<Host>>() {
            @Override
            public void onSuccess(List<Host> hosts) {

                for(Host host : hosts)
                {
                    endpointsOnHost(host.getName(), callback);
                }
            }
        });

    }

    private void endpointsOnHost(final String host, final AsyncCallback<List<WebServiceEndpoint>> callback) {


        ModelNode operation = new ModelNode();
        operation.get(OP).set(READ_RESOURCE_OPERATION);
        operation.get(ADDRESS).add("host", serverSelection.getHost());
        operation.get(ADDRESS).add("server", serverSelection.getServer().getName());
        operation.get(ADDRESS).add("deployment", "*");
        operation.get(ADDRESS).add("subsystem", "webservices");
        operation.get(ADDRESS).add("endpoint", "*");

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {

            @Override
            public void onSuccess(DMRResponse result) {

                List<WebServiceEndpoint> endpoints = new ArrayList<WebServiceEndpoint>();

                ModelNode response = result.get();

                if(SUCCESS.equals(response.get(OUTCOME).asString())) {

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


    private void addIfNotExists(WebServiceEndpoint endpoint) {

        boolean doesExist = false;
        for(WebServiceEndpoint existing : endpoints) // we don't control the AutoBean hash() or equals() method.
        {
            if(existing.getContext().equals(endpoint.getContext())
                    && existing.getName().equals(endpoint.getName()))
            {
                doesExist = true;
                break;
            }
        }
        if(!doesExist)
            endpoints.add(endpoint);
    }

    private void checkComplete(AsyncCallback<List<WebServiceEndpoint>> callback) {
        if(numResponses==numRequests)
            callback.onSuccess(endpoints);
    }

    private void checkComplete(AsyncCallback<List<WebServiceEndpoint>> callback, Throwable caught) {
        if(numResponses==numRequests)
            callback.onFailure(caught);
        else
            Console.error("Failed to query WebService endpoints ", caught.getMessage());
    }
}
