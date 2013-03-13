package org.jboss.as.console.client.shared.dispatch;

/**
 * @author Heiko Braun
 * @date 3/13/13
 */
public interface Diagnostics {

    public void logError(String module, double millis, String errMsg);

    public void logRpc(String type, String id, double millis, String method);

    public void logRpc(String type, String id, double millis);

    public void logEvent(String moduleName, String subSystem,String eventGroup, double millis, String type);

    public boolean isEnabled();
}
