package org.jboss.as.console.client.shared.jvm.model;

import org.jboss.as.console.client.widgets.forms.Binding;

/**
 * @author Heiko Braun
 * @date 9/29/11
 */
public interface RuntimeMetric {

    @Binding(detypedName = "vm-name")
    String getVmName();
    void setVmName(String name);

    @Binding(detypedName = "start-time")
    long getStartTime();
    void setStartTime(long time);

    long getUptime();
    void setUptime(long time);
}
