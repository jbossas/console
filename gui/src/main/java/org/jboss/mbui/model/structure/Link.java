package org.jboss.mbui.model.structure;

import org.jboss.mbui.gui.behaviour.NavigationEvent;
import org.jboss.mbui.model.behaviour.Resource;
import org.jboss.mbui.model.behaviour.ResourceType;

/**
 * @author Heiko Braun
 * @date 1/16/13
 */
public class Link<S extends Enum<S>> extends InteractionUnit<S> {

    private QName target;

    public Link(QName id, QName target, String label) {
        super(id, label);

        this.target = target;

        // explicit output
        setOutputs(new Resource<ResourceType>(NavigationEvent.ID, ResourceType.Navigation));
    }

    public QName getTarget() {
        return target;
    }

    @Override
    public String toString()
    {
        return "Link {" + getId() + ", label="+ getLabel()+"}";
    }
}
