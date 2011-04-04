/*
 * RHQ Management Platform
 * Copyright (C) 2005-2010 Red Hat, Inc.
 * All rights reserved.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
package org.jboss.as.console.client.core.message;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import org.jboss.as.console.client.widgets.icons.Icons;

import java.util.List;

/**
 * @author Greg Hinkle
 * @author Heiko Braun
 */
public class MessageCenterView implements MessageCenter.MessageListener {

    private MessageCenter messageCenter;
    private LayoutPanel messageDisplay;
    final MessageListPopup messagePopup = new MessageListPopup();

    @Inject
    public MessageCenterView(MessageCenter messageCenter) {
        this.messageCenter = messageCenter;
    }

    private static class MessageListPopup extends PopupPanel
    {
        private CellList<Message> messageList;

        public MessageListPopup()
        {
            super(true);

            SafeHtmlBuilder emptyMessage = new SafeHtmlBuilder();
            emptyMessage.appendHtmlConstant("No recent messages!");

            MessageCell messageCell = new MessageCell();
            messageList = new CellList<Message>(messageCell);
            messageList.setEmptyListMessage(emptyMessage.toSafeHtml());

            messageList.setStyleName("message-list");

            setWidget(messageList);
            setStyleName("default-popup");
        }

        public CellList<Message> getMessageList() {
            return messageList;
        }
    }

    private MessageListPopup getMessagePopup() {
        return messagePopup;
    }

    public Widget asWidget()
    {
        LayoutPanel layout = new LayoutPanel()
        {
            @Override
            public void onResize() {
                super.onResize();
                MessageListPopup popup = getMessagePopup();
                if(popup!=null) popup.hide();
            }
        };

        messageDisplay = new LayoutPanel();

        final Button button = new Button("Messages");
        button.getElement().addClassName("default-button");

        ClickHandler clickHandler = new ClickHandler() {
            public void onClick(ClickEvent event) {

                int numMessages = fetchMessages(messagePopup);

                int width = 200;
                int height = numMessages*35;

                messagePopup.setPopupPosition(
                        button.getAbsoluteLeft() - (width+24-button.getOffsetWidth()) ,
                        button.getAbsoluteTop() - (height+24)
                );



                messagePopup.show();

                messagePopup.setWidth(width+"px");
                messagePopup.setHeight(height+"px");
            }
        };

        button.addClickHandler(clickHandler);

        // register listener
        messageCenter.addMessageListener(this);


        layout.add(messageDisplay);
        layout.add(button);

        layout.setWidgetLeftWidth(messageDisplay, 0, Style.Unit.PX, 200, Style.Unit.PX);
        layout.setWidgetLeftWidth(button, 200, Style.Unit.PX, 100, Style.Unit.PX);
        layout.setWidgetTopHeight(button, 2, Style.Unit.PX, 22, Style.Unit.PX);

        return layout;
    }

    private int fetchMessages(MessageListPopup popup) {
        List<Message> messages = messageCenter.getMessages();
        popup.getMessageList().setRowData(0, messages);
        return messages.size();
    }

    private void showDetails(Message message) {
        // TODO: implement popup window
        Log.debug("Message detail not implemented yet");
    }

    public void onMessage(final Message message) {
        if (!message.isTransient()) {
            logMessage(message);

            HorizontalPanel panel = new HorizontalPanel();
            panel.getElement().setAttribute("cellpadding", "6");

            String actualMessage = message.getConciseMessage().length()>30 ? message.getConciseMessage().substring(0, 30)+" ..." : message.getConciseMessage();

            final Label label = new Label(actualMessage);
            label.getElement().setAttribute("style", "white-space: nowrap;text-overflow:ellipsis");

            final ImageResource iconSrc = getSeverityIcon(message.severity);

            panel.add(new Image(iconSrc));
            panel.add(label);

            // would be nice too have
            //label.setTooltip(message.detailedMessage);

            label.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent clickEvent) {
                    showDetails(message);
                }
            });

            messageDisplay.clear();
            messageDisplay.add(panel);

            Timer hideTimer = new Timer() {
                @Override
                public void run() {
                    // hide message
                    messageDisplay.clear();
                }
            };

            hideTimer.schedule(5000);
        }
    }

    private void logMessage(Message message) {
        // TODO: Format the message better.
        String logMessage = message.toString();
        switch (message.getSeverity()) {
            case Info:
                Log.info(logMessage);
                break;
            case Warning:
                Log.warn(logMessage);
                break;
            case Error:
                Log.error(logMessage);
                break;
            case Fatal:
                Log.fatal(logMessage);
                break;
        }
    }

    public static ImageResource getSeverityIcon(Message.Severity severity) {
        ImageResource iconSrc = null;
        switch (severity) {
            case Info:
                iconSrc = Icons.INSTANCE.info_blue();
                break;
            case Warning:
                iconSrc = Icons.INSTANCE.info_orange();
                break;
            case Error:
            case Fatal:
                iconSrc = Icons.INSTANCE.info_red();
                break;
        }
        return iconSrc;
    }

}
