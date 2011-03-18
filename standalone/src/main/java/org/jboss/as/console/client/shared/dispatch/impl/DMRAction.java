package org.jboss.as.console.client.shared.dispatch.impl;

import org.jboss.as.console.client.shared.dispatch.Action;
import org.jboss.as.console.client.shared.dispatch.ActionType;
import org.jboss.dmr.client.ModelNode;

/**
 * @author Heiko Braun
 * @date 3/17/11
 */
public class DMRAction implements Action<DMRResponse> {

    private ModelNode operation;

    public DMRAction(ModelNode operation) {
        this.operation = operation;
    }

    @Override
    public ActionType getType() {
        return ActionType.DMR;
    }

    @Override
    public Object getAddress() {
        return "http://localhost:9990/domain-api";
    }

    @Override
    public boolean isSecured() {
        return false;
    }

    public ModelNode getOperation()
    {
        return this.operation;
    }
}


