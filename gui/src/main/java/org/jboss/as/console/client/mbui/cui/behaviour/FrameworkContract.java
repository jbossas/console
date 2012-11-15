package org.jboss.as.console.client.mbui.cui.behaviour;

/**
 * Contract between InteractionCoordinator and underlying framework (i.e. GWT Platform)
 *
 * @author Heiko Braun
 * @date 11/15/12
 */
public interface FrameworkContract {

    void onBind();
    //void prepareFromRequest();
    void onReveal();
    void onReset();
    //void prepareRequest();
}
