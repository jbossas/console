package org.jboss.as.console.client.server.deployment;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.shared.DeploymentRecord;

/**
 * @author Heiko Braun
 * @date 3/14/11
 */
public class DeploymentListPresenter extends Presenter<DeploymentListPresenter.MyView, DeploymentListPresenter.MyProxy> {

    private final PlaceManager placeManager;

    @ProxyCodeSplit
    @NameToken(NameTokens.DeploymentListPresenter)
    public interface MyProxy extends Proxy<DeploymentListPresenter>, Place {
    }

    public interface MyView extends View {
        void setPresenter(DeploymentListPresenter presenter);
    }

    @Inject
    public DeploymentListPresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
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
        RevealContentEvent.fire(getEventBus(), DeploymentMgmtPresenter.TYPE_MainContent, this);
    }

    public void onFilterType(String value) {

    }

    public void deleteDeployment(DeploymentRecord selectedObject) {

    }

}
