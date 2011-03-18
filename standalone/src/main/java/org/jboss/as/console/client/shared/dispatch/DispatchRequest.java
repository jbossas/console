package org.jboss.as.console.client.shared.dispatch;

/**
 * @author Heiko Braun
 * @date 3/17/11
 */
public interface DispatchRequest {
    void cancel();
    boolean isPending();
}
