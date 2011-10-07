package org.jboss.as.console.client.shared.jvm;

import com.gwtplatform.mvp.client.View;
import org.jboss.as.console.client.shared.jvm.model.HeapMetric;
import org.jboss.as.console.client.shared.jvm.model.OSMetric;
import org.jboss.as.console.client.shared.jvm.model.RuntimeMetric;
import org.jboss.as.console.client.shared.jvm.model.ThreadMetric;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 10/7/11
 */
public interface VMView extends View  {

    void setPresenter(VMMetricsManagement presenter);

    void setHeap(HeapMetric heap);
    void setNonHeap(HeapMetric nonHeap);
    void setThreads(ThreadMetric thread);
    void setOSMetric(OSMetric osMetric);
    void setRuntimeMetric(RuntimeMetric runtime);

    void attachCharts();
    void detachCharts();

    void reset();

    void setVMKeys(List<String> vmkeys);
}
