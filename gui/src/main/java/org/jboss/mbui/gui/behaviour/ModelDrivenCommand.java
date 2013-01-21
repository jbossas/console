package org.jboss.mbui.gui.behaviour;

import org.jboss.mbui.model.structure.Dialog;

/**
 * An executable command. Corresponds to the
 * {@link org.jboss.mbui.model.behaviour.Transition} of the behaviour model.
 *
 * @author Heiko Braun
 * @date 11/20/12
 */
public interface ModelDrivenCommand<T> {

    void execute(Dialog dialog, T data);
}
