package org.jboss.as.console.client.standalone.runtime;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.Timer;
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
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.model.SubsystemRecord;
import org.jboss.as.console.client.shared.model.SubsystemStore;
import org.jboss.ballroom.client.layout.LHSHighlightEvent;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 11/2/11
 */
public class StandaloneRuntimePresenter extends Presenter<StandaloneRuntimePresenter.MyView, StandaloneRuntimePresenter.MyProxy> {

    private final PlaceManager placeManager;
    private boolean hasBeenRevealed = false;

    @ContentSlot
    public static final GwtEvent.Type<RevealContentHandler<?>> TYPE_MainContent =
            new GwtEvent.Type<RevealContentHandler<?>>();
    private SubsystemStore subsysStore;
    private BootstrapContext bootstrap;

    private String lastSubPlace;
    private Header header;

    @ProxyCodeSplit
    @NameToken(NameTokens.StandaloneRuntimePresenter)
    public interface MyProxy extends Proxy<StandaloneRuntimePresenter>, Place {
    }

    public interface MyView extends View {
        void setPresenter(StandaloneRuntimePresenter presenter);
        void setSubsystems(List<SubsystemRecord> result);
    }

    @Inject
    public StandaloneRuntimePresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager, SubsystemStore subsysStore,
            BootstrapContext bootstrap, Header header) {
        super(eventBus, view, proxy);

        this.placeManager = placeManager;
        this.bootstrap = bootstrap;
        this.subsysStore = subsysStore;
        this.header = header;
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
    }


    @Override
    protected void onReset() {

        super.onReset();


        header.highlight(NameTokens.StandaloneRuntimePresenter);

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
        if(!hasBeenRevealed )
        {
            subsysStore.loadSubsystems("default", new SimpleCallback<List<SubsystemRecord>>() {
                @Override
                public void onSuccess(List<SubsystemRecord> result) {
                    getView().setSubsystems(result);
                }
            });


            if(placeManager.getCurrentPlaceRequest().getNameToken().equals(NameTokens.StandaloneRuntimePresenter))
            {
                placeManager.revealPlace(new PlaceRequest(NameTokens.StandaloneServerPresenter));
            }

            Timer t = new Timer() {
                @Override
                public void run() {
                    highlightLHSNav();
                }
            };

            t.schedule(150);

            hasBeenRevealed = true;

        }
    }

    private void highlightLHSNav() {
        if(bootstrap.getInitialPlace()!=null)
        {
            Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                @Override
                public void execute() {
                    Console.getEventBus().fireEvent(
                            new LHSHighlightEvent(bootstrap.getInitialPlace())
                    );
                    bootstrap.setInitialPlace(null);
                }
            });
        }
    }

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(getEventBus(), MainLayoutPresenter.TYPE_MainContent, this);
    }
}
