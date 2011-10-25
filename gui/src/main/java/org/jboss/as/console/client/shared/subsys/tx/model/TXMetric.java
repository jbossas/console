package org.jboss.as.console.client.shared.subsys.tx.model;

/**
 * @author Heiko Braun
 * @date 10/25/11
 */
public final class TXMetric {

    long total;
    long committed;
    long aborted;
    long timedOut;

    public TXMetric(long total, long committed, long aborted, long timedOut) {
        this.total = total;
        this.committed = committed;
        this.aborted = aborted;
        this.timedOut = timedOut;
    }

    public long getTotal() {
        return total;
    }

    public long getCommitted() {
        return committed;
    }

    public long getAborted() {
        return aborted;
    }

    public long getTimedOut() {
        return timedOut;
    }
}
