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
import org.jboss.as.console.client.core.Header;
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
        StaleModelEvent.StaleModelListener,
        HostSelectionEvent.HostSelectionListener {

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
    private Header header;


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
        void setSubsystems(List<SubsystemRecord> result);

        ServerSelectionEvent.ServerSelectionListener getLhsNavigation();
    }

    @Inject
    public DomainRuntimePresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager,  HostInformationStore hostInfoStore,
            CurrentServerSelection serverSelection, CurrentHostSelection hostSelection,
            SubsystemStore subsysStore, BootstrapContext bootstrap,
            ServerGroupStore serverGroupStore, Header header) {
        super(eventBus, view, proxy);

        this.placeManager = placeManager;
        this.hostInfoStore = hostInfoStore;
        this.serverSelection = serverSelection;
        this.hostSelection = hostSelection;
        this.subsysStore = subsysStore;
        this.bootstrap = bootstrap;
        this.serverGroupStore = serverGroupStore;
        this.header = header;
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);

        getEventBus().addHandler(StaleModelEvent.TYPE, this);
        getEventBus().addHandler(ServerSelectionEvent.TYPE, DomainRuntimePresenter.this);
        getEventBus().addHandler(HostSelectionEvent.TYPE, DomainRuntimePresenter.this);
        getEventBus().addHandler(ServerSelectionEvent.TYPE, getView().getLhsNavigation());

        // check if server has been preselected
        /*if(serverSelection.isSet())
        {
            Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                @Override
                public void execute() {
                    System.out.println("Force preselection: "+serverSelection.getServer().getName());
                    getEventBus().fireEvent(
                            new ServerSelectionEvent(serverSelection.getHost(), serverSelection.getServer())
                    );
                }
            });
        } */

    }

    @Override
    protected void onReset() {
        super.onReset();

        header.highlight(NameTokens.DomainRuntimePresenter);

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
        if(!hasBeenRevealed && NameTokens.DomainRuntimePresenter.equals(currentToken))
        {
            placeManager.revealPlace(new PlaceRequest(NameTokens.HostVMMetricPresenter));
            hasBeenRevealed = true;
        }
        else if(!NameTokens.DomainRuntimePresenter.equals(currentToken))
        {
            Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                @Override
                public void execute() {
                    loadHostData();
                }
            });

        }

    }

    @Override
    public void onHostSelection(String hostName) {
        // TODO: necessary ?
    }

    private void loadHostData() {

        // load host and server data
        hostInfoStore.getHosts(new SimpleCallback<List<Host>>() {
            @Override
            public void onSuccess(final List<Host> hosts) {


                if(hosts.isEmpty())
                {
                    Console.warning("No hosts found!");
                    return;
                }

                getView().setHosts(hosts);

            }
        });
    }

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(getEventBus(), MainLayoutPresenter.TYPE_MainContent, this);
    }

    @Override
    public void onServerSelection(final String hostName, final ServerInstance server, ServerSelectionEvent.Source source) {

        //System.out.println("Server selection: "+server.getName() + "("+source+")");

        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                if (server != null) {
                    loadSubsystems(server);
                }
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
                                    Console.getEventBus().fireEvent(
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

            Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                @Override
                public void execute() {
                    loadHostData();
                }
            });

        }
    }
}
