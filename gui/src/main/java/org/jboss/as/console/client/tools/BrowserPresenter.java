package org.jboss.as.console.client.tools;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.Property;

import java.util.ArrayList;
import java.util.List;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * @author Heiko Braun
 * @date 6/15/12
 */
public class BrowserPresenter extends Presenter<BrowserPresenter.MyView, BrowserPresenter.MyProxy> {

    private final PlaceManager placeManager;
    private DispatchAsync dispatcher;

    @ProxyCodeSplit
    @NameToken(NameTokens.DMRBrowser)
    public interface MyProxy extends Proxy<BrowserPresenter>, Place {
    }

    public interface MyView extends View {
        void setPresenter(BrowserPresenter presenter);
        void setDescription(CompositeDescription desc);

        void setChildTypes(ModelNode address, List<ModelNode> childTypes);
    }

    @Inject
    public BrowserPresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager, DispatchAsync dispatcher) {
        super(eventBus, view, proxy);

        this.placeManager = placeManager;
        this.dispatcher = dispatcher;
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
    }


    @Override
    protected void onReset() {
        super.onReset();

        loadDescription(new ModelNode().setEmptyList());
    }

    public void loadDescription(final ModelNode address) {

        ModelNode operation = new ModelNode();
        operation.get(ADDRESS).setEmptyList();
        operation.get(OP).set(COMPOSITE);

        List<ModelNode> steps = new ArrayList<ModelNode>();

        ModelNode descriptionOp = new ModelNode();
        descriptionOp.get(ADDRESS).set(address);
        descriptionOp.get(OP).set(READ_RESOURCE_DESCRIPTION_OPERATION);
        descriptionOp.get("recursive-depth").set(2);
        steps.add(descriptionOp);

        final List<ModelNode> path = address.asList();
        if(!path.isEmpty())
        {

            ModelNode childTypeOp = new ModelNode();
            childTypeOp.get(ADDRESS).setEmptyList();  // TODO
            childTypeOp.get(OP).set(READ_CHILDREN_NAMES_OPERATION);
            childTypeOp.get(CHILD_TYPE).set(address.get(path.size()-1).asProperty().getName());
            steps.add(childTypeOp);
        }

        operation.get(STEPS).set(steps);


        //System.out.println(operation);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse dmrResponse) {
                final ModelNode response = dmrResponse.get();

                System.out.println("---");
                System.out.println(response);
                System.out.println("---");

                List<Property> propertyList = response.get(RESULT).asPropertyList();

                CompositeDescription desc = new CompositeDescription();
                desc.setAddress(address);

                for(Property step : propertyList)
                {
                    ModelNode stepResult = step.getValue();
                    if(step.getName().equals("step-1"))
                    {
                        if(stepResult.isDefined())
                            desc.setDescription(stepResult.get(RESULT).asObject());
                        else
                            desc.setDescription(new ModelNode());
                    }
                    else if (step.getName().equals("step-2"))
                    {
                        desc.setChildNames(stepResult.get(RESULT).asList());
                    }

                    getView().setDescription(desc);
                }

            }
        });
    }

    public void loadChildTypes(final ModelNode address) {
        ModelNode operation = new ModelNode();
        operation.get(ADDRESS).set(address);
        operation.get(OP).set(READ_CHILDREN_TYPES_OPERATION);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse dmrResponse) {
                final ModelNode response = dmrResponse.get();
                //System.out.println(response);
                //getView().setChildTypes(address, response.get(RESULT).asList());
            }
        });
    }


    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(getEventBus(), ToolsPresenter.TYPE_MainContent, this);
    }
}
