package org.jboss.as.console.client.server.deployments;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import org.jboss.as.console.client.NameTokens;
import org.jboss.as.console.client.server.ServerMgmtApplicationPresenter;

/**
 * Manages deployments on a standalone server.
 * Acts as a presenter component.
 *
 * @author Heiko Braun
 * @date 1/31/11
 */
public class DeploymentToolPresenter extends Presenter<DeploymentToolPresenter.DeploymentToolView,
        DeploymentToolPresenter.DeploymentToolProxy> {

    private EventBus eventBus;
    private PlaceManager placeManager;
    private DeploymentStore store;

    public interface DeploymentToolView extends View {
        void setPresenter(DeploymentToolPresenter presenter);
    }

    @ProxyCodeSplit
    @NameToken(NameTokens.deploymentTool)
    public interface DeploymentToolProxy extends ProxyPlace<DeploymentToolPresenter> {}

    @Inject
    public DeploymentToolPresenter(
            EventBus eventBus, DeploymentToolView view,
            DeploymentToolProxy proxy, PlaceManager placeManager,
            DeploymentStore store) {

        super(eventBus, view, proxy);

        this.eventBus = eventBus;
        this.store = store;
        this.placeManager = placeManager;
    }

    public ListGridRecord[] getRecords() {
        return store.loadDeployments();
    }

    @Override
    public void prepareFromRequest(PlaceRequest request) {
        super.prepareFromRequest(request);
        getView().setPresenter(this);
    }

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(eventBus, ServerMgmtApplicationPresenter.TYPE_SetToolContent, this);
    }
}
