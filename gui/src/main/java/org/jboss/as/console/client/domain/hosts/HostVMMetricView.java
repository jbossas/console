package org.jboss.as.console.client.domain.hosts;

import org.jboss.as.console.client.standalone.runtime.VMMetricsView;

/**
 * @author Heiko Braun
 * @date 10/7/11
 */
public class HostVMMetricView extends VMMetricsView implements HostVMMetricPresenter.MyView{

    public HostVMMetricView() {
        this.hasServerPicker = true;
    }


}
