package org.jboss.as.console.client.shared.runtime.jpa;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.Proxy;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.domain.model.ServerInstance;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.state.CurrentServerSelection;
import org.jboss.as.console.client.shared.state.ServerSelectionEvent;
import org.jboss.as.console.client.shared.subsys.RevealStrategy;
import org.jboss.as.console.client.shared.subsys.web.model.HttpConnector;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;

/**
 * @author Heiko Braun
 * @date 1/19/12
 */
public class JPAMetricPresenter extends Presenter<JPAMetricPresenter.MyView, JPAMetricPresenter.MyProxy>
    implements ServerSelectionEvent.ServerSelectionListener {

    private DispatchAsync dispatcher;
    private RevealStrategy revealStrategy;
    private CurrentServerSelection serverSelection;
    private BeanFactory factory;

    @ProxyCodeSplit
    @NameToken(NameTokens.JPAMetricPresenter)
    public interface MyProxy extends Proxy<JPAMetricPresenter>, Place {
    }

    public interface MyView extends View {
        void setPresenter(JPAMetricPresenter presenter);
    }

    @Inject
    public JPAMetricPresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            DispatchAsync dispatcher,
            ApplicationMetaData metaData, RevealStrategy revealStrategy,
            CurrentServerSelection serverSelection, BeanFactory factory) {
        super(eventBus, view, proxy);

        this.dispatcher = dispatcher;
        this.revealStrategy = revealStrategy;
        this.serverSelection = serverSelection;
        this.factory = factory;
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
    }

    @Override
    protected void revealInParent() {
        revealStrategy.revealInRuntimeParent(this);
    }

    @Override
    public void onServerSelection(String hostName, ServerInstance server) {

        //getView().clearSamples();
        //if(isVisible()) refresh();
    }
}
