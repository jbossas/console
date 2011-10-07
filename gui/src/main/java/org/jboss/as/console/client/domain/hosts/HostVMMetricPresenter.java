package org.jboss.as.console.client.domain.hosts;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.jvm.LoadMetricsCmd;
import org.jboss.as.console.client.shared.jvm.VMView;
import org.jboss.as.console.client.widgets.forms.PropertyMetaData;
import static org.jboss.dmr.client.ModelDescriptionConstants.*;
import org.jboss.dmr.client.ModelNode;

/**
 * @author Heiko Braun
 * @date 10/7/11
 */
public class HostVMMetricPresenter extends Presenter<HostVMMetricPresenter.MyView, HostVMMetricPresenter.MyProxy> {

    private final PlaceManager placeManager;
    private CurrentHostSelection currentHost;
    private DispatchAsync dispatcher;
    private PropertyMetaData metaData;
    private BeanFactory factory;
    private String currentServer;

    public String getCurrentServer() {
        return currentServer;
    }

    @ProxyCodeSplit
    @NameToken(NameTokens.HostVMMetricPresenter)
    public interface MyProxy extends Proxy<HostVMMetricPresenter>, Place {
    }

    public interface MyView extends View, VMView {
        void setPresenter(HostVMMetricPresenter presenter);
    }

    @Inject
    public HostVMMetricPresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager, CurrentHostSelection currentHost,
            DispatchAsync dispatcher, BeanFactory factory,
            PropertyMetaData metaData) {
        super(eventBus, view, proxy);

        this.placeManager = placeManager;
        this.currentHost = currentHost;
        this.dispatcher = dispatcher;
        this.factory = factory;
        this.metaData = metaData;
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
    }

    @Override
    public void prepareFromRequest(PlaceRequest request) {
        currentServer = request.getParameter("server", null);
    }

    @Override
    protected void onReset() {
        super.onReset();
    }

    private LoadMetricsCmd createLoadMetricCmd() {
        ModelNode address = new ModelNode();
        address.get(ADDRESS).add("host", currentHost.getName());
        address.get(ADDRESS).add("server", getCurrentServer());

        return new LoadMetricsCmd(
                dispatcher, factory,
                address,
                metaData
                );
    }

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(getEventBus(), HostMgmtPresenter.TYPE_MainContent, this);
    }
}
