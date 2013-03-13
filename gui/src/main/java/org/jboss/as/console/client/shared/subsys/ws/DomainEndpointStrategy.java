package org.jboss.as.console.client.shared.subsys.ws;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.jboss.as.console.client.domain.model.HostInformationStore;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.dmr.client.dispatch.DispatchAsync;
import org.jboss.dmr.client.dispatch.impl.DMRAction;
import org.jboss.dmr.client.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.shared.runtime.RuntimeBaseAddress;
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
public class DomainEndpointStrategy extends BaseRegistry implements EndpointStrategy {

    DispatchAsync dispatcher;
    BeanFactory factory;

    @Inject
    public DomainEndpointStrategy(
            DispatchAsync dispatcher,
            BeanFactory factory,
            HostInformationStore hostInformationStore) {

        super(factory, dispatcher);

        this.dispatcher = dispatcher;
        this.factory = factory;
    }

    @Override
    public void refreshEndpoints(final AsyncCallback<List<WebServiceEndpoint>> callback) {
        endpointsOnHost(callback);
    }

    private void endpointsOnHost(final AsyncCallback<List<WebServiceEndpoint>> callback) {

        ModelNode operation = new ModelNode();
        operation.get(ADDRESS).setEmptyList();
        operation.get(OP).set(COMPOSITE);

        List<ModelNode> steps = new ArrayList<ModelNode>();

        ModelNode deploymentsOp = new ModelNode();
        deploymentsOp.get(OP).set(READ_RESOURCE_OPERATION);

        ModelNode addr = RuntimeBaseAddress.get();
        addr.add("deployment", "*");
        addr.add("subsystem", "webservices");
        addr.add("endpoint", "*");
        deploymentsOp.get(ADDRESS).set(addr);

        ModelNode subdeploymentOp = new ModelNode();
        subdeploymentOp.get(OP).set(READ_RESOURCE_OPERATION);
        ModelNode addr2 = RuntimeBaseAddress.get();

        addr2.add("deployment", "*");
        addr2.add("subdeployment", "*");
        addr2.add("subsystem", "webservices");
        addr2.add("endpoint", "*");
        subdeploymentOp.get(ADDRESS).set(addr2);

        steps.add(deploymentsOp);
        steps.add(subdeploymentOp);

        operation.get(STEPS).set(steps);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {

            @Override
            public void onSuccess(DMRResponse result) {

                List<WebServiceEndpoint> endpoints = new ArrayList<WebServiceEndpoint>();


                ModelNode compositeResponse= result.get();


                if(compositeResponse.isFailure())
                {
                    callback.onFailure(new RuntimeException(compositeResponse.getFailureDescription()));
                }
                else
                {
                    ModelNode compositeResult = compositeResponse.get(RESULT);

                    ModelNode mainResponse = compositeResult.get("step-1").asObject();
                    ModelNode subdeploymentResponse = compositeResult.get("step-2").asObject();

                    parseEndpoints(mainResponse, endpoints);
                    parseEndpoints(subdeploymentResponse, endpoints);

                }

                callback.onSuccess(endpoints);

            }
        });
    }
}
