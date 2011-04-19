/*
 * JBoss, Home of Professional Open Source
 * Copyright <YEAR> Red Hat Inc. and/or its affiliates and other contributors
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
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.inject.Inject;
import org.jboss.as.console.client.widgets.DefaultWindow;
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

    private class MessageListPopup extends PopupPanel
    {
        private CellList<Message> messageList;

        public MessageListPopup()
        {
            super(true);
            setStyleName("default-popup");

            SafeHtmlBuilder emptyMessage = new SafeHtmlBuilder();
            emptyMessage.appendHtmlConstant("No recent messages!");

            MessageCell messageCell = new MessageCell();
            messageList = new CellList<Message>(messageCell);
            messageList.setStyleName("message-list");

            messageList.setEmptyListMessage(emptyMessage.toSafeHtml());

            final SingleSelectionModel<Message> selectionModel = new SingleSelectionModel<Message>();
            messageList.setSelectionModel(selectionModel);
            selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
                public void onSelectionChange(SelectionChangeEvent event) {
                    Message selected = selectionModel.getSelectedObject();
                    if (selected != null) {
                        showDetail(selected);
                    }
                }
            });

            setWidget(messageList);
        }

        public CellList<Message> getMessageList() {
            return messageList;
        }
    }

    private void showDetail(final Message msg) {

        DefaultWindow window = new DefaultWindow("Message Detail");
        window.setWidth(320);
        window.setHeight(240);
        window.setGlassEnabled(true);


        ImageResource icon = MessageCenterView.getSeverityIcon(msg.getSeverity());
        AbstractImagePrototype prototype = AbstractImagePrototype.create(icon);

        SafeHtmlBuilder html = new SafeHtmlBuilder();

        html.appendHtmlConstant(prototype.getHTML());
        html.appendHtmlConstant("&nbsp;");
        html.appendEscaped(msg.getFired().toString());
        html.appendHtmlConstant("<h3>").appendEscaped(msg.getConciseMessage()).appendHtmlConstant("</h3>");
        html.appendHtmlConstant("<p/>");

        String detail = msg.getDetailedMessage() != null ? msg.getDetailedMessage() : "(No detail message)";
        html.appendHtmlConstant("<pre style='font-family:tahoma, verdana, sans-serif;'>");
        html.appendEscaped(detail);
        html.appendHtmlConstant("</pre>");
        HTML widget = new HTML(html.toSafeHtml());
        widget.getElement().setAttribute("style", "margin:5px");

        ScrollPanel scroll = new ScrollPanel();
        scroll.add(widget);
        window.setWidget(scroll);

        window.addCloseHandler(new CloseHandler<PopupPanel>() {

            @Override
            public void onClose(CloseEvent<PopupPanel> event) {
                messagePopup.getMessageList().getSelectionModel().setSelected(msg, false);
                messagePopup.hide();
            }
        });

        window.center();
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
