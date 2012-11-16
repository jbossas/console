package org.jboss.as.console.client.mbui.aui.aim.assets;

import org.jboss.as.console.client.mbui.aui.aim.Event;
import org.jboss.as.console.client.mbui.aui.aim.EventConsumer;
import org.jboss.as.console.client.mbui.aui.aim.EventType;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Heiko Braun
 * @date 10/31/12
 */
public class EventConsumption implements EventConsumer {

    private Set<Event<EventType>> consumedTypes;

    @Override
    public Set<Event<EventType>> getTriggers() {
        return consumedTypes;
    }

    @Override
    public boolean isTriggeredBy(Event<EventType> event) {
        boolean match = false;

        if(consumedTypes!=null)
        {
            for(Event<EventType> candidate : consumedTypes)
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
    public void setTriggers(Event<EventType>... trigger) {
        this.consumedTypes = new HashSet<Event<EventType>>();
        for(Event<EventType> event : trigger)
            consumedTypes.add(event);
    }
}
