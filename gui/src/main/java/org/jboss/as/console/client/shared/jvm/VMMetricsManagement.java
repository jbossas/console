package org.jboss.as.console.client.shared.jvm;

import org.jboss.dmr.client.ModelNode;

/**
 * @author Heiko Braun
 * @date 10/7/11
 */
public interface VMMetricsManagement {
    void loadVMStatus();

    void keepPolling(boolean b);
}
