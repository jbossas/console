package org.jboss.mbui.gui.behaviour;

import org.jboss.mbui.model.structure.QName;

/**
 * Contract between {@link InteractionCoordinator} and the UI Kernel
 *
 * @author Heiko Braun
 * @date 11/15/12
 */
public interface KernelContract {

    void activate();
    void reset();
    void passivate();

    void setStatement(QName sourceId, String key, String value);
    void clearStatement(QName sourceId, String key, String value);

}
