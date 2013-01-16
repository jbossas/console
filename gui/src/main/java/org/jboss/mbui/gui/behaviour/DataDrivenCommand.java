package org.jboss.mbui.gui.behaviour;

/**
 * An executable command. Corresponds to the
 * {@link org.jboss.mbui.model.behaviour.Transition} of the behaviour model.
 *
 * @author Heiko Braun
 * @date 11/20/12
 */
public interface DataDrivenCommand<T> {

    void execute(T data);
}
