package org.jboss.as.console.client.server.subsys;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.NoGatekeeper;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.*;
import org.jboss.as.console.client.NameTokens;
import org.jboss.as.console.client.server.ServerMgmtApplicationPresenter;

/**
 * @author Heiko Braun
 * @date 2/8/11
 */
public class SubsystemToolPresenter extends Presenter<SubsystemToolPresenter.MyView, SubsystemToolPresenter.MyProxy> {

    private final PlaceManager placeManager;

    @ProxyCodeSplit
    @NameToken(NameTokens.SubsystemToolPresenter)
    public interface MyProxy extends Proxy<SubsystemToolPresenter>, Place {
    }

    public interface MyView extends View {
        void setPresenter(SubsystemToolPresenter presenter);
    }

    @Inject
    public SubsystemToolPresenter(EventBus eventBus, MyView view, MyProxy proxy,
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
        RevealContentEvent.fire(getEventBus(), ServerMgmtApplicationPresenter.TYPE_SetToolContent, this);
    }
}
