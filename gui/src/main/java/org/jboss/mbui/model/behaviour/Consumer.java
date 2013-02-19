package org.jboss.mbui.model.behaviour;

import java.util.Set;

/**
 * Consumes {@link Resource}'s
 *
 * @author Heiko Braun
 * @date 10/31/12
 */
public interface Consumer extends SupportsConsumption {

    Set<Resource<ResourceType>> getInputs();

    void setInputs(Resource<ResourceType>... resources);
}
