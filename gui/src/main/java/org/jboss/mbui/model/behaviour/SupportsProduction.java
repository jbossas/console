package org.jboss.mbui.model.behaviour;

/**
 * The concept of producing resources.
 *
 * @author Heiko Braun
 * @date 2/19/13
 */
public interface SupportsProduction {
    boolean doesProduce(Resource<ResourceType> resource);
}
