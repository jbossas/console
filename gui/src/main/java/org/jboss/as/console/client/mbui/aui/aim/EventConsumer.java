package org.jboss.as.console.client.mbui.aui.aim;

import java.util.Set;

/**
 * @author Heiko Braun
 * @date 10/31/12
 */
public interface EventConsumer {

    Set<Event<EventType>> getTriggers();

    boolean isTriggeredBy(Event<EventType> event);

    void setTriggers(Event<EventType>... trigger);
}
