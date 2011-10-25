package org.jboss.as.console.client.shared.subsys.tx.model;

import org.jboss.as.console.client.widgets.forms.Address;
import org.jboss.as.console.client.widgets.forms.Binding;

/**
 * @author Heiko Braun
 * @date 10/25/11
 */
@Address("/subsystem=transaction")
public interface TransactionManager {

    @Binding(detypedName = "socket-binding")
    String getSocketBinding();
    void setSocketBinding(String s);

    @Binding(detypedName = "status-socket-binding")
    String getStatusSocketBinding();
    void setStatusSocketBinding(String s);

    @Binding(detypedName = "default-timeout")
    int getDefaultTimeout();
    void setDefaultTimeout(int t);

    @Binding(detypedName = "enable-statistics")
    boolean isEnableStatistics();
    void setEnableStatistics(boolean b);

    @Binding(detypedName = "enable-tsm-status")
    boolean isEnableTsmStatus();
    void setEnableTsmStatus(boolean b);

    @Binding(detypedName = "recovery-listener")
    boolean isRecoveryListener();
    void setRecoveryListener(boolean b);


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
