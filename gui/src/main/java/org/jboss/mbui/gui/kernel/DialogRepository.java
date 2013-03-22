package org.jboss.mbui.gui.kernel;

import org.jboss.mbui.model.Dialog;

/**
 * @author Heiko Braun
 * @date 3/22/13
 */
public interface DialogRepository {
    Dialog getDialog(String name);
}
