package org.jboss.mbui.gui.behaviour;

import org.jboss.mbui.model.Dialog;

/**
 * An executable command.
 *
 * @author Heiko Braun
 * @date 11/20/12
 */
public interface ModelDrivenCommand<T> {

    void execute(Dialog dialog, T data);
}
