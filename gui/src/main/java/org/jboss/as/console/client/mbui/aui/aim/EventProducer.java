package org.jboss.as.console.client.mbui.aui.aim;

import java.util.Set;

/**
 * @author Heiko Braun
 * @date 10/31/12
 */
public interface EventProducer {

    boolean doesProduceEvents();

    void setProducedEvents(Event<EventType>... events);

    public Set<Event<EventType>> getProducedEvents();
}
