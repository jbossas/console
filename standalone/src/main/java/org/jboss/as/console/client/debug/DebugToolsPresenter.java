package org.jboss.as.console.client.debug;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.*;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.MainLayoutPresenter;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.domain.profiles.ProfileHeader;

/**
 * @author Heiko Braun
 * @date 3/16/11
 */
public class DebugToolsPresenter extends Presenter<DebugToolsPresenter.MyView, DebugToolsPresenter.MyProxy> {

    private final PlaceManager placeManager;

    private boolean hasBeenRevealed = false;

    @ProxyCodeSplit
    @NameToken(NameTokens.DebugToolsPresenter)
    public interface MyProxy extends Proxy<DebugToolsPresenter>, Place {
    }

    public interface MyView extends View {
        void setPresenter(DebugToolsPresenter presenter);
    }

    @ContentSlot
    public static final GwtEvent.Type<RevealContentHandler<?>> TYPE_MainContent = new GwtEvent.Type<RevealContentHandler<?>>();

    @Inject
    public DebugToolsPresenter(EventBus eventBus, MyView view, MyProxy proxy,
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

        Console.MODULES.getHeader().highlight(NameTokens.DebugToolsPresenter);
        ProfileHeader header = new ProfileHeader("Development Tools");
        Console.MODULES.getHeader().setContent(header);

        if(!hasBeenRevealed)
        {
            placeManager.revealRelativePlace(
                    new PlaceRequest(NameTokens.ModelBrowserPresenter)
            );
            hasBeenRevealed = true;
        }
    }

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(getEventBus(), MainLayoutPresenter.TYPE_MainContent, this);
    }
}
