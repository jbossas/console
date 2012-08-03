package org.jboss.as.console.client.shared.expr;

import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.Map;

/**
 * @author Heiko Braun
 * @date 10/4/11
 */
public abstract class ExpressionResolver {

    public abstract void resolveValue(Expression expr, AsyncCallback<Map<String,String>> callback);
}
