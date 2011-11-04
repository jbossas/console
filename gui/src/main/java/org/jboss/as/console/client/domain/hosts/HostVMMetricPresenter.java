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
import org.jboss.as.console.client.domain.model.Server;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.domain.runtime.DomainRuntimePresenter;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.jvm.LoadMetricsCmd;
import org.jboss.as.console.client.shared.runtime.vm.VMMetricsManagement;
import org.jboss.as.console.client.shared.runtime.vm.VMView;
import org.jboss.as.console.client.shared.jvm.model.CompositeVMMetric;
import org.jboss.as.console.client.shared.runtime.Metric;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
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
    private CurrentHostSelection hostSelection;
    private DispatchAsync dispatcher;
    private ApplicationMetaData metaData;
    private BeanFactory factory;
    private String currentServer;

    private boolean keepPolling = true;
    private Scheduler.RepeatingCommand pollCmd = null;
    private static final int POLL_INTERVAL = 5000;
    private CurrentServerConfigurations serverConfigs;

    @ProxyCodeSplit
    @NameToken(NameTokens.HostVMMetricPresenter)
    public interface MyProxy extends Proxy<HostVMMetricPresenter>, Place {
    }

    public interface MyView extends VMView {
    }

    @Inject
    public HostVMMetricPresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager, CurrentHostSelection hostSelection,
            DispatchAsync dispatcher, BeanFactory factory,
            ApplicationMetaData metaData, CurrentServerConfigurations serverConfigs) {
        super(eventBus, view, proxy);

        this.placeManager = placeManager;
        this.hostSelection = hostSelection;
        this.dispatcher = dispatcher;
        this.factory = factory;
        this.metaData = metaData;
        this.serverConfigs = serverConfigs;
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
    }

    @Override
    protected void onReset() {

        List<String> vmkeys = loadVMKeys();
        getView().setVMKeys(vmkeys);
        currentServer = vmkeys.get(0);

        // actually load the vm metrics

        loadVMStatus();
        beginPolling();

    }

    private List<String> loadVMKeys() {

        List<String> vmkeys = new ArrayList<String>();

        for(Server s : serverConfigs.getServerConfigs())
        {
            vmkeys.add(s.getName());
        }

        return vmkeys;
    }

    @Override
    protected void onHide() {
        super.onHide();
        getView().recycle();
    }

    private LoadMetricsCmd createLoadMetricCmd() {

        if(!hostSelection.isSet())
            throw new RuntimeException("Host selection not set!");

         if(getCurrentServer()==null)
            throw new RuntimeException("Current Server not set!");

        ModelNode address = new ModelNode();
        address.add("host", hostSelection.getName());
        address.add("server", getCurrentServer());

        return new LoadMetricsCmd(
                dispatcher, factory,
                address,
                metaData
        );
    }

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(getEventBus(), DomainRuntimePresenter.TYPE_MainContent, this);
    }

    @Override
    public void loadVMStatus() {
        createLoadMetricCmd().execute(new SimpleCallback<CompositeVMMetric>() {


            @Override
            public void onFailure(Throwable caught) {
                Console.error("No VM Metrics available", caught.getMessage());
                keepPolling(false);
            }

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
                getView().setOSMetric(result.getOs());
                getView().setRuntimeMetric(result.getRuntime());
                getView().setThreads(
                        new Metric(
                                result.getThreads().getCount(),
                                result.getThreads().getDaemonCount()
                        )
                );
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

        this.currentServer = vmKey;

        keepPolling(true);
    }
}
