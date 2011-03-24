package org.jboss.as.console.client.shared.subsys.jca;

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
import org.jboss.as.console.client.domain.profiles.ProfileMgmtPresenter;

/**
 * @author Heiko Braun
 * @date 3/24/11
 */
public class DataSourcePresenter extends Presenter<DataSourcePresenter.MyView, DataSourcePresenter.MyProxy> {

    private final PlaceManager placeManager;

    @ProxyCodeSplit
    @NameToken(NameTokens.DataSourcePresenter)
    public interface MyProxy extends Proxy<DataSourcePresenter>, Place {
    }

    public interface MyView extends View {
        void setPresenter(DataSourcePresenter presenter);
    }

    @Inject
    public DataSourcePresenter(EventBus eventBus, MyView view, MyProxy proxy,
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
         RevealContentEvent.fire(getEventBus(), ProfileMgmtPresenter.TYPE_MainContent, this);
    }
}
