package org.jboss.as.console.client.domain.runtime;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.MainLayoutPresenter;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.domain.events.HostSelectionEvent;
import org.jboss.as.console.client.domain.events.StaleModelEvent;
import org.jboss.as.console.client.domain.model.Host;
import org.jboss.as.console.client.domain.model.HostInformationStore;
import org.jboss.as.console.client.domain.model.ServerInstance;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.state.CurrentHostSelection;
import org.jboss.as.console.client.shared.state.CurrentServerSelection;
import org.jboss.as.console.client.shared.state.ServerSelectionEvent;
import org.jboss.ballroom.client.layout.LHSHighlightEvent;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 11/2/11
 */
public class DomainRuntimePresenter extends Presenter<DomainRuntimePresenter.MyView, DomainRuntimePresenter.MyProxy>
        implements ServerSelectionEvent.ServerSelectionListener,
        HostSelectionEvent.HostSelectionListener,
        StaleModelEvent.StaleModelListener{

    private final PlaceManager placeManager;
    private boolean hasBeenRevealed = false;
    private HostInformationStore hostInfoStore;
    private CurrentServerSelection serverSelection;
    private CurrentHostSelection hostSelection;

    @ProxyCodeSplit
    @NameToken(NameTokens.DomainRuntimePresenter)
    public interface MyProxy extends Proxy<DomainRuntimePresenter>, Place {
    }

    @ContentSlot
    public static final GwtEvent.Type<RevealContentHandler<?>> TYPE_MainContent =
            new GwtEvent.Type<RevealContentHandler<?>>();

    public interface MyView extends View {
        void setPresenter(DomainRuntimePresenter presenter);
        void setHosts(List<Host> hosts);
        void setServer(List<ServerInstance> server);
    }

    @Inject
    public DomainRuntimePresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager,  HostInformationStore hostInfoStore,
            CurrentServerSelection serverSelection, CurrentHostSelection hostSelection) {
        super(eventBus, view, proxy);

        this.placeManager = placeManager;
        this.hostInfoStore = hostInfoStore;
        this.serverSelection = serverSelection;
        this.hostSelection = hostSelection;
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);

        // register for server election events
        getEventBus().addHandler(ServerSelectionEvent.TYPE, this);
        getEventBus().addHandler(HostSelectionEvent.TYPE, this);
        getEventBus().addHandler(StaleModelEvent.TYPE, this);
    }


    @Override
    protected void onReset() {
        super.onReset();
        Console.MODULES.getHeader().highlight(NameTokens.DomainRuntimePresenter);

        // first request, select default contents
        if(!hasBeenRevealed &&
                NameTokens.DomainRuntimePresenter.equals(placeManager.getCurrentPlaceRequest().getNameToken()))
        {
            placeManager.revealRelativePlace(
                    new PlaceRequest(NameTokens.InstancesPresenter)
            );
            hasBeenRevealed = true;


            //  highlight LHS nav
            Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                @Override
                public void execute() {
                    getEventBus().fireEvent(
                            new LHSHighlightEvent(null, Console.CONSTANTS.common_label_serverInstances(), "domain-runtime")

                    );
                }
            });

        }

        loadHostData();

    }

    private void loadHostData() {
        // load host and server data
        hostInfoStore.getHosts(new SimpleCallback<List<Host>>() {
            @Override
            public void onSuccess(final List<Host> hosts) {

                if(!hosts.isEmpty())
                    selectDefaultHost(hosts);


                hostInfoStore.getServerInstances(serverSelection.getHost(), new SimpleCallback<List<ServerInstance>>() {
                    @Override
                    public void onSuccess(List<ServerInstance> server) {

                        if(!serverSelection.hasSetServer())
                        {
                            if(!server.isEmpty())
                            {
                                ServerInstance serverInstance = server.get(0);
                                Console.info("Default server selection: " + serverInstance.getName());
                                serverSelection.setServer(serverInstance);
                            }
                        }

                        // update the LHS in all cases
                        getView().setHosts(hosts);
                        getView().setServer(server);

                    }
                });

            }
        });
    }

    private void selectDefaultHost(List<Host> hosts) {

        if(!serverSelection.hasSetHost())
        {
            String name = hosts.get(0).getName();
            Console.info("Default host selection: "+name);
            serverSelection.setHost(name);
        }

    }

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(getEventBus(), MainLayoutPresenter.TYPE_MainContent, this);
    }

    @Override
    public void onServerSelection(String hostName, ServerInstance server) {

        //System.out.println("** Update state "+hostName+"/"+serverName);

        serverSelection.setHost(hostName);
        serverSelection.setServer(server);
    }

    @Override
    public void onHostSelection(String hostName) {
        hostSelection.setName(hostName);
    }

    @Override
    public void onStaleModel(String modelName) {
        if(StaleModelEvent.SERVER_INSTANCES.equals(modelName))
        {
            loadHostData();
        }
    }
}
