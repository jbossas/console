package org.jboss.as.console.client.standalone;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.standalone.runtime.StandaloneRuntimePresenter;
import org.jboss.ballroom.client.layout.LHSHighlightEvent;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.Property;

import java.util.ArrayList;
import java.util.List;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * @author Heiko Braun
 * @date 6/7/11
 */
public class StandaloneServerPresenter extends Presenter<StandaloneServerPresenter.MyView, StandaloneServerPresenter.MyProxy> {

    private final PlaceManager placeManager;
    private DispatchAsync dispatcher;
    private BeanFactory factory;

    @ProxyCodeSplit
    @NameToken(NameTokens.StandaloneServerPresenter)
    public interface MyProxy extends Proxy<StandaloneServerPresenter>, Place {
    }

    public interface MyView extends View {
        void setPresenter(StandaloneServerPresenter presenter);

        void updateFrom(StandaloneServer server);
    }

    @Inject
    public StandaloneServerPresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager, DispatchAsync dispatcher, BeanFactory factory) {
        super(eventBus, view, proxy);

        this.placeManager = placeManager;
        this.dispatcher = dispatcher;
        this.factory = factory;
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
    }

    private void loadConfig()
    {
        ModelNode operation = new ModelNode();
        operation.get(OP).set(COMPOSITE);
        operation.get(ADDRESS).setEmptyList();

        List<ModelNode> steps = new ArrayList<ModelNode>();

        ModelNode fetchName = new ModelNode();
        fetchName.get(OP).set(READ_ATTRIBUTE_OPERATION);
        fetchName.get(ADDRESS).setEmptyList();
        fetchName.get("name").set("name");
        steps.add(fetchName);

        //:read-children-resources(child-type=socket-binding-group)

        ModelNode fetchSocket = new ModelNode();
        fetchSocket.get(OP).set(READ_CHILDREN_RESOURCES_OPERATION);
        fetchSocket.get(ADDRESS).setEmptyList();
        fetchSocket.get(CHILD_TYPE).set("socket-binding-group");
        steps.add(fetchSocket);

        operation.get(STEPS).set(steps);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();

                List<Property> propertyList = response.get(RESULT).asPropertyList();
                StandaloneServer server = factory.standaloneServer().as();

                for(Property step : propertyList)
                {
                    ModelNode stepResult = step.getValue();
                    if(step.getName().equals("step-1"))
                    {
                        // name response
                        String name = stepResult.get(RESULT).asString();
                        server.setName(name);
                    }
                    else
                    {
                        // socket-binding response
                        List<Property> socketProps = stepResult.get(RESULT).asPropertyList();
                        String socketBindingName = socketProps.get(0).getName();
                        server.setSocketBinding(socketBindingName);

                    }
                }


                getView().updateFrom(server);

            }
        });

    }

    @Override
    protected void onReset() {
        super.onReset();

        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                getEventBus().fireEvent(
                        new LHSHighlightEvent(null, "Server", "standalone-runtime")

                );
            }
        });

        loadConfig();
    }

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(getEventBus(), StandaloneRuntimePresenter.TYPE_MainContent, this);
    }

    public void onReloadServerConfig() {
       final ModelNode operation = new ModelNode();
        operation.get(OP).set("reload");
        operation.get(ADDRESS).setEmptyList();

        dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();
                if(response.get("outcome").asString().equals("success"))
                {
                    Console.info("Success: Reload server");
                }
                else
                {
                    Console.error("Error: Failed to reload server");
                }
            }

            @Override
            public void onFailure(Throwable caught) {
                Console.error("Error: Failed to reload server", caught.getMessage());
            }
        });
    }
}
