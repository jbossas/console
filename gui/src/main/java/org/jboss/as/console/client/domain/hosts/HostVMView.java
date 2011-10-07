package org.jboss.as.console.client.domain.hosts;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.core.DisposableViewImpl;
import org.jboss.as.console.client.shared.jvm.model.HeapMetric;
import org.jboss.as.console.client.shared.jvm.model.OSMetric;
import org.jboss.as.console.client.shared.jvm.model.RuntimeMetric;
import org.jboss.as.console.client.shared.jvm.model.ThreadMetric;

/**
 * @author Heiko Braun
 * @date 10/7/11
 */
public class HostVMView extends DisposableViewImpl implements HostVMMetricPresenter.MyView{
    @Override
    public void setPresenter(HostVMMetricPresenter presenter) {
        
    }

    @Override
    public void setHeap(HeapMetric heap) {
        
    }

    @Override
    public void setNonHeap(HeapMetric nonHeap) {
        
    }

    @Override
    public void setThreads(ThreadMetric thread) {
        
    }

    @Override
    public void setOSMetric(OSMetric osMetric) {
        
    }

    @Override
    public void setRuntimeMetric(RuntimeMetric runtime) {
        
    }

    @Override
    public void attachCharts() {
        
    }

    @Override
    public void detachCharts() {
        
    }

    @Override
    public void reset() {
        
    }

    @Override
    public Widget createWidget() {
        return new HTML();
    }
}
