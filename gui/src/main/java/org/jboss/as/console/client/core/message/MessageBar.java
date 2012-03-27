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

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.inject.Inject;

/**
 * Displays messages to the user.
 * Will be notified through {@link MessageCenter}
 *
 * @author Heiko Braun
 * @date 1/28/11
 */
public class MessageBar extends LayoutPanel implements MessageListener {

    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    private Message stickyMessage;
    private MessageCenter messageCenter;
    private MessagePopup popup;

    private static class MessagePopup extends PopupPanel
    {
        private HTML label;

        public MessagePopup()
        {
            super(true);

            label = new HTML();
            setWidget(label);
            setStyleName(Message.Severity.Blank.getStyle());
        }

        public void setMessage(Message message) {
            label.setHTML(message.getConciseMessage());
            setStyleName(Message.Severity.Blank.getStyle());
            addStyleName(message.getSeverity().getStyle());
        }
    }

    @Inject
    public MessageBar(MessageCenter messageCenter) {
        super();
        this.messageCenter = messageCenter;
        this.popup = new MessagePopup();

        //messageCenter.addMessageListener(this);
    }

    @Override
    public void onMessage(Message message) {
        if (!message.isBackgroundJobResult()) {
            popup.setMessage(message);

            // Auto-clear the message after some time unless it's been designated as sticky.
            if (message.isSticky()) {
                this.stickyMessage = message;
            } else {
                new Timer() {
                    @Override
                    public void run() {
                        clearMessage(false);
                        if (stickyMessage != null) {
                             popup.setMessage(stickyMessage);
                        }
                    }
                }.schedule(AUTO_HIDE_DELAY_MILLIS);
            }

            int width =  (Window.getClientWidth()/3)*2;
            int height = 24;

            popup.setWidth(width+"px");
            popup.setHeight(height+"px");

            popup.setPopupPosition(Window.getClientWidth()/2 - width/2, 35);
            popup.show();
        }
    }

    private void clearMessage(boolean clearSticky) {

        if (clearSticky) {
            this.stickyMessage = null;
        }

        popup.hide();
    }

}
