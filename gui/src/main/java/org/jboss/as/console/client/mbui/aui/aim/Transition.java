package org.jboss.as.console.client.mbui.aui.aim;

import org.jboss.as.console.client.mbui.aui.aim.assets.EventProduction;

import java.util.Set;

/**
 * @author Heiko Braun
 * @date 10/31/12
 */
public abstract class Transition implements EventProducer {

    private String id;

    private EventProduction eventProduction =
            new EventProduction(EventType.Transition);

    public abstract void perform();

    @Override
    public boolean doesProduceEvents() {
        return eventProduction.doesProduceEvents();
    }

    @Override
    public void setProducedEvents(Event<EventType>... events) {
        this.eventProduction.setProducedEvents(events);
    }

    public Set<Event<EventType>> getProducedEvents()
    {
        return eventProduction.getProducedEvents();
    }
}
