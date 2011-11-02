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
import org.jboss.as.console.client.domain.events.ServerSelectionEvent;
import org.jboss.as.console.client.domain.hosts.CurrentHostSelection;
import org.jboss.as.console.client.domain.model.Host;
import org.jboss.as.console.client.domain.model.HostInformationStore;
import org.jboss.as.console.client.domain.model.Server;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.ballroom.client.layout.LHSHighlightEvent;

import java.util.List;
import java.util.Timer;

/**
 * @author Heiko Braun
 * @date 11/2/11
 */
public class DomainRuntimePresenter extends Presenter<DomainRuntimePresenter.MyView, DomainRuntimePresenter.MyProxy>
    implements HostSelectionEvent.HostSelectionListener, ServerSelectionEvent.ServerSelectionListener {

    private final PlaceManager placeManager;
    private boolean hasBeenRevealed = false;
    private HostInformationStore hostInfoStore;
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
        void setServer(String host, List<Server> server);
    }

    @Inject
    public DomainRuntimePresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager,  HostInformationStore hostInfoStore,
            CurrentHostSelection hostSelection) {
        super(eventBus, view, proxy);

        this.placeManager = placeManager;
        this.hostInfoStore = hostInfoStore;
        this.hostSelection = hostSelection;
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
        getEventBus().addHandler(HostSelectionEvent.TYPE, this);
        getEventBus().addHandler(ServerSelectionEvent.TYPE, this);
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

        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                hostInfoStore.getHosts(new SimpleCallback<List<Host>>() {
                    @Override
                    public void onSuccess(List<Host> hosts) {
                        getView().setHosts(hosts);
                    }
                });

                if (hostSelection.isSet()) {
                    hostInfoStore.getServerConfigurations(hostSelection.getName(), new SimpleCallback<List<Server>>() {
                        @Override
                        public void onSuccess(List<Server> hosts) {
                            getView().setServer(hostSelection.getName(), hosts);
                        }
                    });
                } else {
                    throw new RuntimeException("Host selection not set!");
                }

            }
        });

    }

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(getEventBus(), MainLayoutPresenter.TYPE_MainContent, this);
    }

    @Override
    public void onHostSelection(String hostName) {

        System.out.println("Selected host: "+hostSelection.getName());
        hostSelection.setName(hostName);
    }

    @Override
    public void onServerSelection(String serverName) {

        System.out.println("Selected server: "+serverName);

    }
}
