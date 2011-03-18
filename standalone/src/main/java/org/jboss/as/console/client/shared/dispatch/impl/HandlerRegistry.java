package org.jboss.as.console.client.shared.dispatch.impl;

import org.jboss.as.console.client.shared.dispatch.Action;
import org.jboss.as.console.client.shared.dispatch.ActionHandler;
import org.jboss.as.console.client.shared.dispatch.ActionType;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Heiko Braun
 * @date 3/17/11
 */
public class HandlerRegistry {

    private Map<ActionType, ActionHandler> registry = new HashMap<ActionType, ActionHandler>();

    public HandlerRegistry() {
        register(ActionType.DMR, new DMRHandler());
    }

    public ActionHandler resolve(Action action) {
        return registry.get(action.getType());
    }

    public void register(ActionType actionType, ActionHandler handler)
    {
        registry.put(actionType, handler);
    }
}
