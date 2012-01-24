package org.jboss.as.console.client.shared.subsys.ws;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.shared.subsys.ws.model.WebServiceEndpoint;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.Property;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * @author Heiko Braun
 * @date 6/10/11
 */
public class StandaloneEndpointStrategy extends BaseRegistry implements EndpointStrategy {


    @Inject
    public StandaloneEndpointStrategy(DispatchAsync dispatcher, BeanFactory factory) {
        super(factory, dispatcher);
    }

    @Override
    public void refreshEndpoints(final AsyncCallback<List<WebServiceEndpoint>> callback) {

        // /deployment="*"/subsystem=webservices/endpoint="*":read-resource

        ModelNode operation = new ModelNode();
        operation.get(ADDRESS).setEmptyList();
        operation.get(OP).set(COMPOSITE);

        List<ModelNode> steps = new ArrayList<ModelNode>();

        ModelNode deploymentsOp = new ModelNode();
        deploymentsOp.get(OP).set(READ_RESOURCE_OPERATION);
        deploymentsOp.get(ADDRESS).add("deployment", "*");
        deploymentsOp.get(ADDRESS).add("subsystem", "webservices");
        deploymentsOp.get(ADDRESS).add("endpoint", "*");

        ModelNode subdeploymentOp = new ModelNode();
        subdeploymentOp.get(OP).set(READ_RESOURCE_OPERATION);
        subdeploymentOp.get(ADDRESS).add("deployment", "*");
        subdeploymentOp.get(ADDRESS).add("subdeployment", "*");
        subdeploymentOp.get(ADDRESS).add("subsystem", "webservices");
        subdeploymentOp.get(ADDRESS).add("endpoint", "*");

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
                    ModelNode compositeResult = compositeResponse.get(RESULT).asObject();

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
