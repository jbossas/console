package org.jboss.mbui.model.behaviour;

import java.util.Set;

/**
 * A producer creates {@link Resource}'s
 *
 * @author Heiko Braun
 * @date 10/31/12
 */
public interface Producer {

    boolean doesProduce();

    void setOutputs(Resource<ResourceType>... resources);

    public Set<Resource<ResourceType>> getOutputs();
}
