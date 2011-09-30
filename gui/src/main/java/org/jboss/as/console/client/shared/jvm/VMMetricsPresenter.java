package org.jboss.as.console.client.shared.jvm;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
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
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.shared.jvm.model.HeapMetric;
import org.jboss.as.console.client.shared.jvm.model.OSMetric;
import org.jboss.as.console.client.shared.jvm.model.RuntimeMetric;
import org.jboss.as.console.client.shared.jvm.model.ThreadMetric;
import org.jboss.as.console.client.shared.model.ModelAdapter;
import org.jboss.as.console.client.shared.subsys.RevealStrategy;
import org.jboss.as.console.client.standalone.ServerMgmtApplicationPresenter;
import org.jboss.as.console.client.widgets.forms.EntityAdapter;
import org.jboss.as.console.client.widgets.forms.PropertyMetaData;
import org.jboss.dmr.client.ModelNode;

import java.util.ArrayList;
import java.util.List;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * @author Heiko Braun
 * @date 9/28/11
 */
public class VMMetricsPresenter extends Presenter<VMMetricsPresenter.MyView, VMMetricsPresenter.MyProxy> {

    private static final int POLL_INTERVAL = 5000;
    private DispatchAsync dispatcher;
    private BeanFactory factory;
    private PropertyMetaData metaData;
    private RevealStrategy revealStrategy;

    private EntityAdapter<HeapMetric> heapMetricAdapter;
    private EntityAdapter<ThreadMetric> threadMetricAdapter;
    private EntityAdapter<RuntimeMetric> runtimeAdapter;
    private EntityAdapter<OSMetric> osAdapter;

    private boolean keepPolling = true;
    private Scheduler.RepeatingCommand pollCmd = null;

    @ProxyCodeSplit
    @NameToken(NameTokens.VirtualMachine)
    public interface MyProxy extends Proxy<VMMetricsPresenter>, Place {
    }

    public interface MyView extends View {
        void setPresenter(VMMetricsPresenter presenter);

        void setHeap(HeapMetric heap);
        void setNonHeap(HeapMetric nonHeap);
        void setThreads(ThreadMetric thread);
        void setRuntimeMetric(RuntimeMetric runtime);

        void attachCharts();
        void detachCharts();

        void setOSMetric(OSMetric osMetric);

        void reset();
    }

    @Inject
    public VMMetricsPresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            RevealStrategy revealStrategy,
            DispatchAsync dispatcher, BeanFactory factory, PropertyMetaData propertyMetaData) {
        super(eventBus, view, proxy);

        this.revealStrategy = revealStrategy;
        this.metaData = propertyMetaData;
        this.factory = factory;
        this.dispatcher = dispatcher;

        this.heapMetricAdapter = new EntityAdapter<HeapMetric>(HeapMetric.class, metaData);
        this.threadMetricAdapter = new EntityAdapter<ThreadMetric>(ThreadMetric.class, metaData);
        this.runtimeAdapter = new EntityAdapter<RuntimeMetric>(RuntimeMetric.class, metaData);
        this.osAdapter = new EntityAdapter<OSMetric>(OSMetric.class, metaData);
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
    }

    @Override
    protected void onHide() {
        super.onHide();

        getView().detachCharts();
    }


    @Override
    protected void onReset() {
        super.onReset();


        getView().reset();

        if(Console.visAPILoaded())
            getView().attachCharts();
        else
            Console.error("Failed load visualization API", "Charts will not be available.");

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


    public void loadVMStatus() {

        ModelNode composite = new ModelNode();
        composite.get(OP).set(COMPOSITE);
        composite.get(ADDRESS).setEmptyList();

        List<ModelNode> steps = new ArrayList<ModelNode>();

        // memory

        ModelNode memory = new ModelNode();
        memory.get(ADDRESS).add("core-service", "platform-mbean");
        memory.get(ADDRESS).add("type", "memory");
        memory.get(OP).set(READ_RESOURCE_OPERATION);
        memory.get(INCLUDE_RUNTIME).set(true);

        steps.add(memory);

        // threads

        ModelNode threads = new ModelNode();
        threads.get(ADDRESS).add("core-service", "platform-mbean");
        threads.get(ADDRESS).add("type", "threading");
        threads.get(OP).set(READ_RESOURCE_OPERATION);
        threads.get(INCLUDE_RUNTIME).set(true);

        steps.add(threads);


        // runtime

        ModelNode runtime = new ModelNode();
        runtime.get(ADDRESS).add("core-service", "platform-mbean");
        runtime.get(ADDRESS).add("type", "runtime");
        runtime.get(OP).set(READ_RESOURCE_OPERATION);
        runtime.get(INCLUDE_RUNTIME).set(true);

        steps.add(runtime);

        // OS

        ModelNode os = new ModelNode();
        os.get(ADDRESS).add("core-service", "platform-mbean");
        os.get(ADDRESS).add("type", "operating-system");
        os.get(OP).set(READ_RESOURCE_OPERATION);
        os.get(INCLUDE_RUNTIME).set(true);

        steps.add(os);

        composite.get(STEPS).set(steps);

        dispatcher.execute(new DMRAction(composite), new SimpleCallback<DMRResponse>() {

            @Override
            public void onFailure(Throwable caught) {
                Console.error("Error loading VM metrics", caught.getMessage());
            }

            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = ModelNode.fromBase64(result.getResponseText());
                ModelNode steps = response.get(RESULT);

                //System.out.println(steps);

                if(ModelAdapter.wasSuccess(response))
                {
                    // memory

                    ModelNode memory  = steps.get("step-1").get(RESULT);
                    HeapMetric heap = heapMetricAdapter.fromDMR(memory.get("heap-memory-usage"));
                    HeapMetric nonHeap = heapMetricAdapter.fromDMR(memory.get("non-heap-memory-usage"));

                    getView().setHeap(heap);
                    getView().setNonHeap(nonHeap);

                    // threads

                    ModelNode threads = steps.get("step-2").get(RESULT);
                    ThreadMetric thread = threadMetricAdapter.fromDMR(threads);

                    getView().setThreads(thread);

                    // runtime

                    ModelNode runtime = steps.get("step-3").get(RESULT);
                    RuntimeMetric runtimeMetric = runtimeAdapter.fromDMR(runtime);

                    getView().setRuntimeMetric(runtimeMetric);

                    // os

                    ModelNode os = steps.get("step-4").get(RESULT);
                    OSMetric osMetric = osAdapter.fromDMR(os);

                    getView().setOSMetric(osMetric);
                }
                else
                {
                    Console.error("Failed to load VM metrics", response.toString());
                }
            }
        });
    }

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(getEventBus(), ServerMgmtApplicationPresenter.TYPE_MainContent, this);
    }

    public void keepPolling(boolean b) {

        this.keepPolling = b;

        if(keepPolling && pollCmd==null)
            beginPolling();
        else if(!keepPolling && pollCmd!=null)
            pollCmd=null;

    }

}
