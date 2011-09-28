package org.jboss.as.console.client.shared.jvm.model;

/**
 * @author Heiko Braun
 * @date 9/28/11
 */
public interface HeapMetric {

    long getInit();
    void setInit(long l);

    long getUsed();
    void setUsed(long l);

    long getCommitted();
    void setCommitted(long l);

    long getMax();
    void setMax(long l);
}
