package org.jboss.as.console.client.shared.runtime.vm;

import com.gwtplatform.mvp.client.View;
import org.jboss.as.console.client.domain.model.ServerInstance;
import org.jboss.as.console.client.shared.jvm.model.OSMetric;
import org.jboss.as.console.client.shared.jvm.model.RuntimeMetric;
import org.jboss.as.console.client.shared.runtime.Metric;
import org.jboss.as.console.client.shared.runtime.vm.VMMetricsManagement;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 10/7/11
 */
public interface VMView extends View {

    void setPresenter(VMMetricsManagement presenter);

    void setHeap(Metric heap);
    void setNonHeap(Metric nonHeap);
    void setThreads(Metric thread);
    void setOSMetric(OSMetric osMetric);
    void setRuntimeMetric(RuntimeMetric runtime);
}
