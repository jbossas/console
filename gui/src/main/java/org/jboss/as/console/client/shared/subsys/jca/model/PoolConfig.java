package org.jboss.as.console.client.shared.subsys.jca.model;

import org.jboss.as.console.client.widgets.forms.Binding;

/**
 * @author Heiko Braun
 * @date 9/16/11
 */
public interface PoolConfig {

    String getName();
    void setName(String name);

    @Binding(detypedName = "max-pool-size")
    int getMaxPoolSize();
    void setMaxPoolSize(int max);

    @Binding(detypedName = "min-pool-size")
    int getMinPoolSize();
    void setMinPoolSize(int min);

    @Binding(detypedName = "pool-prefill")
    boolean getPoolPrefill();
    void setPoolPrefill(boolean b);

    @Binding(detypedName = "pool-use-strict-min")
    boolean getPoolStrictMin();
    void setPoolStrictMin(boolean b);


    // metrics below

    @Binding(detypedName = "none", ignore = true)
    int getActiveCount();
    void setActiveCount(int i);

    @Binding(detypedName = "none", ignore = true)
    int getCreatedCount();
    void setCreatedCount(int i);

    @Binding(detypedName = "none", ignore = true)
    int getMaxWaitCount();
    void setMaxWaitCount(int i);

    @Binding(detypedName = "none", ignore = true)
    int getAvailbleCount();
    void setAvailbleCount(int i);

    @Binding(detypedName = "none", ignore = true)
    int getMaxUsedCount();
    void setMaxUsedCount(int i);


}

