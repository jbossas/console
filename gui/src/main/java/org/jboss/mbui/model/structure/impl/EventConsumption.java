package org.jboss.mbui.model.structure.impl;

import org.jboss.mbui.model.behaviour.TriggerTarget;
import org.jboss.mbui.model.behaviour.Trigger;
import org.jboss.mbui.model.behaviour.TriggerType;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Heiko Braun
 * @date 10/31/12
 */
public class EventConsumption implements TriggerTarget {

    private Set<Trigger<TriggerType>> consumedTypes;

    @Override
    public Set<Trigger<TriggerType>> getInputs() {
        return consumedTypes;
    }

    @Override
    public boolean isTriggeredBy(Trigger<TriggerType> event) {
        boolean match = false;

        if(consumedTypes!=null)
        {
            for(Trigger<TriggerType> candidate : consumedTypes)
            {
                if(candidate.equals(event))
                {
                    match = true;
                    break;
                }
            }
        }
        return match;
    }

    @Override
    public void setInputs(Trigger<TriggerType>... trigger) {
        this.consumedTypes = new HashSet<Trigger<TriggerType>>();
        for(Trigger<TriggerType> event : trigger)
            consumedTypes.add(event);
    }
}
