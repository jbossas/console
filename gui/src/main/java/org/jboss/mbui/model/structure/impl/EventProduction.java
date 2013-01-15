package org.jboss.mbui.model.structure.impl;

import org.jboss.mbui.model.behaviour.TriggerSource;
import org.jboss.mbui.model.behaviour.Trigger;
import org.jboss.mbui.model.behaviour.TriggerType;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Heiko Braun
 * @date 10/31/12
 */
public class EventProduction implements TriggerSource {

    private Set<Trigger<TriggerType>> producedTypes;
    private TriggerType type;

    public EventProduction(TriggerType type) {
        this.type = type;
    }

    @Override
    public boolean doesTrigger() {
        return producedTypes!=null && !producedTypes.isEmpty();
    }

    @Override
    public void setOutputs(Trigger<TriggerType>... events) {
        this.producedTypes = new HashSet<Trigger<TriggerType>>();
        for(Trigger<TriggerType> event : events)
            this.producedTypes.add(event);
    }

    @Override
    public Set<Trigger<TriggerType>> getOutputs() {
        return producedTypes;
    }
}
