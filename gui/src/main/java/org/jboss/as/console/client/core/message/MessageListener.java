package org.jboss.as.console.client.core.message;

/**
 * @author Heiko Braun
 * @date 3/27/12
 */
public interface MessageListener {
    void onMessage(Message message);
}