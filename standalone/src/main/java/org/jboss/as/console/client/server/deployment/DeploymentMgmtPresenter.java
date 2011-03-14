package org.jboss.as.console.client.server.deployment;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.*;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.MainLayoutPresenter;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.core.SuspendableView;
import org.jboss.as.console.client.domain.profiles.ProfileHeader;

/**
 * Manages deployments on a standalone server.
 * Acts as a presenter component.
 *
 * @author Heiko Braun
 * @date 1/31/11
 */
public class DeploymentMgmtPresenter extends Presenter<DeploymentMgmtPresenter.DeploymentToolView,
        DeploymentMgmtPresenter.DeploymentToolProxy> {

    private PlaceManager placeManager;
    private boolean hasBeenRevealed = false;

    public interface DeploymentToolView extends SuspendableView {
        void setPresenter(DeploymentMgmtPresenter presenter);
    }

    @ProxyCodeSplit
    @NameToken(NameTokens.DeploymentMgmtPresenter)
    public interface DeploymentToolProxy extends ProxyPlace<DeploymentMgmtPresenter> {}

    @ContentSlot
    public static final GwtEvent.Type<RevealContentHandler<?>> TYPE_MainContent = new GwtEvent.Type<RevealContentHandler<?>>();

    @Inject
    public DeploymentMgmtPresenter(
            EventBus eventBus, DeploymentToolView view,
            DeploymentToolProxy proxy, PlaceManager placeManager) {

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
        Console.MODULES.getHeader().highlight(NameTokens.DeploymentMgmtPresenter);
        ProfileHeader header = new ProfileHeader("Deployments");
        Console.MODULES.getHeader().setContent(header);

        if(!hasBeenRevealed)
        {
            placeManager.revealRelativePlace(
                    new PlaceRequest(NameTokens.DeploymentListPresenter)
            );
            hasBeenRevealed = true;
        }
    }

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(getEventBus(), MainLayoutPresenter.TYPE_MainContent, this);
    }
}
