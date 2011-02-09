package org.jboss.as.console.client.system;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import org.jboss.as.console.client.MainLayoutPresenter;
import org.jboss.as.console.client.NameTokens;

/**
 * @author Heiko Braun
 * @date 1/31/11
 */
public class SystemApplicationPresenter extends Presenter<SystemApplicationPresenter.SystemAppView,
        SystemApplicationPresenter.SystemAppProxy> {

    private EventBus eventBus;

    public interface SystemAppView extends View {
    }

    @ProxyCodeSplit
    @NameToken(NameTokens.systemApp)
    public interface SystemAppProxy extends ProxyPlace<SystemApplicationPresenter> {}

    @Inject
    public SystemApplicationPresenter(EventBus eventBus, SystemAppView view, SystemAppProxy proxy) {
        super(eventBus, view, proxy);
        this.eventBus = eventBus;
    }

    @Override
    protected void revealInParent() {
        // reveal in main layout
        RevealContentEvent.fire(eventBus, MainLayoutPresenter.TYPE_SetMainContent, this);
    }

}
