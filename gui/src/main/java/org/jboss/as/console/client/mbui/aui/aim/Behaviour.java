package org.jboss.as.console.client.mbui.aui.aim;

import org.jboss.as.console.client.mbui.aui.aim.assets.EventConsumption;

import java.util.LinkedList;
import java.util.Set;

/**
 * @author Heiko Braun
 * @date 10/31/12
 */
public class Behaviour implements EventConsumer {

    private QName id;
    private Condition condition;
    private Event trigger;
    private LinkedList<Transition> transitions = new LinkedList<Transition>();

    private static final Condition CONDITION_ALWAYS= new Condition() {
        @Override
        public boolean isMet() {
            return true;
        }
    };

    private EventConsumption eventConsumption = new EventConsumption();

    public Behaviour(String namespace, String id, Event trigger) {
        this.trigger = trigger;
        this.id = new QName(namespace, id);
        this.condition = CONDITION_ALWAYS;
        this.eventConsumption.setTriggers(trigger);
    }

    public Behaviour(String namespace, String id, Event trigger, Condition condition) {
        this(namespace, id, trigger);
        this.condition = condition;
    }

    public QName getId() {
        return id;
    }

    public Event getTrigger() {
        return trigger;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public void execute() {

        if(condition.isMet())
        {
            for(Transition action : transitions)
                action.perform();
        }
    }

    public void addTransition(Transition transition) {
        transitions.add(transition);
    }

    @Override
    public Set<Event<EventType>> getTriggers() {
        return eventConsumption.getTriggers();
    }

    @Override
    public void setTriggers(Event<EventType>... trigger) {
        eventConsumption.setTriggers(trigger);
    }

    @Override
    public boolean isTriggeredBy(Event<EventType> type) {
        return eventConsumption.isTriggeredBy(type);
    }
}
