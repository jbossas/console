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

    public UnitMetric(Metric txMetric, Metric queryMetric, Metric queryExecMetric, Metric secondLevelCacheMetric) {
        this.txMetric = txMetric;
        this.queryMetric = queryMetric;
        this.queryExecMetric = queryExecMetric;
        this.secondLevelCacheMetric = secondLevelCacheMetric;
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
}
