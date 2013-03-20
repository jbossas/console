package org.jboss.mbui.gui.behaviour;

/**
 * Contract between {@link InteractionCoordinator} and underlying framework (i.e. GWT Platform)
 *
 * @author Heiko Braun
 * @date 11/15/12
 */
public interface FrameworkContract {

    void onBind();
    void onReset();

    void setStatement(String key, String value);
    void clearStatement(String key);

}
