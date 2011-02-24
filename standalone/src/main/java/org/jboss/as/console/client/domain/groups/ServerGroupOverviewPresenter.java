package org.jboss.as.console.client.domain.groups;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.*;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.core.SuspendableView;
import org.jboss.as.console.client.domain.DomainMgmtApplicationPresenter;

/**
 * @author Heiko Braun
 * @date 2/18/11
 */
public class ServerGroupOverviewPresenter extends Presenter<ServerGroupOverviewPresenter.MyView, ServerGroupOverviewPresenter.MyProxy> {

    private final PlaceManager placeManager;

    @ProxyCodeSplit
    @NameToken(NameTokens.ServerGroupOverviewPresenter)
    public interface MyProxy extends Proxy<ServerGroupOverviewPresenter>, Place {
    }

    public interface MyView extends SuspendableView {
        void setPresenter(ServerGroupOverviewPresenter presenter);
    }

    @Inject
    public ServerGroupOverviewPresenter(EventBus eventBus, MyView view, MyProxy proxy,
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
        RevealContentEvent.fire(getEventBus(), DomainMgmtApplicationPresenter.TYPE_MainContent, this);
    }
}
