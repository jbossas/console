package org.jboss.as.console.client.standalone;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.UseGatekeeper;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.BootstrapContext;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.core.StandaloneGateKeeper;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.dispatch.AsyncCommand;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.jboss.as.console.client.shared.schedule.LongRunningTask;
import org.jboss.as.console.client.shared.state.ReloadEvent;
import org.jboss.as.console.client.shared.state.ReloadState;
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
    private ReloadState reloadState;
    private BootstrapContext bootstrap;

    @ProxyCodeSplit
    @NameToken(NameTokens.StandaloneServerPresenter)
    @UseGatekeeper( StandaloneGateKeeper.class )
    public interface MyProxy extends Proxy<StandaloneServerPresenter>, Place {
    }

    public interface MyView extends View {
        void setPresenter(StandaloneServerPresenter presenter);
        void updateFrom(StandaloneServer server);
        void setReloadRequired(boolean reloadRequired);

        void setEnvironment(List<PropertyRecord> environment);
    }

    @Inject
    public StandaloneServerPresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager, DispatchAsync dispatcher, BeanFactory factory,
            ReloadState reloadState, BootstrapContext bootstrap) {
        super(eventBus, view, proxy);

        this.placeManager = placeManager;
        this.dispatcher = dispatcher;
        this.factory = factory;
        this.reloadState = reloadState;
        this.bootstrap = bootstrap;
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

        ModelNode serverConfig = new ModelNode();
        serverConfig.get(OP).set(READ_RESOURCE_OPERATION);
        serverConfig.get(INCLUDE_RUNTIME).set(true);
        serverConfig.get(ADDRESS).setEmptyList();
        steps.add(serverConfig);

        //:read-children-resources(child-type=socket-binding-group)

        ModelNode fetchExtensions = new ModelNode();
        fetchExtensions.get(OP).set(READ_CHILDREN_RESOURCES_OPERATION);
        fetchExtensions.get(ADDRESS).setEmptyList();
        fetchExtensions.get(CHILD_TYPE).set("extension");
        steps.add(fetchExtensions);

        // /core-service=platform-mbean/type=runtime:read-attribute(name=system-properties)
        ModelNode envProperties = new ModelNode();
        envProperties.get(OP).set(READ_ATTRIBUTE_OPERATION);
        envProperties.get(ADDRESS).add("core-service", "platform-mbean");
        envProperties.get(ADDRESS).add("type", "runtime");
        envProperties.get(NAME).set("system-properties");
        steps.add(envProperties);

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
                        ModelNode serverAttributes = stepResult.get(RESULT).asObject();
                        server.setName(serverAttributes.get("name").asString());
                        server.setReleaseCodename(serverAttributes.get("release-codename").asString());
                        server.setReleaseVersion(serverAttributes.get("release-version").asString());
                        server.setServerState(serverAttributes.get("server-state").asString());
                    }
                    else if(step.getName().equals("step-2"))
                    {
                        // socket-binding response
                        List<Property> model = stepResult.get(RESULT).asPropertyList();
                        List<String> extension = new ArrayList<String>(model.size());
                        for(Property item : model)
                        {
                            extension.add(item.getName());
                        }

                        server.setExtensions(extension);


                    }
                    else if(step.getName().equals("step-3"))
                    {
                        List<Property> properties = stepResult.get(RESULT).asPropertyList();
                        List<PropertyRecord> environment = new ArrayList<PropertyRecord>(properties.size());

                        for(Property prop : properties)
                        {
                            PropertyRecord model = factory.property().as();
                            model.setKey(prop.getName());
                            model.setValue(prop.getValue().asString());

                            environment.add(model);

                        }

                        getView().setEnvironment(environment);
                    }
                }


                getView().updateFrom(server);
                getView().setReloadRequired(reloadState.isStaleModel());

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
                        new LHSHighlightEvent(null, "Configuration", "standalone-runtime")

                );
            }
        });

        loadConfig();

        getView().setReloadRequired(reloadState.isStaleModel());
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

                if(response.isFailure())
                {
                    Console.error(Console.MESSAGES.failed("Reload Server"), response.getFailureDescription());
                }
                else
                {
                    pollState();
                }
            }

            @Override
            public void onFailure(Throwable caught) {
                Console.error(Console.MESSAGES.failed("Reload Server"), caught.getMessage());
            }
        });
    }

    private void pollState() {

        LongRunningTask poll = new LongRunningTask(new AsyncCommand<Boolean>() {
            @Override
            public void execute(final AsyncCallback<Boolean> callback) {
                checkReloadState(callback);
            }
        }, 10);

        // kick of the polling request
        poll.schedule(500);
    }

    /**
     * Simply query the process state attribute to get to the required headers
     */
    public void checkReloadState(final AsyncCallback<Boolean> callback) {

        // :read-attribute(name=process-type)
        final ModelNode operation = new ModelNode();
        operation.get(OP).set(READ_ATTRIBUTE_OPERATION);
        operation.get(NAME).set("server-state");
        operation.get(ADDRESS).setEmptyList();

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {

            @Override
            public void onSuccess(DMRResponse result) {

                ModelNode response = result.get();
                System.out.println(response);

                // TODO: only works when this response changes the reload state
                boolean keepRunning = reloadState.isStaleModel();

                callback.onSuccess(keepRunning);

                if(!keepRunning)
                {

                    // clear state
                    reloadState.reset();

                    Console.info(Console.MESSAGES.successful("Reload Server"));
                    getView().setReloadRequired(reloadState.isStaleModel());
                    getEventBus().fireEvent(new ReloadEvent());
                }
            }
        });
    }
}
