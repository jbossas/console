package org.jboss.as.console.client.mbui.aui.aim;

import java.util.Set;

/**
 * @author Heiko Braun
 * @date 10/31/12
 */
public interface TriggerSource {

    boolean doesTrigger();

    void setOutputs(Trigger<TriggerType>... events);

    public Set<Trigger<TriggerType>> getOutputs();
}
