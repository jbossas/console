package org.jboss.as.console.client.shared.dispatch;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author Heiko Braun
 * @date 3/17/11
 */
public interface DispatchAsync {
    <A extends Action<R>,R extends Result> DispatchRequest execute(
            A action,AsyncCallback<R> callback);

    <A extends Action<R>,R extends Result> DispatchRequest undo(
            A action, R result, AsyncCallback<Void> callback);

}
