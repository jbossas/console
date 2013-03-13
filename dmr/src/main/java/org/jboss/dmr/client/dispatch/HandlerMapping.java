package org.jboss.dmr.client.dispatch;

/**
 * @author Heiko Braun
 * @date 4/12/11
 */
public interface HandlerMapping {
    ActionHandler resolve(Action action);

    void register(ActionType actionType, ActionHandler handler);
}
