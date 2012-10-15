package org.jboss.as.console.client.shared.general;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.Proxy;
import org.jboss.as.console.client.core.NameTokens;

/**
 * @author Heiko Braun
 * @date 10/15/12
 */
public class PathManagementPresenter extends Presenter<PathManagementPresenter.MyView, PathManagementPresenter.MyProxy> {

    private final PlaceManager placeManager;

    @ProxyCodeSplit
    @NameToken(NameTokens.PathManagementPresenter)
    public interface MyProxy extends Proxy<PathManagementPresenter>, Place {
    }

    public interface MyView extends View {
        void setPresenter(PathManagementPresenter presenter);
    }

    @Inject
    public PathManagementPresenter(EventBus eventBus, MyView view, MyProxy proxy,
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
        // TODO: Implement
    }
}
