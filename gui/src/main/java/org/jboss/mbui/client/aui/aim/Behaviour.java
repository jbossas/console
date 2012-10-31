package org.jboss.mbui.client.aui.aim;

import org.jboss.mbui.client.aui.aim.assets.EventConsumption;

import java.util.LinkedList;

/**
 * @author Heiko Braun
 * @date 10/31/12
 */
public class Behaviour implements EventConsumer {

    private String id;

    private Event triggeredBy;
    private Condition condition;
    private LinkedList<Transition> transitions = new LinkedList<Transition>();

    private static final Condition CONDITION_NONE = new Condition() {
        @Override
        public boolean isMet() {
            return false;
        }
    };

    private EventConsumption eventConsumption = new EventConsumption(
            EventType.System, EventType.Interaction, EventType.Transition
    );

    public Behaviour(String id, Event trigger) {
        this.triggeredBy = trigger;
        this.id = id;
        this.condition = CONDITION_NONE;
    }

    public Behaviour(String id, Event trigger, Condition condition) {
        this.id = id;
        this.triggeredBy = trigger;
        this.condition = condition;
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
    public EventType[] getConsumedTypes() {
        return eventConsumption.getConsumedTypes();
    }

    @Override
    public boolean consumes(Event event) {
        return eventConsumption.consumes(event);
    }
}
