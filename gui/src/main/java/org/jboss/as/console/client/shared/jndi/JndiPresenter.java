package org.jboss.as.console.client.shared.jndi;

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
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.subsys.RevealStrategy;

/**
 * @author Heiko Braun
 * @date 7/20/11
 */
public class JndiPresenter extends Presenter<JndiPresenter.MyView, JndiPresenter.MyProxy> {

    private final PlaceManager placeManager;
    private RevealStrategy revealStrategy;
    private DispatchAsync dispatcher;
    private BeanFactory factory;

    @ProxyCodeSplit
    @NameToken(NameTokens.JndiPresenter)
    public interface MyProxy extends Proxy<JndiPresenter>, Place {
    }

    public interface MyView extends View {
        void setPresenter(JndiPresenter presenter);
    }

    @Inject
    public JndiPresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager, RevealStrategy revealStrategy,
            DispatchAsync dispatcher, BeanFactory factory) {

        super(eventBus, view, proxy);

        this.placeManager = placeManager;
        this.revealStrategy = revealStrategy;
        this.dispatcher = dispatcher;
        this.factory = factory;

    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
    }


    @Override
    protected void onReset() {
        super.onReset();
    }

    @Override
    protected void revealInParent() {
        revealStrategy.revealInParent(this);
    }
}
