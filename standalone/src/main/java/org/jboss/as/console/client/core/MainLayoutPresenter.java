package org.jboss.as.console.client.core;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;
import com.gwtplatform.mvp.client.proxy.RevealRootLayoutContentEvent;
import org.jboss.as.console.client.Console;

/**
 * @author Heiko Braun
 * @date 2/4/11
 */
public class MainLayoutPresenter
        extends Presenter<MainLayoutPresenter.MainLayoutView,
        MainLayoutPresenter.MainLayoutProxy> {

    boolean revealDefault = true;

    public interface MainLayoutView extends View {
    }

    @ContentSlot
    public static final GwtEvent.Type<RevealContentHandler<?>> TYPE_SetMainContent = new GwtEvent.Type<RevealContentHandler<?>>();

    @ProxyCodeSplit
    @NameToken(NameTokens.mainLayout)
    public interface MainLayoutProxy extends ProxyPlace<MainLayoutPresenter> {}

    @Inject
    public MainLayoutPresenter(EventBus eventBus, MainLayoutView view, MainLayoutProxy proxy) {
        super(eventBus, view, proxy);
    }

    @Override
    public void prepareFromRequest(PlaceRequest request) {
        super.prepareFromRequest(request);
        if(revealDefault && request.getNameToken().equals(NameTokens.mainLayout))
        {
            revealDefault = false;
            Console.MODULES.getPlaceManager().revealPlace(
                    new PlaceRequest(NameTokens.ProfileMgmtPresenter)
            );
        }
    }

    @Override
    protected void revealInParent() {
        RevealRootLayoutContentEvent.fire(this, this);
    }
}
