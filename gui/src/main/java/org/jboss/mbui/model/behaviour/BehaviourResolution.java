package org.jboss.mbui.model.behaviour;

import org.jboss.mbui.model.behaviour.Behaviour;
import org.jboss.mbui.model.structure.QName;

/**
 * Provides means to resolve actual behaviour implementations.
 *
 * @author Heiko Braun
 * @date 2/19/13
 */
public interface BehaviourResolution {

    Behaviour resolve(QName id);
}
