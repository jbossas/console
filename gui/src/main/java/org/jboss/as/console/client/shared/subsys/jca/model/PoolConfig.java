package org.jboss.as.console.client.shared.subsys.jca.model;

import org.jboss.as.console.client.widgets.forms.Address;
import org.jboss.as.console.client.widgets.forms.Binding;

/**
 * @author Heiko Braun
 * @date 9/16/11
 */
@Address("/subsystem=datasources/{0}={1}")
public interface PoolConfig {

    @Binding(key = true)
    String getName();
    void setName(String name);

    @Binding(detypedName = "max-pool-size")
    int getMaxPoolSize();
    void setMaxPoolSize(int max);

    @Binding(detypedName = "min-pool-size")
    int getMinPoolSize();
    void setMinPoolSize(int min);

    @Binding(detypedName = "pool-prefill")
    boolean isPoolPrefill();
    void setPoolPrefill(boolean b);

    @Binding(detypedName = "pool-use-strict-min")
    boolean isPoolStrictMin();
    void setPoolStrictMin(boolean b);


    // metrics below

    @Binding(skip = true)
    int getActiveCount();
    void setActiveCount(int i);

    @Binding(skip = true)
    int getCreatedCount();
    void setCreatedCount(int i);

    @Binding(skip = true)
    int getMaxWaitCount();
    void setMaxWaitCount(int i);

    @Binding(skip = true)
    int getAvailbleCount();
    void setAvailbleCount(int i);

    @Binding(skip = true)
    int getMaxUsedCount();
    void setMaxUsedCount(int i);


}

