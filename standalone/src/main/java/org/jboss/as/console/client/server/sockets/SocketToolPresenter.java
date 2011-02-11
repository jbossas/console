package org.jboss.as.console.client.server.sockets;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import org.jboss.as.console.client.NameTokens;
import org.jboss.as.console.client.components.DisposableView;
import org.jboss.as.console.client.server.ServerMgmtApplicationPresenter;

/**
 * @author Heiko Braun
 * @date 2/8/11
 */
public class SocketToolPresenter extends Presenter<SocketToolPresenter.MyView, SocketToolPresenter.MyProxy> {

    private final PlaceManager placeManager;

    @ProxyCodeSplit
    @NameToken(NameTokens.SocketToolPresenter)
    public interface MyProxy extends Proxy<SocketToolPresenter>, Place {
    }

    public interface MyView extends DisposableView {
        void setPresenter(SocketToolPresenter presenter);
    }

    @Inject
    public SocketToolPresenter(EventBus eventBus, MyView view, MyProxy proxy,
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
