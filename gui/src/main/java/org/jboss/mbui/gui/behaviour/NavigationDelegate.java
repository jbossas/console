package org.jboss.mbui.gui.behaviour;

import org.jboss.mbui.model.structure.QName;

/**
 * @author Heiko Braun
 * @date 3/14/13
 */
public interface NavigationDelegate {

    void onNavigation(QName source, QName dialog);
}
