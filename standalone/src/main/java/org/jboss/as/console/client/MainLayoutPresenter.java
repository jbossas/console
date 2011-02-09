package org.jboss.as.console.client;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;
import com.gwtplatform.mvp.client.proxy.RevealRootContentEvent;
import com.gwtplatform.mvp.client.proxy.RevealRootLayoutContentEvent;

/**
 * @author Heiko Braun
 * @date 2/4/11
 */
public class MainLayoutPresenter
        extends Presenter<MainLayoutPresenter.MainLayoutView,
        MainLayoutPresenter.MainLayoutProxy> {

    public interface MainLayoutView extends View {
    }

    @ContentSlot
    public static final GwtEvent.Type<RevealContentHandler<?>> TYPE_SetMainContent =
            new GwtEvent.Type<RevealContentHandler<?>>();

    @ProxyCodeSplit
    @NameToken(NameTokens.mainLayout)
    public interface MainLayoutProxy extends ProxyPlace<MainLayoutPresenter> {}

    @Inject
    public MainLayoutPresenter(EventBus eventBus, MainLayoutView view, MainLayoutProxy proxy) {
        super(eventBus, view, proxy);
    }

    @Override
    protected void revealInParent() {
        RevealRootLayoutContentEvent.fire(this, this);
    }
}
