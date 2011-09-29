package org.jboss.as.console.client.shared.jvm.model;

import org.jboss.as.console.client.widgets.forms.Binding;

/**
 * @author Heiko Braun
 * @date 9/29/11
 */
public interface ThreadMetric {

    @Binding(detypedName = "thread-count")
    long getCount();
    void setCount(long l);

    @Binding(detypedName = "peak-thread-count")
    long getPeakCount();
    void setPeakCount(long l);

    @Binding(detypedName = "daemon-thread-count")
    long getDaemonCount();
    void setDaemonCount(long l);

    @Binding(detypedName = "total-started-thread-count")
    long getTotalStarted();
    void setTotalStarted(long l);


}
