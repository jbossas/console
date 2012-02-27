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
import org.jboss.as.console.client.core.BootstrapContext;
import org.jboss.as.console.client.core.MainLayoutPresenter;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.domain.events.HostSelectionEvent;
import org.jboss.as.console.client.domain.events.StaleModelEvent;
import org.jboss.as.console.client.domain.model.Host;
import org.jboss.as.console.client.domain.model.HostInformationStore;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.domain.model.ServerGroupStore;
import org.jboss.as.console.client.domain.model.ServerInstance;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.model.SubsystemRecord;
import org.jboss.as.console.client.shared.model.SubsystemStore;
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
        StaleModelEvent.StaleModelListener{

    private final PlaceManager placeManager;
    private boolean hasBeenRevealed = false;
    private HostInformationStore hostInfoStore;
    private CurrentServerSelection serverSelection;
    private CurrentHostSelection hostSelection;
    private SubsystemStore subsysStore;
    private BootstrapContext bootstrap;
    private String lastSubPlace;
    private ServerGroupStore serverGroupStore;
    private String previousServerSelection = null;


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

        void setSubsystems(List<SubsystemRecord> result);

        void clearSelection();

        void setSelectedServer(String hostName, ServerInstance server);
    }

    @Inject
    public DomainRuntimePresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager,  HostInformationStore hostInfoStore,
            CurrentServerSelection serverSelection, CurrentHostSelection hostSelection,
            SubsystemStore subsysStore, BootstrapContext bootstrap,
            ServerGroupStore serverGroupStore) {
        super(eventBus, view, proxy);

        this.placeManager = placeManager;
        this.hostInfoStore = hostInfoStore;
        this.serverSelection = serverSelection;
        this.hostSelection = hostSelection;
        this.subsysStore = subsysStore;
        this.bootstrap = bootstrap;
        this.serverGroupStore = serverGroupStore;
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);

        getEventBus().addHandler(StaleModelEvent.TYPE, this);
        getEventBus().addHandler(ServerSelectionEvent.TYPE, DomainRuntimePresenter.this);

    }

    @Override
    protected void onReset() {
        super.onReset();

        Console.MODULES.getHeader().highlight(NameTokens.DomainRuntimePresenter);

        String currentToken = placeManager.getCurrentPlaceRequest().getNameToken();
        if(!currentToken.equals(getProxy().getNameToken()))
        {
            lastSubPlace = currentToken;
        }
        else if(lastSubPlace!=null)
        {
            placeManager.revealPlace(new PlaceRequest(lastSubPlace));
        }

        // first request, select default contents
        if(!hasBeenRevealed &&
                NameTokens.DomainRuntimePresenter.equals(placeManager.getCurrentPlaceRequest().getNameToken()))
        {
            placeManager.revealPlace(new PlaceRequest(NameTokens.InstancesPresenter));
            hasBeenRevealed = true;
        }
        else if(!NameTokens.DomainRuntimePresenter.equals(placeManager.getCurrentPlaceRequest().getNameToken()))
        {
            Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                @Override
                public void execute() {
                    loadHostData();
                }
            });

        }

    }

    private void loadHostData() {

        // load host and server data
        hostInfoStore.getHosts(new SimpleCallback<List<Host>>() {
            @Override
            public void onSuccess(final List<Host> hosts) {

                if(!hosts.isEmpty())
                    selectDefaultHost(hosts);


                final String host = serverSelection.getHost();
                hostInfoStore.getServerInstances(host, new SimpleCallback<List<ServerInstance>>() {
                    @Override
                    public void onSuccess(List<ServerInstance> server) {

                        if(!serverSelection.hasSetServer())
                        {
                            if(!server.isEmpty())
                            {
                                final ServerInstance serverInstance = server.get(0);
                                Console.info("Default server selection: " + serverInstance.getName());

                                Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                                    @Override
                                    public void execute() {

                                        // make this fires
                                        getEventBus().fireEvent(new ServerSelectionEvent(host, serverInstance));
                                    }
                                });
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
    public void onServerSelection(final String hostName, final ServerInstance server) {

        getView().setSelectedServer(hostName, server);

        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                loadSubsystems(server);
            }
        });


    }

    private void loadSubsystems(ServerInstance server) {
        if(!server.getName().equals(previousServerSelection))
        {
            previousServerSelection = server.getName();

            // load subsystems for selected server

            serverGroupStore.loadServerGroup(server.getGroup(), new SimpleCallback<ServerGroupRecord>() {
                @Override
                public void onSuccess(ServerGroupRecord group)
                {
                    subsysStore.loadSubsystems(group.getProfileName(), new SimpleCallback<List<SubsystemRecord>>() {
                        @Override
                        public void onSuccess(List<SubsystemRecord> result) {
                            getView().setSubsystems(result);


                            Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                                @Override
                                public void execute() {
                                    Console.MODULES.getEventBus().fireEvent(
                                            new LHSHighlightEvent(
                                                    placeManager.getCurrentPlaceRequest().getNameToken()
                                            )
                                    );
                                }
                            });
                        }
                    });
                }
            });
        }
    }


    @Override
    public void onStaleModel(String modelName) {
        if(StaleModelEvent.SERVER_INSTANCES.equals(modelName)
                || StaleModelEvent.SERVER_CONFIGURATIONS.equals(modelName))
        {


            // clear current selection
            serverSelection.setHost(null);
            serverSelection.setServer(null);

            getView().clearSelection();

            Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                @Override
                public void execute() {
                    loadHostData();
                }
            });

        }
    }
}
