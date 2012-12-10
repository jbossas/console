package org.jboss.as.console.client.standalone.runtime;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.UseGatekeeper;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.core.StandaloneGateKeeper;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.jvm.LoadJVMMetricsCmd;
import org.jboss.as.console.client.shared.jvm.model.CompositeVMMetric;
import org.jboss.as.console.client.shared.runtime.Metric;
import org.jboss.as.console.client.shared.runtime.vm.VMMetricsManagement;
import org.jboss.as.console.client.shared.runtime.vm.VMView;
import org.jboss.as.console.client.shared.state.ServerSelectionChanged;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.dmr.client.ModelNode;

/**
 * @author Heiko Braun
 * @date 9/28/11
 */
public class VMMetricsPresenter
        extends Presenter<VMView, VMMetricsPresenter.MyProxy>
        implements VMMetricsManagement, ServerSelectionChanged.ChangeListener {

    private ApplicationMetaData metaData;
    private LoadJVMMetricsCmd loadMetricCmd;

    @ProxyCodeSplit
    @NameToken(NameTokens.VirtualMachine)
    @UseGatekeeper( StandaloneGateKeeper.class )
    public interface MyProxy extends Proxy<VMMetricsPresenter>, Place {
    }

    public interface MyView extends VMView {
    }

    @Inject
    public VMMetricsPresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            DispatchAsync dispatcher, BeanFactory factory, ApplicationMetaData propertyMetaData) {
        super(eventBus, view, proxy);

        this.metaData = propertyMetaData;
        this.loadMetricCmd = new LoadJVMMetricsCmd(dispatcher, factory, new ModelNode(), metaData);
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
        getEventBus().addHandler(ServerSelectionChanged.TYPE, this);
    }

    @Override
    protected void onHide() {
        super.onHide();
    }


    @Override
    protected void onReset() {
        super.onReset();
        loadVMStatus();

    }

    @Override
    public void refresh() {
         loadVMStatus();
    }

    public void loadVMStatus() {

        getView().clearSamples();

        loadMetricCmd.execute(new SimpleCallback<CompositeVMMetric>() {
            @Override
            public void onSuccess(CompositeVMMetric result) {

                getView().setHeap(new Metric(
                        result.getHeap().getMax(),
                        result.getHeap().getUsed(),
                        result.getHeap().getCommitted(),
                        result.getHeap().getInit()

                ));

                getView().setNonHeap(new Metric(
                        result.getNonHeap().getMax(),
                        result.getNonHeap().getUsed(),
                        result.getNonHeap().getCommitted(),
                        result.getNonHeap().getInit()
                ));

                getView().setThreads(new Metric(
                        result.getThreads().getCount(),
                        result.getThreads().getDaemonCount()
                ));

                getView().setOSMetric(result.getOs());
                getView().setRuntimeMetric(result.getRuntime());
            }
        });

    }

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(this, StandaloneRuntimePresenter.TYPE_MainContent, this);
    }

    @Override
    public void onServerSelectionChanged(boolean isRunning) {
        if(isVisible()) refresh();
    }
}
