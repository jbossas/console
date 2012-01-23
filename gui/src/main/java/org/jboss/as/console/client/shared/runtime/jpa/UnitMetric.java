package org.jboss.as.console.client.shared.runtime.jpa;

import org.jboss.as.console.client.shared.runtime.Metric;

/**
 * @author Heiko Braun
 * @date 1/20/12
 */
public class UnitMetric {


    private Metric txMetric;
    private Metric queryMetric;
    private Metric queryExecMetric;
    private Metric secondLevelCacheMetric;
    private Metric connectionMetric;

    private boolean isEnabled;

    public UnitMetric(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public UnitMetric(Metric txMetric, Metric queryCacheMetric, Metric queryExecMetric, Metric secondLevelCacheMetric, Metric connectionMetric) {
        this.txMetric = txMetric;
        this.queryMetric = queryCacheMetric;
        this.queryExecMetric = queryExecMetric;
        this.secondLevelCacheMetric = secondLevelCacheMetric;
        this.connectionMetric = connectionMetric;
        this.isEnabled = true;
    }

    public Metric getTxMetric() {
        return txMetric;
    }

    public Metric getQueryMetric() {
        return queryMetric;
    }

    public Metric getQueryExecMetric() {
        return queryExecMetric;
    }

    public Metric getSecondLevelCacheMetric() {
        return secondLevelCacheMetric;
    }

    public Metric getConnectionMetric() {
        return connectionMetric;
    }

    public boolean isEnabled() {
        return isEnabled;
    }
}
