package org.jboss.mbui.client.aui.aim.assets;

import org.jboss.mbui.client.aui.aim.Event;
import org.jboss.mbui.client.aui.aim.EventProducer;
import org.jboss.mbui.client.aui.aim.EventType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 10/31/12
 */
public class EventProduction<T extends EventType> implements EventProducer<T> {

    private List<Event<T>> eventsProduced = Collections.EMPTY_LIST;

    @Override
    public boolean doesProduceEvents() {
        return !eventsProduced.isEmpty();
    }

    @Override
    public void setProducedEvents(Event<T>... events) {
        this.eventsProduced = new ArrayList<Event<T>>();
        for(Event event : events)
            this.eventsProduced.add(event);
    }
}
