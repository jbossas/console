package org.jboss.as.console.client.shared.subsys.messaging.model;

import org.jboss.as.console.client.widgets.forms.Binding;

/**
 * @author Heiko Braun
 * @date 4/17/12
 */
public interface Bridge {

    @Binding(skip = true)
    String getName();
    void setName(String name);

    @Binding(detypedName = "queue-name")
    String getQueueName();
    void setQueueName(String name);

    @Binding(detypedName = "forwarding-address")
    String getForwardingAddress();
    void setForwardingAddress(String forwardTo);

    boolean isHa();
    void setHa(boolean b);

    @Binding(detypedName = "filter")
    String getFilter();
    void setFilter(String filterString);

    @Binding(detypedName = "transformer-class-name")
    String getTransformerClass();
    void setTransformerClass(String classname);

    @Binding(detypedName = "retry-interval")
    Long getRetryInterval();
    void setRetryInterval(Long interval);

    @Binding(detypedName = "retry-interval-multiplier")
    Long getRetryIntervalMultiplier();
    void setRetryIntervalMultiplier(Long multiplier);

    @Binding(detypedName = "reconnect-attempts")
    Long getReconnectAttempts();
    void setReconnectAttempts(Long numAttempts);

    @Binding(detypedName = "failover-on-server-shutdown")
    boolean isFailoverShutdown();
    void setFailoverShutdown(boolean b);

    @Binding(detypedName = "use-duplicate-detection")
    boolean isDuplicateDetection();
    void setDuplicateDetection(boolean b);

    String getUser();
    void setUser(String user);

    String getPassword();
    void setPassword(String pass);

    boolean isStarted();
    void setStarted(boolean b);
}
