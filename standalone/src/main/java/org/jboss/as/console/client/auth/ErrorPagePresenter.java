package org.jboss.as.console.client.auth;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Button;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.NoGatekeeper;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealRootLayoutContentEvent;
import org.jboss.as.console.client.NameTokens;

/**
 * @author Heiko Braun
 * @date 2/7/11
 */
public class ErrorPagePresenter extends
        Presenter<ErrorPagePresenter.MyView, ErrorPagePresenter.MyProxy> {

    private final PlaceManager placeManager;


    @ProxyStandard
    @NameToken(NameTokens.errorPage)
    @NoGatekeeper
    public interface MyProxy extends Proxy<ErrorPagePresenter>, Place {
    }

    public interface MyView extends View {

    }

    @Inject
    public ErrorPagePresenter(EventBus eventBus, MyView view, MyProxy proxy,
                              PlaceManager placeManager) {
        super(eventBus, view, proxy);
        this.placeManager = placeManager;
    }

    @Override
    protected void revealInParent() {
        RevealRootLayoutContentEvent.fire(this, this);
    }
}
