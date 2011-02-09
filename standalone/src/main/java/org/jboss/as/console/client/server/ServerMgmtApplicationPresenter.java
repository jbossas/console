package org.jboss.as.console.client.server;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;
import org.jboss.as.console.client.MainLayoutPresenter;
import org.jboss.as.console.client.NameTokens;

/**
 * A collection of tools to manage a standalone server instance.
 *
 * @author Heiko Braun
 * @date 1/28/11
 */
public class ServerMgmtApplicationPresenter extends Presenter<ServerMgmtApplicationPresenter.ServerManagementView,
        ServerMgmtApplicationPresenter.ServerManagementProxy> {

    private EventBus eventBus;
    private PlaceManager placeManager;

    public interface ServerManagementView extends View {
    }

    @ProxyCodeSplit
    @NameToken(NameTokens.serverConfig)
    public interface ServerManagementProxy extends ProxyPlace<ServerMgmtApplicationPresenter> {}

    @ContentSlot
    public static final GwtEvent.Type<RevealContentHandler<?>> TYPE_SetToolContent =
            new GwtEvent.Type<RevealContentHandler<?>>();

    @Inject
    public ServerMgmtApplicationPresenter(
            EventBus eventBus, ServerManagementView view,
            ServerManagementProxy proxy, PlaceManager placeManager) {
        super(eventBus, view, proxy);
        this.eventBus = eventBus;
        this.placeManager = placeManager;
    }

    @Override
    protected void revealInParent() {
        // reveal in main layout
        RevealContentEvent.fire(eventBus, MainLayoutPresenter.TYPE_SetMainContent, this);
    }
}
