package org.jboss.as.console.client.shared.jvm.model;

/**
 * @author Heiko Braun
 * @date 10/7/11
 */
public class CompositeVMMetric {

    HeapMetric nonHeap;
    HeapMetric heap;
    OSMetric os;
    RuntimeMetric runtime;
    ThreadMetric threads;

    public CompositeVMMetric() {
    }

    public CompositeVMMetric(HeapMetric heap, OSMetric os, RuntimeMetric runtime, ThreadMetric threads) {
        this.heap = heap;
        this.os = os;
        this.runtime = runtime;
        this.threads = threads;
    }

    public void setHeap(HeapMetric heap) {
        this.heap = heap;
    }

    public HeapMetric getNonHeap() {
        return nonHeap;
    }

    public void setNonHeap(HeapMetric nonHeap) {
        this.nonHeap = nonHeap;
    }

    public void setOs(OSMetric os) {
        this.os = os;
    }

    public void setRuntime(RuntimeMetric runtime) {
        this.runtime = runtime;
    }

    public void setThreads(ThreadMetric threads) {
        this.threads = threads;
    }

    public HeapMetric getHeap() {
        return heap;
    }

    public OSMetric getOs() {
        return os;
    }

    public RuntimeMetric getRuntime() {
        return runtime;
    }

    public ThreadMetric getThreads() {
        return threads;
    }
}
