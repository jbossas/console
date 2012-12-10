package org.jboss.as.console.client.domain.runtime;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
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
import org.jboss.as.console.client.domain.events.StaleModelEvent;
import org.jboss.as.console.client.domain.model.Host;
import org.jboss.as.console.client.domain.model.HostInformationStore;
import org.jboss.as.console.client.domain.model.Server;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.domain.model.ServerGroupStore;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.model.SubsystemRecord;
import org.jboss.as.console.client.shared.model.SubsystemStore;
import org.jboss.as.console.client.shared.state.CurrentServerSelection;
import org.jboss.as.console.client.shared.state.DomainEntityManager;
import org.jboss.as.console.client.shared.state.HostList;
import org.jboss.as.console.client.shared.state.HostSelectionChanged;
import org.jboss.as.console.client.shared.state.ServerSelectionChanged;
import org.jboss.ballroom.client.layout.LHSHighlightEvent;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 11/2/11
 */
public class DomainRuntimePresenter extends Presenter<DomainRuntimePresenter.MyView, DomainRuntimePresenter.MyProxy>
        implements StaleModelEvent.StaleModelListener,
        ServerSelectionChanged.ChangeListener,
        HostSelectionChanged.ChangeListener  {

    private final PlaceManager placeManager;
    private boolean hasBeenRevealed = false;
    private HostInformationStore hostInfoStore;
    private CurrentServerSelection serverSelection;
    private SubsystemStore subsysStore;
    private BootstrapContext bootstrap;
    private ServerGroupStore serverGroupStore;
    private String previousServerSelection = null;
    private Header header;
    private PlaceRequest lastSubRequest = null;
    private final DomainEntityManager domainManager;


    @ProxyCodeSplit
    @NameToken(NameTokens.DomainRuntimePresenter)
    public interface MyProxy extends Proxy<DomainRuntimePresenter>, Place {
    }

    @ContentSlot
    public static final GwtEvent.Type<RevealContentHandler<?>> TYPE_MainContent =
            new GwtEvent.Type<RevealContentHandler<?>>();

    public interface MyView extends View {
        void setPresenter(DomainRuntimePresenter presenter);
        void setHosts(HostList hosts);
        void setSubsystems(List<SubsystemRecord> result);
        void resetHostSelection();
    }

    @Inject
    public DomainRuntimePresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager,  HostInformationStore hostInfoStore,
            DomainEntityManager domainManager,
            SubsystemStore subsysStore, BootstrapContext bootstrap,
            ServerGroupStore serverGroupStore, Header header) {
        super(eventBus, view, proxy);

        this.placeManager = placeManager;
        this.hostInfoStore = hostInfoStore;
        this.domainManager = domainManager;
        this.subsysStore = subsysStore;
        this.bootstrap = bootstrap;
        this.serverGroupStore = serverGroupStore;
        this.header = header;
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);

        getEventBus().addHandler(HostSelectionChanged.TYPE, this);
        getEventBus().addHandler(ServerSelectionChanged.TYPE, this);
        getEventBus().addHandler(StaleModelEvent.TYPE, this);

    }

    @Override
    protected void onReset() {
        super.onReset();

        header.highlight(NameTokens.DomainRuntimePresenter);

        String currentToken = placeManager.getCurrentPlaceRequest().getNameToken();
        if(!currentToken.equals(getProxy().getNameToken()))
        {
            lastSubRequest = placeManager.getCurrentPlaceRequest();
        }
        else if(lastSubRequest!=null)
        {
            placeManager.revealPlace(lastSubRequest);
        }

        // first request, select default contents
        if(!hasBeenRevealed && NameTokens.DomainRuntimePresenter.equals(currentToken))
        {
            if (lastSubRequest != null)
            {
                placeManager.revealPlace(lastSubRequest);
            }
            else
            {
                placeManager.revealPlace(new PlaceRequest(NameTokens.Topology));
            }
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


    private void loadHostData() {

        // load host and server data
        domainManager.getHosts(new SimpleCallback<HostList>() {
            @Override
            public void onSuccess(final HostList hosts) {

                getView().setHosts(hosts);

            }
        });
    }

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(this, MainLayoutPresenter.TYPE_MainContent, this);
    }

    @Override
    public void onServerSelectionChanged() {
        System.out.println("serverSelection changed");
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                loadSubsystems();
            }
        });

    }

    @Override
    public void onHostSelectionChanged() {

    }

    private void loadSubsystems() {

        // load subsystems for selected server

        hostInfoStore.getServerConfiguration(

                domainManager.getSelectedHost(), domainManager.getSelectedServer(),
                new SimpleCallback<Server>() {
                    @Override
                    public void onSuccess(Server server) {
                        serverGroupStore.loadServerGroup(server.getGroup(),
                                new SimpleCallback<ServerGroupRecord>() {
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
        );
    }


    @Override
    public void onStaleModel(String modelName) {
        if(StaleModelEvent.SERVER_INSTANCES.equals(modelName)
                || StaleModelEvent.SERVER_CONFIGURATIONS.equals(modelName))
        {

            /*Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                @Override
                public void execute() {
                    loadHostData();
                }
            });*/

        }
    }
}
