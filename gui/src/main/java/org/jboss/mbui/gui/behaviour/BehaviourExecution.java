package org.jboss.mbui.gui.behaviour;

import org.jboss.mbui.model.structure.QName;

import java.util.Map;
import java.util.Set;

/**
 * @author Heiko Braun
 * @date 2/21/13
 */
public interface BehaviourExecution {

    void addProcedure(Procedure procedure);

    Map<QName, Set<Procedure>> listProcedures();
}
