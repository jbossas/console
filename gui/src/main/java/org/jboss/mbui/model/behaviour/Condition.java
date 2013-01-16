package org.jboss.mbui.model.behaviour;

/**
 * Serves as ‘guards’ to specify under which conditions a transition might be executed.
 *
 * @author Heiko Braun
 * @date 10/31/12
 */
public interface Condition {
     boolean isMet();
}
