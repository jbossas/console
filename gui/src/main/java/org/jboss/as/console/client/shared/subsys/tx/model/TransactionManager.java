package org.jboss.as.console.client.shared.subsys.tx.model;

import org.jboss.as.console.client.widgets.forms.Address;
import org.jboss.as.console.client.widgets.forms.Binding;

/**
 * @author Heiko Braun
 * @date 10/25/11
 */
@Address("/subsystem=transactions")
public interface TransactionManager {

    @Binding(detypedName = "default-timeout", expr = true)
    int getDefaultTimeout();
    void setDefaultTimeout(int t);

    @Binding(detypedName = "enable-statistics", expr = true)
    boolean isEnableStatistics();
    void setEnableStatistics(boolean b);

    @Binding(detypedName = "enable-tsm-status", expr = true)
    boolean isEnableTsmStatus();
    void setEnableTsmStatus(boolean b);

    @Binding(detypedName = "jts", expr = true)
    boolean isJts();
    void setJts(boolean b);

    @Binding(detypedName = "node-identifier", expr = true)
    String getNodeIdentifier();
    void setNodeIdentifier(String s);

    @Binding(detypedName = "object-store-path", expr = true)
    String getObjectStorePath();
    void setObjectStorePath(String s);

    @Binding(detypedName = "object-store-relative-to", expr = true)
    String getObjectStoreRelativeTo();
    void setObjectStoreRelativeTo(String s);

    @Binding(expr = true)
    String getPath();
    void setPath(String s);

    @Binding(detypedName = "process-id-socket-binding", expr=true)
    String getProcessIdSocketBinding();
    void setProcessIdSocketBinding(String s);

    @Binding(detypedName = "process-id-socket-max-ports", expr = true)
    int getProcessIdMaxPorts();
    void setProcessIdMaxPorts(int i);

    @Binding(detypedName = "process-id-uuid")
    boolean isProcessIdUUID();
    void setProcessIdUUID(boolean b);


    @Binding(detypedName = "recovery-listener", expr=true)
    boolean isRecoveryListener();
    void setRecoveryListener(boolean b);

    @Binding(detypedName = "relative-to", expr = true)
    String getRelativeTo();
    void setRelativeTo(String s);

    @Binding(detypedName = "socket-binding", expr = true)
    String getSocketBinding();
    void setSocketBinding(String s);

    @Binding(detypedName = "status-socket-binding", expr = true)
    String getStatusSocketBinding();
    void setStatusSocketBinding(String s);

    @Binding(detypedName = "use-hornetq-store", expr=true)
    boolean isHornetqStore();
    void setHornetqStore(boolean b);

    // Metrics

    @Binding(detypedName = "number-of-nested-transactions")
    long getNumNestedTransactions();
    void setNumNestedTransactions(long l);

    @Binding(detypedName = "number-of-timed-out-transactions")
    long getNumTimeoutTransactions();
    void setNumTimeoutTransactions(long l);

    @Binding(detypedName = "number-of-transactions")
    long getNumTransactions();
    void setNumTransactions(long l);

    @Binding(detypedName = "number-of-committed-transactions")
    long getNumCommittedTransactions();
    void setNumCommittedTransactions(long l);

    @Binding(detypedName = "number-of-aborted-transactions")
    long getNumAbortedTransactions();
    void setNumAbortedTransactions(long l);

    @Binding(detypedName = "number-of-inflight-transactions")
    long getNumInflightTransactions();
    void setNumInflightTransactions(long l);

    @Binding(detypedName = "number-of-application-rollbacks")
    long getNumApplicationRollback();
    void setNumApplicationRollback(long l);

    @Binding(detypedName = "number-of-resource-rollbacks")
    long getNumResourceRollback();
    void setNumResourceRollback(long l);

    @Binding(detypedName = "number-of-heuristics")
    long getNumHeuristics();
    void setNumHeuristics(long l);

}
