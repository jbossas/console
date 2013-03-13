package org.jboss.as.console.client.shared.jvm;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.util.AddressableModelCmd;
import org.jboss.dmr.client.dispatch.AsyncCommand;
import org.jboss.dmr.client.dispatch.DispatchAsync;
import org.jboss.dmr.client.dispatch.impl.DMRAction;
import org.jboss.dmr.client.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.shared.jvm.model.CompositeVMMetric;
import org.jboss.as.console.client.shared.jvm.model.HeapMetric;
import org.jboss.as.console.client.shared.jvm.model.OSMetric;
import org.jboss.as.console.client.shared.jvm.model.RuntimeMetric;
import org.jboss.as.console.client.shared.jvm.model.ThreadMetric;
import org.jboss.as.console.client.shared.model.ModelAdapter;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.as.console.client.widgets.forms.EntityAdapter;
import org.jboss.dmr.client.ModelNode;

import java.util.ArrayList;
import java.util.List;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * @author Heiko Braun
 * @date 10/7/11
 */
public class LoadJVMMetricsCmd extends AddressableModelCmd implements AsyncCommand<CompositeVMMetric> {

    private EntityAdapter<HeapMetric> heapMetricAdapter;
    private EntityAdapter<ThreadMetric> threadMetricAdapter;
    private EntityAdapter<RuntimeMetric> runtimeAdapter;
    private EntityAdapter<OSMetric> osAdapter;


    public LoadJVMMetricsCmd(
            DispatchAsync dispatcher,
            BeanFactory factory,
            ModelNode address,
            ApplicationMetaData metaData)
    {
        super(dispatcher, factory, address);

        this.heapMetricAdapter = new EntityAdapter<HeapMetric>(HeapMetric.class, metaData);
        this.threadMetricAdapter = new EntityAdapter<ThreadMetric>(ThreadMetric.class, metaData);
        this.runtimeAdapter = new EntityAdapter<RuntimeMetric>(RuntimeMetric.class, metaData);
        this.osAdapter = new EntityAdapter<OSMetric>(OSMetric.class, metaData);
    }

    @Override
    public void execute(final AsyncCallback<CompositeVMMetric> callback) {

        ModelNode composite = new ModelNode();
        composite.get(OP).set(COMPOSITE);
        composite.get(ADDRESS).setEmptyList();

        List<ModelNode> steps = new ArrayList<ModelNode>();

        // memory

        ModelNode memory = new ModelNode();
        memory.get(ADDRESS).set(address);
        memory.get(ADDRESS).add("core-service", "platform-mbean");
        memory.get(ADDRESS).add("type", "memory");
        memory.get(OP).set(READ_RESOURCE_OPERATION);
        memory.get(INCLUDE_RUNTIME).set(true);

        steps.add(memory);

        // threads

        ModelNode threads = new ModelNode();
        threads.get(ADDRESS).set(address);
        threads.get(ADDRESS).add("core-service", "platform-mbean");
        threads.get(ADDRESS).add("type", "threading");
        threads.get(OP).set(READ_RESOURCE_OPERATION);
        threads.get(INCLUDE_RUNTIME).set(true);

        steps.add(threads);


        // runtime

        ModelNode runtime = new ModelNode();
        runtime.get(ADDRESS).set(address);
        runtime.get(ADDRESS).add("core-service", "platform-mbean");
        runtime.get(ADDRESS).add("type", "runtime");
        runtime.get(OP).set(READ_RESOURCE_OPERATION);
        runtime.get(INCLUDE_RUNTIME).set(true);

        steps.add(runtime);

        // OS

        ModelNode os = new ModelNode();
        os.get(ADDRESS).set(address);
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
                ModelNode response = result.get();

                ModelNode steps = response.get(RESULT);

                CompositeVMMetric metric = new CompositeVMMetric();

                if(ModelAdapter.wasSuccess(response))
                {
                    // memory
                    ModelNode memory  = steps.get("step-1").get(RESULT);

                    HeapMetric heap = heapMetricAdapter.fromDMR(memory.get("heap-memory-usage"));
                    HeapMetric nonHeap = heapMetricAdapter.fromDMR(memory.get("non-heap-memory-usage"));


                    metric.setHeap(heap);
                    metric.setNonHeap(nonHeap);

                    // threads

                    ModelNode threads = steps.get("step-2").get(RESULT);
                    ThreadMetric thread = threadMetricAdapter.fromDMR(threads);

                    metric.setThreads(thread);

                    // runtime

                    ModelNode runtime = steps.get("step-3").get(RESULT);
                    RuntimeMetric runtimeMetric = runtimeAdapter.fromDMR(runtime);

                    metric.setRuntime(runtimeMetric);

                    // os

                    ModelNode os = steps.get("step-4").get(RESULT);
                    OSMetric osMetric = osAdapter.fromDMR(os);

                    metric.setOs(osMetric);


                    callback.onSuccess(metric);


                }
                else
                {
                    callback.onFailure(new RuntimeException("The server doesn't seem to be running: "+address));
                    Log.error("Failed to load server status: "+ response.getFailureDescription());

                    //callback.onFailure(new RuntimeException("Failed to load VM metrics: "+response.toString()));
                }
            }
        });
    }
}
