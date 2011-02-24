package org.jboss.as.console.client.server;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.*;
import org.jboss.as.console.client.MainLayoutPresenter;
import org.jboss.as.console.client.NameTokens;
import org.jboss.as.console.client.shared.SubsystemRecord;
import org.jboss.as.console.client.shared.SubsystemStore;

import java.util.List;

/**
 * A collection of tools to manage a standalone server instance.
 *
 * @author Heiko Braun
 * @date 1/28/11
 */
public class ServerMgmtApplicationPresenter extends Presenter<ServerMgmtApplicationPresenter.ServerManagementView,
        ServerMgmtApplicationPresenter.ServerManagementProxy> {

    private PlaceManager placeManager;
    private boolean revealDefault = true;

    private SubsystemStore subsysStore;

    public interface ServerManagementView extends View {

        void updateFrom(List<SubsystemRecord> subsystemRecords);
    }

    @ProxyCodeSplit
    @NameToken(NameTokens.serverConfig)
    public interface ServerManagementProxy extends ProxyPlace<ServerMgmtApplicationPresenter> {}

    @ContentSlot
    public static final GwtEvent.Type<RevealContentHandler<?>> TYPE_MainContent = new GwtEvent.Type<RevealContentHandler<?>>();

    @Inject
    public ServerMgmtApplicationPresenter(
            EventBus eventBus, ServerManagementView view,
            ServerManagementProxy proxy, PlaceManager placeManager,
            SubsystemStore subsysStore) {
        super(eventBus, view, proxy);
        this.placeManager = placeManager;
        this.subsysStore = subsysStore;
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().updateFrom(subsysStore.loadSubsystems());
    }

    /**
     * Load a default sub page upon first reveal
     * and highlight navigation sections in subsequent requests.
     *
     * @param request
     */
    @Override
    public void prepareFromRequest(PlaceRequest request) {
        super.prepareFromRequest(request);

        // reveal default sub page
        if(revealDefault && NameTokens.serverConfig.equals(request.getNameToken()))
        {
            placeManager.revealRelativePlace(new PlaceRequest(NameTokens.deploymentTool));
            revealDefault = false; // only once
        }
    }

    @Override
    protected void revealInParent() {
        // reveal in main layout
        RevealContentEvent.fire(getEventBus(), MainLayoutPresenter.TYPE_SetMainContent, this);
    }
}
