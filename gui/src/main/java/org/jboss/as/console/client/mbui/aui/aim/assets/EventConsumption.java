package org.jboss.as.console.client.mbui.aui.aim.assets;

import org.jboss.as.console.client.mbui.aui.aim.Event;
import org.jboss.as.console.client.mbui.aui.aim.EventConsumer;
import org.jboss.as.console.client.mbui.aui.aim.EventType;

/**
 * @author Heiko Braun
 * @date 10/31/12
 */
public class EventConsumption implements EventConsumer {

    private EventType[] consumedTypes;

    public EventConsumption(EventType... consumedTypes) {
        this.consumedTypes = consumedTypes;
    }

    @Override
    public EventType[] getConsumedTypes() {
        return consumedTypes;
    }

    @Override
    public boolean consumes(Event event) {
        boolean match = false;

        for(EventType candidate : consumedTypes)
        {
            if(candidate.equals(event.getType()))
            {
                match = true;
                break;
            }
        }
        return match;
    }
}
