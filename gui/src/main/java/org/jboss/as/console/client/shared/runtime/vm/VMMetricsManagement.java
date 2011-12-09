package org.jboss.as.console.client.shared.runtime.vm;

/**
 * @author Heiko Braun
 * @date 10/7/11
 */
public interface VMMetricsManagement {
    void keepPolling(boolean b);
    void onServerSelection(String serverName);
    void refresh();
}
