package org.jboss.as.console.client.core.message;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 3/27/12
 */
public interface MessageCenter {
    void notify(Message message);
    void addMessageListener(MessageListener listener);
    List<Message> getMessages();
    int getNewMessageCount();
}
