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
    private LinkedList<Transition> actions = new LinkedList<Transition>();

    private static final Condition CONDITION_NONE = new Condition() {
        @Override
        public boolean isMet() {
            return false;
        }
    };

    private EventConsumption eventConsumption = new EventConsumption(
            EventType.System, EventType.Interaction, EventType.Transition
    );

    public Behaviour(Event triggeredBy, String id) {
        this.triggeredBy = triggeredBy;
        this.id = id;
        this.condition = CONDITION_NONE;
    }

    public Behaviour(String id, Event triggeredBy, Condition condition) {
        this.id = id;
        this.triggeredBy = triggeredBy;
        this.condition = condition;
    }

    public void execute() {
        for(Transition action : actions)
            action.perform();
    }

    public void addAction(Transition action) {
        actions.add(action);
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
