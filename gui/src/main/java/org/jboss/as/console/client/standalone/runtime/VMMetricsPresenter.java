package org.jboss.as.console.client.standalone.runtime;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.jvm.LoadMetricsCmd;
import org.jboss.as.console.client.shared.jvm.model.CompositeVMMetric;
import org.jboss.as.console.client.shared.runtime.Metric;
import org.jboss.as.console.client.shared.runtime.vm.VMMetricsManagement;
import org.jboss.as.console.client.shared.runtime.vm.VMView;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.dmr.client.ModelNode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 9/28/11
 */
public class VMMetricsPresenter
        extends Presenter<VMView, VMMetricsPresenter.MyProxy>
        implements VMMetricsManagement {

    private static final int POLL_INTERVAL = 5000;
    private ApplicationMetaData metaData;

    private boolean keepPolling = true;
    private Scheduler.RepeatingCommand pollCmd = null;
    private LoadMetricsCmd loadMetricCmd;
    private List<String> vmkeys;

    @ProxyCodeSplit
    @NameToken(NameTokens.VirtualMachine)
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
        this.loadMetricCmd = new LoadMetricsCmd(dispatcher, factory, new ModelNode(), metaData);
        this.vmkeys = new ArrayList<String>();
        this.vmkeys.add("Standalone Server");
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
    }

    @Override
    protected void onHide() {
        super.onHide();
        getView().recycle();
    }


    @Override
    protected void onReset() {
        super.onReset();
        getView().setVMKeys(vmkeys);

        getView().reset();
        keepPolling = true;

        loadVMStatus();
        beginPolling();
    }

    private void beginPolling() {
        pollCmd = new Scheduler.RepeatingCommand() {
            @Override
            public boolean execute() {

                final boolean keepPooling = isVisible() && !shouldPause();

                if (keepPooling)
                    loadVMStatus();
                else
                    Console.warning("Stop polling for VM metrics.");

                return keepPooling;
            }
        };

        Scheduler.get().scheduleFixedDelay(pollCmd, POLL_INTERVAL);

        Console.info("Begin polling for virtual machine metrics");
    }

    private boolean shouldPause() {
        return !keepPolling;
    }


    @Override
    public void loadVMStatus() {
        loadMetricCmd.execute(new SimpleCallback<CompositeVMMetric>() {
            @Override
            public void onSuccess(CompositeVMMetric result) {


                getView().setHeap(new Metric(
                        result.getHeap().getUsed(),
                        result.getHeap().getMax(),
                        result.getHeap().getCommitted(),
                        result.getHeap().getInit()

                ));

                getView().setNonHeap(new Metric(
                        result.getNonHeap().getUsed(),
                        result.getNonHeap().getMax(),
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
        RevealContentEvent.fire(getEventBus(), StandaloneRuntimePresenter.TYPE_MainContent, this);
    }

    @Override
    public void keepPolling(boolean b) {

        this.keepPolling = b;

        if(keepPolling && pollCmd==null)
            beginPolling();
        else if(!keepPolling && pollCmd!=null)
            pollCmd=null;

    }

    @Override
    public void onVMSelection(String vmKey) {
        // ignore. there is only single vm
    }
}
