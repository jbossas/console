package org.jboss.mbui.model.behaviour;

import org.jboss.mbui.model.structure.QName;
import org.jboss.mbui.model.structure.impl.ResourceProduction;

import java.util.Set;

/**
 * The execution of an behaviour.
 *
 * @author Heiko Braun
 * @date 10/31/12
 */
public abstract class Transition implements Producer {

    private QName id;

    protected Transition(QName id) {
        this.id = id;
    }

    public QName getId() {
        return id;
    }

    private ResourceProduction resourceProduction =
            new ResourceProduction(ResourceType.Transition);

    @Override
    public boolean doesProduce() {
        return resourceProduction.doesProduce();
    }

    @Override
    public void setOutputs(Resource<ResourceType>... events) {
        this.resourceProduction.setOutputs(events);
    }

    public Set<Resource<ResourceType>> getOutputs()
    {
        return resourceProduction.getOutputs();
    }
}
