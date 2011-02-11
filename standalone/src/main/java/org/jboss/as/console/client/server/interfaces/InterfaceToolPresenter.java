package org.jboss.as.console.client.server.interfaces;

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
import org.jboss.as.console.client.NameTokens;
import org.jboss.as.console.client.server.ServerMgmtApplicationPresenter;

/**
 * @author Heiko Braun
 * @date 2/8/11
 */
public class InterfaceToolPresenter extends Presenter<InterfaceToolPresenter.MyView, InterfaceToolPresenter.MyProxy> {

    private final PlaceManager placeManager;

    @ProxyCodeSplit
    @NameToken(NameTokens.InterfaceToolPresenter)
    public interface MyProxy extends Proxy<InterfaceToolPresenter>, Place {
    }

    public interface MyView extends View {
        void setPresenter(InterfaceToolPresenter presenter);
    }

    @Inject
    public InterfaceToolPresenter(EventBus eventBus, MyView view, MyProxy proxy,
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
        RevealContentEvent.fire(getEventBus(), ServerMgmtApplicationPresenter.TYPE_MainContent, this);
    }
}
