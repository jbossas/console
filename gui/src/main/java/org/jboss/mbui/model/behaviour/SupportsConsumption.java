package org.jboss.mbui.model.behaviour;

/**
 * The concept of consuming resources.
 *
 * @author Heiko Braun
 * @date 2/19/13
 */
public interface SupportsConsumption {

    boolean doesConsume(Resource<ResourceType> resource);

}
