package org.jboss.as.console.client.shared.dispatch;

/**
 * @author Heiko Braun
 * @date 3/17/11
 */
public interface Action<R extends Result> {

    ActionType getType();
    Object getAddress();
    boolean isSecured();
}
