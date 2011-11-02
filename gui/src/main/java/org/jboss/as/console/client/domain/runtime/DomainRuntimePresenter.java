package org.jboss.as.console.client.domain.runtime;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.MainLayoutPresenter;
import org.jboss.as.console.client.core.NameTokens;

/**
 * @author Heiko Braun
 * @date 11/2/11
 */
public class DomainRuntimePresenter extends Presenter<DomainRuntimePresenter.MyView, DomainRuntimePresenter.MyProxy> {

    private final PlaceManager placeManager;

    @ProxyCodeSplit
    @NameToken(NameTokens.DomainRuntimePresenter)
    public interface MyProxy extends Proxy<DomainRuntimePresenter>, Place {
    }

    @ContentSlot
    public static final GwtEvent.Type<RevealContentHandler<?>> TYPE_MainContent =
            new GwtEvent.Type<RevealContentHandler<?>>();

    public interface MyView extends View {
        void setPresenter(DomainRuntimePresenter presenter);
    }

    @Inject
    public DomainRuntimePresenter(EventBus eventBus, MyView view, MyProxy proxy,
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
        Console.MODULES.getHeader().highlight(NameTokens.DomainRuntimePresenter);
    }

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(getEventBus(), MainLayoutPresenter.TYPE_MainContent, this);
    }
}
