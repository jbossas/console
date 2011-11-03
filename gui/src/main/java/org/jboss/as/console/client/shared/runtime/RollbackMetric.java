package org.jboss.as.console.client.shared.runtime;

/**
 * @author Heiko Braun
 * @date 10/25/11
 */
public final class RollbackMetric {

    long appRollback;
    long resourceRollback;

    public RollbackMetric(long appRollback, long resourceRollback) {
        this.appRollback = appRollback;
        this.resourceRollback = resourceRollback;
    }

    public long getAppRollback() {
        return appRollback;
    }

    public long getResourceRollback() {
        return resourceRollback;
    }
}
