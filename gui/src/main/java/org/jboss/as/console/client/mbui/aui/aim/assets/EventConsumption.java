package org.jboss.as.console.client.mbui.aui.aim.assets;

import org.jboss.as.console.client.mbui.aui.aim.TriggerTarget;
import org.jboss.as.console.client.mbui.aui.aim.Trigger;
import org.jboss.as.console.client.mbui.aui.aim.TriggerType;

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
