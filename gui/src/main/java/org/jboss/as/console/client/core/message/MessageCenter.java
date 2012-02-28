/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package org.jboss.as.console.client.core.message;

import com.allen_sauer.gwt.log.client.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Greg Hinkle
 * @author Heiko Braun
 */
public class MessageCenter {

    private LinkedList<Message> messages = new LinkedList<Message>();
    private List<MessageListener> listeners = new ArrayList<MessageListener>();

    private static final int MAX_MESSAGES = 10;

    public void notify(Message message) {

        log(message);

        // keep only non-transient messsages
        if (!message.isTransient())
        {
            this.messages.addFirst(message);

            if (messages.size() > MAX_MESSAGES)
            {
                messages.removeLast();
            }
        }

        // notify listeners
        for (MessageListener listener : listeners)
        {
            listener.onMessage(message);
        }
    }

    public void addMessageListener(MessageListener listener) {
        this.listeners.add(listener);
    }

    /**
     * Returns a list of recently published non-transient messages.
     *
     * @return a list of recently published non-transient messages
     */
    public List<Message> getMessages() {
        return messages;
    }

    public int getNewMessageCount() {
        int i=0;
        for(Message m : messages)
            if(m.isNew()) i++;
        return i;
    }

    public interface MessageListener {
        void onMessage(Message message);
    }

    private void log(Message message) {
        String formattedMessage = message.getConciseMessage();

        if (message.severity == Message.Severity.Info) {
            Log.info(formattedMessage);
        } else if (message.severity == Message.Severity.Warning) {
            Log.warn(formattedMessage);
        } else if (message.severity == Message.Severity.Error) {
            Log.error(formattedMessage);
        } else if (message.severity == Message.Severity.Fatal) {
            Log.fatal(formattedMessage);
        } else {
            Log.debug(formattedMessage);
        }
    }
    
}
