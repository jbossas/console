package org.jboss.mbui.gui.behaviour;

/**
 * Acts as a guard clause for {@link Procedure}'s.
 * Corresponds to the {@link org.jboss.mbui.model.behaviour.Condition} model element.
 *
 * @author Heiko Braun
 * @date 1/23/13
 */
public interface Precondition {
    boolean isMet(StatementContext statementContext);
}
