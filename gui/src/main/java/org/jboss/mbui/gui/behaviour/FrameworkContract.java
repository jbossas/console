package org.jboss.mbui.gui.behaviour;

import org.jboss.mbui.model.structure.QName;

/**
 * Contract between {@link InteractionCoordinator} and underlying framework (i.e. GWT Platform)
 *
 * @author Heiko Braun
 * @date 11/15/12
 */
public interface FrameworkContract {

    void onBind();
    void onReset();

    void setStatement(QName sourceId, String key, String value);
    void clearStatement(QName sourceId, String key, String value);

}
