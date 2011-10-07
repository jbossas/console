package org.jboss.as.console.client.domain.hosts;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.jvm.LoadMetricsCmd;
import org.jboss.as.console.client.shared.jvm.VMMetricsManagement;
import org.jboss.as.console.client.shared.jvm.VMView;
import org.jboss.as.console.client.shared.jvm.model.CompositeVMMetric;
import org.jboss.as.console.client.widgets.forms.PropertyMetaData;
import org.jboss.dmr.client.ModelNode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 10/7/11
 */
public class HostVMMetricPresenter extends Presenter<VMView, HostVMMetricPresenter.MyProxy>
        implements VMMetricsManagement {

    private final PlaceManager placeManager;
    private CurrentHostSelection currentHost;
    private DispatchAsync dispatcher;
    private PropertyMetaData metaData;
    private BeanFactory factory;
    private String currentServer;

    private boolean keepPolling = true;
    private Scheduler.RepeatingCommand pollCmd = null;
    private static final int POLL_INTERVAL = 5000;

    @ProxyCodeSplit
    @NameToken(NameTokens.HostVMMetricPresenter)
    public interface MyProxy extends Proxy<HostVMMetricPresenter>, Place {
    }

    public interface MyView extends VMView {
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
    protected void onReset() {

        getView().reset();

        if(Console.visAPILoaded())
            getView().attachCharts();
        else
            Console.error("Failed load visualization API", "Charts will not be available.");

        List<String> vmkeys = loadVMKeys();
        getView().setVMKeys(vmkeys);
        currentServer = vmkeys.get(0);

        // actually load the vm metrics

        loadVMStatus();
        beginPolling();

    }

    private List<String> loadVMKeys() {
        List<String> vmkeys = new ArrayList<String>();
        vmkeys.add("server-one");
        vmkeys.add("server-two");
        vmkeys.add("server-three");
        return vmkeys;
    }

    @Override
    protected void onHide() {
        super.onHide();
        getView().detachCharts();
    }

    private LoadMetricsCmd createLoadMetricCmd() {
        ModelNode address = new ModelNode();
        address.add("host", currentHost.getName());
        address.add("server", getCurrentServer());

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

    @Override
    public void loadVMStatus() {
        createLoadMetricCmd().execute(new SimpleCallback<CompositeVMMetric>() {
            @Override
            public void onSuccess(CompositeVMMetric result) {
                getView().setHeap(result.getHeap());
                getView().setNonHeap(result.getNonHeap());
                getView().setOSMetric(result.getOs());
                getView().setRuntimeMetric(result.getRuntime());
                getView().setThreads(result.getThreads());
            }
        });
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
    public void keepPolling(boolean b) {
        this.keepPolling = b;

        if(keepPolling && pollCmd==null)
            beginPolling();
        else if(!keepPolling && pollCmd!=null)
            pollCmd=null;
    }

    public String getCurrentServer() {
        return currentServer;
    }

    @Override
    public void onVMSelection(String vmKey) {

        System.out.println("Current server: "+vmKey);
        this.currentServer = vmKey;

        getView().reset();
    }
}
