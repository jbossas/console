package org.jboss.mbui.model.behaviour;

import org.jboss.mbui.model.structure.QName;
import org.jboss.mbui.model.structure.impl.ResourceConsumption;

import java.util.LinkedList;
import java.util.Set;

/**
 * A represenation of an ECA rule (event, condition & action)
 *
 * @author Heiko Braun
 * @date 10/31/12
 */
public class Behaviour implements Consumer {

    private QName id;
    private Condition condition;
    private Resource resource;
    private LinkedList<Transition> transitions = new LinkedList<Transition>();

    private static final Condition CONDITION_ALWAYS= new Condition() {
        @Override
        public boolean isMet() {
            return true;
        }
    };

    private ResourceConsumption resourceConsumption = new ResourceConsumption();

    public Behaviour(String namespace, String id, Resource resource) {
        this.resource = resource;
        this.id = new QName(namespace, id);
        this.condition = CONDITION_ALWAYS;
        this.resourceConsumption.setInputs(resource);
    }

    public Behaviour(String namespace, String id, Resource resource, Condition condition) {
        this(namespace, id, resource);
        this.condition = condition;
    }

    public QName getId() {
        return id;
    }

    public Resource getResource() {
        return resource;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public void addTransition(Transition transition) {
        transitions.add(transition);
    }

    @Override
    public Set<Resource<ResourceType>> getInputs() {
        return resourceConsumption.getInputs();
    }

    @Override
    public void setInputs(Resource<ResourceType>... resource) {
        resourceConsumption.setInputs(resource);
    }

    @Override
    public boolean doesConsume(Resource<ResourceType> type) {
        return resourceConsumption.doesConsume(type);
    }

}
