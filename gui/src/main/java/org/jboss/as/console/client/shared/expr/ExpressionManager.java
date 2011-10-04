package org.jboss.as.console.client.shared.expr;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author Heiko Braun
 * @date 10/4/11
 */
public abstract class ExpressionManager {

    public abstract void resolveValue(Expression expr, AsyncCallback<String> callback);
}
