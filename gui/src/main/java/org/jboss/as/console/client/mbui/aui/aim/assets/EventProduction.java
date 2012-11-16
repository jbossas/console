package org.jboss.as.console.client.mbui.aui.aim.assets;

import org.jboss.as.console.client.mbui.aui.aim.Event;
import org.jboss.as.console.client.mbui.aui.aim.EventProducer;
import org.jboss.as.console.client.mbui.aui.aim.EventType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Heiko Braun
 * @date 10/31/12
 */
public class EventProduction implements EventProducer {

    private Set<Event<EventType>> producedTypes;
    private EventType type;

    public EventProduction(EventType type) {
        this.type = type;
    }

    @Override
    public boolean doesProduceEvents() {
        return producedTypes!=null && !producedTypes.isEmpty();
    }

    @Override
    public void setProducedEvents(Event<EventType>... events) {
        this.producedTypes = new HashSet<Event<EventType>>();
        for(Event<EventType> event : events)
            this.producedTypes.add(event);
    }

    @Override
    public Set<Event<EventType>> getProducedEvents() {
        return producedTypes;
    }
}
