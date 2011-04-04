package org.jboss.as.console.client.shared.dispatch.impl;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import org.jboss.as.console.client.shared.dispatch.Action;
import org.jboss.as.console.client.shared.dispatch.ActionHandler;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.DispatchRequest;
import org.jboss.as.console.client.shared.dispatch.Result;

/**
 * @author Heiko Braun
 * @date 3/17/11
 */
public class DispatchAsyncImpl implements DispatchAsync {

    HandlerRegistry registry;

    @Inject
    public DispatchAsyncImpl(HandlerRegistry registry) {
        this.registry = registry;
    }

    @Override
    public <A extends Action<R>, R extends Result> DispatchRequest execute(A action, AsyncCallback<R> callback) {

        ActionHandler<A,R> handler = registry.resolve(action);
        if(null==handler)
            callback.onFailure(new IllegalStateException("No handler for type "+action.getType()));

        return handler.execute(action, callback);
    }

    @Override
    public <A extends Action<R>, R extends Result> DispatchRequest undo(A action, R result, AsyncCallback<Void> callback) {
        return null;
    }
}
