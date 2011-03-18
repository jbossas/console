package org.jboss.as.console.client.shared.dispatch;

/**
 * @author Heiko Braun
 * @date 3/17/11
 */
public interface Dispatch {
    <A extends Action<R>,R extends Result> R execute(A action);
    <A extends Action<R>,R extends Result> void undo(A action, R result);
}
