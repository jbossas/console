package org.jboss.mbui.client.aui.aim;

import org.jboss.mbui.client.aui.aim.assets.EventProduction;

/**
 * @author Heiko Braun
 * @date 10/31/12
 */
public abstract class Transition implements EventProducer<EventType.TypeTransition> {

    private String id;

    private EventProduction<EventType.TypeTransition> eventProduction =
            new EventProduction<EventType.TypeTransition>();

    public abstract void perform();

    @Override
    public boolean doesProduceEvents() {
        return eventProduction.doesProduceEvents();
    }

    @Override
    public void setProducedEvents(Event<EventType.TypeTransition>... events) {
        this.eventProduction.setProducedEvents(events);
    }
}
