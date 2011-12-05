package org.jboss.as.console.client.shared.subsys.jca.model;

import org.jboss.as.console.client.shared.subsys.threads.model.BoundedQueueThreadPool;
import org.jboss.as.console.client.widgets.forms.Binding;

/**
 * @author Heiko Braun
 * @date 12/5/11
 */
public interface WorkmanagerPool extends BoundedQueueThreadPool {

    @Binding(skip = true)
    boolean isShortRunning();
    void setShortRunning(boolean b);
}
