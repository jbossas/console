package org.jboss.as.console.client.standalone.runtime;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.standalone.ServerMgmtApplicationPresenter;

/**
 * @author Heiko Braun
 * @date 11/3/11
 */
public class TXMetricPresenter extends Presenter<TXMetricPresenter.MyView, TXMetricPresenter.MyProxy> {

    private final PlaceManager placeManager;

    @ProxyCodeSplit
    @NameToken(NameTokens.TXMetrics)
    public interface MyProxy extends Proxy<TXMetricPresenter>, Place {
    }

    public interface MyView extends View {
        void setPresenter(TXMetricPresenter presenter);
    }

    @Inject
    public TXMetricPresenter(EventBus eventBus, MyView view, MyProxy proxy,
                             PlaceManager placeManager) {
        super(eventBus, view, proxy);

        this.placeManager = placeManager;
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
       RevealContentEvent.fire(getEventBus(), StandaloneRuntimePresenter.TYPE_MainContent, this);
    }
}
