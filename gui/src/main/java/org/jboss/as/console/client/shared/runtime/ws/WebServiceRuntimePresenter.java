package org.jboss.as.console.client.shared.runtime.ws;

import com.google.gwt.core.client.Scheduler;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.Proxy;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.state.ServerSelectionChanged;
import org.jboss.as.console.client.shared.subsys.RevealStrategy;
import org.jboss.as.console.client.shared.subsys.ws.EndpointRegistry;
import org.jboss.as.console.client.shared.subsys.ws.model.WebServiceEndpoint;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 1/23/12
 */
public class WebServiceRuntimePresenter
        extends Presenter<WebServiceRuntimePresenter.MyView, WebServiceRuntimePresenter.MyProxy>
        implements ServerSelectionChanged.ChangeListener {

    private EndpointRegistry endpointRegistry;
    private RevealStrategy revealStrategy;

    @ProxyCodeSplit
    @NameToken(NameTokens.WebServiceRuntimePresenter)
    public interface MyProxy extends Proxy<WebServiceRuntimePresenter>, Place {
    }

    public interface MyView extends View {
        void setPresenter(WebServiceRuntimePresenter presenter);
        void updateEndpoints(List<WebServiceEndpoint> endpoints);

    }

    @Inject
    public WebServiceRuntimePresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager, EndpointRegistry registry,
            RevealStrategy revealStrategy) {
        super(eventBus, view, proxy);

        this.endpointRegistry = registry;
        this.revealStrategy = revealStrategy;
    }

    @Override
    public void onServerSelectionChanged(boolean isRunning) {
        if(isVisible())
        {
            Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                @Override
                public void execute() {
                    loadEndpoints();

                }
            });

        }
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
        getEventBus().addHandler(ServerSelectionChanged.TYPE, this);
    }


    @Override
    protected void onReset() {
        super.onReset();

        loadEndpoints();
    }

    private void loadEndpoints() {

        endpointRegistry.create().refreshEndpoints(new SimpleCallback<List<WebServiceEndpoint>>() {
            @Override
            public void onSuccess(List<WebServiceEndpoint> result) {
                getView().updateEndpoints(result);
            }
        });
    }

    @Override
    protected void revealInParent() {
        revealStrategy.revealInRuntimeParent(this);
    }
}
