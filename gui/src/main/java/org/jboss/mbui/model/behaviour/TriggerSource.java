package org.jboss.mbui.model.behaviour;

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
