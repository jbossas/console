package org.jboss.as.console.client.shared.subsys.messaging.model;

import org.jboss.as.console.client.widgets.forms.Binding;

/**
 * @author Heiko Braun
 * @date 4/18/12
 */
public interface ClusterConnection {

    @Binding(skip = true)
    String getName();
    void setName(String name);

    @Binding(detypedName = "call-timeout")
    Long getCallTimeout();
    void setCallTimeout(Long timeout);

    @Binding(detypedName = "check-period")
    Long getCheckPeriod();
    void setCheckPeriod(Long period);

    @Binding(detypedName = "cluster-connection-address")
    String getClusterConnectionAddress();
    void setClusterConnectionAddress(String name);

    @Binding(detypedName = "connection-ttl")
    Long getConnectionTTL();
    void setConnectionTTL(Long ttl);

    @Binding(detypedName = "connector-ref")
    String getConnectorRef();
    void setConnectorRef(String name);

    @Binding(detypedName = "discovery-group-name")
    String getDiscoveryGroupName();
    void setDiscoveryGroupName(String name);

    @Binding(detypedName = "forward-when-no-consumers")
    boolean isForwardWhenNoConsumers();
    void setForwardWhenNoConsumers(boolean b);

    @Binding(detypedName = "max-hops")
    Long getMaxHops();
    void setMaxHops(Long maxhops);

    @Binding(detypedName = "max-retry-interval")
    Long getMaxRetryInterval();
    void setMaxRetryInterval(Long interval);

    @Binding(detypedName = "reconnect-attempts")
    Long getReconnectAttempts();
    void setReconnectAttempts(Long numAttempts);

    @Binding(detypedName = "retry-interval")
    Long getRetryInterval();
    void setRetryInterval(Long interval);

    @Binding(detypedName = "use-duplicate-detection")
    boolean isDuplicateDetection();
    void setDuplicateDetection(boolean b);

    @Binding(detypedName = "allow-direct-connections-only")
    boolean isAllowDirect();
    void setAllowDirect(boolean b);

}
