package org.jboss.as.console.client.shared.subsys.logging;

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
import org.jboss.as.console.client.shared.subsys.RevealStrategy;

/**
 * @author Heiko Braun
 * @date 11/17/11
 */
public class LogHandlerPresenter extends Presenter<LogHandlerPresenter.MyView, LogHandlerPresenter.MyProxy> {

    private final PlaceManager placeManager;
    private RevealStrategy revealStategy;

    @ProxyCodeSplit
    @NameToken(NameTokens.LogHandler)
    public interface MyProxy extends Proxy<LogHandlerPresenter>, Place {
    }

    public interface MyView extends View {
        void setPresenter(LogHandlerPresenter presenter);
    }

    @Inject
    public LogHandlerPresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager, RevealStrategy revealStrategy) {
        super(eventBus, view, proxy);

        this.placeManager = placeManager;
        this.revealStategy = revealStrategy;
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
        revealStategy.revealInParent(this);
    }
}
