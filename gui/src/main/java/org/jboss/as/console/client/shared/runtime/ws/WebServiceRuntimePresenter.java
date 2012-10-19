package org.jboss.as.console.client.shared.runtime.ws;

import com.google.gwt.core.client.Scheduler;
import com.google.web.bindery.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.Proxy;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.domain.model.ServerInstance;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.state.CurrentServerSelection;
import org.jboss.as.console.client.shared.state.ServerSelectionEvent;
import org.jboss.as.console.client.shared.subsys.RevealStrategy;
import org.jboss.as.console.client.shared.subsys.ws.EndpointRegistry;
import org.jboss.as.console.client.shared.subsys.ws.model.WebServiceEndpoint;

import java.util.Collections;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 1/23/12
 */
public class WebServiceRuntimePresenter
        extends Presenter<WebServiceRuntimePresenter.MyView, WebServiceRuntimePresenter.MyProxy>
        implements ServerSelectionEvent.ServerSelectionListener {

    private EndpointRegistry endpointRegistry;
    private RevealStrategy revealStrategy;
    private CurrentServerSelection serverSelection;

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
            RevealStrategy revealStrategy, CurrentServerSelection serverSelection) {
        super(eventBus, view, proxy);

        this.endpointRegistry = registry;
        this.serverSelection = serverSelection;
        this.revealStrategy = revealStrategy;
    }

    @Override
    public void onServerSelection(String hostName, final ServerInstance server, ServerSelectionEvent.Source source) {

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
        getEventBus().addHandler(ServerSelectionEvent.TYPE, this);
    }


    @Override
    protected void onReset() {
        super.onReset();

        loadEndpoints();
    }

    private void loadEndpoints() {

        if(!serverSelection.isActive()) {
            Console.warning(Console.CONSTANTS.common_err_server_not_active());
            getView().updateEndpoints(Collections.EMPTY_LIST);
            return;
        }

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
