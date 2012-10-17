package org.jboss.as.console.client.domain.hosts;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.UseGatekeeper;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.DomainGateKeeper;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.domain.model.HostInformationStore;
import org.jboss.as.console.client.domain.model.ServerInstance;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.domain.runtime.DomainRuntimePresenter;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.jvm.LoadJVMMetricsCmd;
import org.jboss.as.console.client.shared.jvm.model.CompositeVMMetric;
import org.jboss.as.console.client.shared.runtime.Metric;
import org.jboss.as.console.client.shared.runtime.vm.VMMetricsManagement;
import org.jboss.as.console.client.shared.runtime.vm.VMView;
import org.jboss.as.console.client.shared.state.CurrentServerSelection;
import org.jboss.as.console.client.shared.state.ServerSelectionEvent;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.dmr.client.ModelNode;

/**
 * @author Heiko Braun
 * @date 10/7/11
 */
public class HostVMMetricPresenter extends Presenter<VMView, HostVMMetricPresenter.MyProxy>
        implements VMMetricsManagement, ServerSelectionEvent.ServerSelectionListener  {


    private DispatchAsync dispatcher;
    private ApplicationMetaData metaData;
    private BeanFactory factory;

    private boolean keepPolling = true;
    private Scheduler.RepeatingCommand pollCmd = null;
    private static final int POLL_INTERVAL = 5000;
    private HostInformationStore hostInfoStore;
    CurrentServerSelection serverSelection;

    @ProxyCodeSplit
    @NameToken(NameTokens.HostVMMetricPresenter)
    @UseGatekeeper( DomainGateKeeper.class )
    public interface MyProxy extends Proxy<HostVMMetricPresenter>, Place {
    }

    public interface MyView extends VMView {
    }

    @Inject
    public HostVMMetricPresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            CurrentServerSelection serverSelection,
            DispatchAsync dispatcher, BeanFactory factory,
            ApplicationMetaData metaData, HostInformationStore hostInfoStore
            ) {
        super(eventBus, view, proxy);

        this.serverSelection = serverSelection;
        this.dispatcher = dispatcher;
        this.factory = factory;
        this.metaData = metaData;
        this.hostInfoStore = hostInfoStore;
    }

    @Override
    public void onServerSelection(String hostName, ServerInstance server, ServerSelectionEvent.Source source) {

         Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                if(isVisible()) refresh();
            }
         });
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
        getEventBus().addHandler(ServerSelectionEvent.TYPE, this);
    }

    @Override
    protected void onReset() {
        refresh();
    }

    @Override
    public void refresh() {

        if(!serverSelection.isActive()) {
            Console.warning(Console.CONSTANTS.common_err_server_not_active());
            getView().clearSamples();
            return;
        }

        loadVMStatus(serverSelection.getServer().getName());
    }

    private LoadJVMMetricsCmd createLoadMetricCmd() {

        if(!serverSelection.isSet())
            throw new RuntimeException("Server selection not set!");


        ModelNode address = new ModelNode();
        address.add("host", serverSelection.getHost());
        address.add("server", serverSelection.getServer().getName());

        return new LoadJVMMetricsCmd(
                dispatcher, factory,
                address,
                metaData
        );
    }

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(this, DomainRuntimePresenter.TYPE_MainContent, this);
    }

    public void loadVMStatus(final String serverName) {


        if(!serverSelection.isActive()) {
            Console.warning(Console.CONSTANTS.common_err_server_not_active());
            return;
        }

        createLoadMetricCmd().execute(new SimpleCallback<CompositeVMMetric>() {


            @Override
            public void onFailure(Throwable caught) {
                Console.error(Console.MESSAGES.failed("JVM Status ")+serverName, caught.getMessage());
            }

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


}
