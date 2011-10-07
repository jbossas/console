package org.jboss.as.console.client.shared.jvm;

import org.jboss.as.console.client.shared.jvm.model.HeapMetric;
import org.jboss.as.console.client.shared.jvm.model.OSMetric;
import org.jboss.as.console.client.shared.jvm.model.RuntimeMetric;
import org.jboss.as.console.client.shared.jvm.model.ThreadMetric;

/**
 * @author Heiko Braun
 * @date 10/7/11
 */
public interface VMView {

    void setHeap(HeapMetric heap);
    void setNonHeap(HeapMetric nonHeap);
    void setThreads(ThreadMetric thread);
    void setOSMetric(OSMetric osMetric);
    void setRuntimeMetric(RuntimeMetric runtime);

    void attachCharts();
    void detachCharts();

    void reset();
}
