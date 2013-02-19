package org.jboss.mbui.model.behaviour;

import org.jboss.mbui.model.structure.QName;

/**
 * A represent the concept of behaviour.
 *
 * @author Heiko Braun
 * @date 10/31/12
 */
public interface Behaviour extends SupportsConsumption, SupportsProduction {

    QName getId();
}
