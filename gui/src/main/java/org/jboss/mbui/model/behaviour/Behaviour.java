package org.jboss.mbui.model.behaviour;

import org.jboss.mbui.model.structure.QName;
import org.jboss.mbui.model.structure.impl.EventConsumption;

import java.util.LinkedList;
import java.util.Set;

/**
 * @author Heiko Braun
 * @date 10/31/12
 */
public class Behaviour implements TriggerTarget {

    private QName id;
    private Condition condition;
    private Trigger trigger;
    private LinkedList<Transition> transitions = new LinkedList<Transition>();

    private static final Condition CONDITION_ALWAYS= new Condition() {
        @Override
        public boolean isMet() {
            return true;
        }
    };

    private EventConsumption eventConsumption = new EventConsumption();

    public Behaviour(String namespace, String id, Trigger trigger) {
        this.trigger = trigger;
        this.id = new QName(namespace, id);
        this.condition = CONDITION_ALWAYS;
        this.eventConsumption.setInputs(trigger);
    }

    public Behaviour(String namespace, String id, Trigger trigger, Condition condition) {
        this(namespace, id, trigger);
        this.condition = condition;
    }

    public QName getId() {
        return id;
    }

    public Trigger getTrigger() {
        return trigger;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public void addTransition(Transition transition) {
        transitions.add(transition);
    }

    @Override
    public Set<Trigger<TriggerType>> getInputs() {
        return eventConsumption.getInputs();
    }

    @Override
    public void setInputs(Trigger<TriggerType>... trigger) {
        eventConsumption.setInputs(trigger);
    }

    @Override
    public boolean isTriggeredBy(Trigger<TriggerType> type) {
        return eventConsumption.isTriggeredBy(type);
    }

}
