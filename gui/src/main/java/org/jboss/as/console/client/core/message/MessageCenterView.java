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

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.inject.Inject;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.state.ReloadEvent;
import org.jboss.as.console.client.widgets.lists.DefaultCellList;
import org.jboss.ballroom.client.widgets.InlineLink;
import org.jboss.ballroom.client.widgets.icons.Icons;
import org.jboss.ballroom.client.widgets.window.DefaultWindow;
import org.jboss.ballroom.client.widgets.window.DialogueOptions;
import org.jboss.ballroom.client.widgets.window.TrappedFocusPanel;
import org.jboss.ballroom.client.widgets.window.WindowContentBuilder;

import java.util.List;

/**
 * @author Greg Hinkle
 * @author Heiko Braun
 */
public class MessageCenterView implements MessageCenter.MessageListener, ReloadEvent.ReloadListener {

    private MessageCenter messageCenter;
    private HorizontalPanel messageDisplay;
    final MessageListPopup messagePopup = new MessageListPopup();
    private Message lastSticky = null;
    private Label messageButton;

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

            getElement().setAttribute("role", "alert");
            getElement().setAttribute("aria-live", "assertive");

            this.sinkEvents(Event.ONKEYDOWN);
            this.sinkEvents(Event.MOUSEEVENTS);

            setStyleName("default-popup");

            SafeHtmlBuilder emptyMessage = new SafeHtmlBuilder();
            emptyMessage.appendHtmlConstant("<div style='padding:10px'>");
            emptyMessage.appendHtmlConstant(Console.CONSTANTS.common_label_noRecentMessages());
            emptyMessage.appendHtmlConstant("</div>");

            MessageCell messageCell = new MessageCell();
            messageList = new DefaultCellList<Message>(messageCell);
            messageList.setTabIndex(-1);
            messageList.addStyleName("message-list");
            messageList.setEmptyListWidget(new HTML(emptyMessage.toSafeHtml()));
            messageList.setKeyboardSelectionPolicy(HasKeyboardSelectionPolicy.KeyboardSelectionPolicy.ENABLED);

            final SingleSelectionModel<Message> selectionModel = new SingleSelectionModel<Message>();
            messageList.setSelectionModel(selectionModel);
            selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
                public void onSelectionChange(SelectionChangeEvent event) {
                    Message selected = selectionModel.getSelectedObject();
                    if (selected != null) {
                        if(selected.isSticky())
                        {
                            clearSticky();
                        }

                        showDetail(selected);
                    }
                }
            });

            VerticalPanel panel = new VerticalPanel();
            panel.setStyleName("fill-layout-width");

            panel.add(messageList);
            InlineLink clearBtn = new InlineLink(Console.CONSTANTS.common_label_clear());
            clearBtn.getElement().setAttribute("style", "float:right;padding-right:5px;font-size:10px;");

            clearBtn.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    messageCenter.getMessages().clear();
                    reflectMessageCount();
                    messagePopup.hide();
                }
            });
            panel.add(clearBtn);

            setWidget(panel);

            addCloseHandler(new CloseHandler<PopupPanel>() {
                @Override
                public void onClose(CloseEvent<PopupPanel> event) {
                    reflectMessageCount();
                }
            });

        }

        @Override
        protected void onPreviewNativeEvent(Event.NativePreviewEvent event) {
            if (Event.ONKEYDOWN == event.getTypeInt()) {
                if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ESCAPE) {
                    // Dismiss when escape is pressed
                    hide();
                }
            }
        }

        public CellList<Message> getMessageList() {
            return messageList;
        }

        public void focusOnFirstMessage() {
            messageList.setFocus(true);
        }
    }

    private void clearSticky() {
        MessageCenterView.this.lastSticky=null;
        messageDisplay.clear();
    }


    private void showDetail(final Message msg) {

        msg.setNew(false);

        final DefaultWindow window = new DefaultWindow(Console.CONSTANTS.common_label_messageDetailTitle());

        window.setWidth(480);
        window.setHeight(360);
        window.setGlassEnabled(true);


        ImageResource icon = MessageCenterView.getSeverityIcon(msg.getSeverity());
        AbstractImagePrototype prototype = AbstractImagePrototype.create(icon);

        SafeHtmlBuilder html = new SafeHtmlBuilder();

        // TODO: XSS prevention?
        html.appendHtmlConstant(prototype.getHTML());
        html.appendHtmlConstant("&nbsp;");
        html.appendHtmlConstant(msg.getFired().toString());
        html.appendHtmlConstant("<h3>");
        html.appendHtmlConstant(msg.getConciseMessage());
        html.appendHtmlConstant("</h3>");
        html.appendHtmlConstant("<p/>");

        String detail = msg.getDetailedMessage() != null ? msg.getDetailedMessage() : "";

        html.appendHtmlConstant("<pre style='font-family:tahoma, verdana, sans-serif;'>");
        html.appendHtmlConstant(detail);
        html.appendHtmlConstant("</pre>");

        final HTML widget = new HTML(html.toSafeHtml());
        widget.getElement().setAttribute("style", "margin:5px");


        DialogueOptions options = new DialogueOptions(
                "OK",
                new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent clickEvent) {
                        window.hide();
                    }
                },
                Console.CONSTANTS.common_label_cancel(),
                new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent clickEvent) {
                        window.hide();
                    }
                }
        );

        Widget windowContent = new WindowContentBuilder(widget, options).build();

        TrappedFocusPanel trap = new TrappedFocusPanel(windowContent)
        {
            @Override
            protected void onAttach() {
                super.onAttach();

                Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                    @Override
                    public void execute() {
                        getFocus().onFirstButton();
                    }
                });
            }
        };

        window.setWidget(trap);

        window.addCloseHandler(new CloseHandler<PopupPanel>() {

            @Override
            public void onClose(CloseEvent<PopupPanel> event) {
                messagePopup.getMessageList().getSelectionModel().setSelected(msg, false);
                messagePopup.hide();
            }
        });

        messagePopup.hide();
        window.center();
    }

    public Widget asWidget()
    {

        HorizontalPanel layout = new HorizontalPanel();
        layout.getElement().setAttribute("style", "width:100%;padding-top:5px;");

        messageButton = new Label("("+messageCenter.getNewMessageCount()+") "+Console.CONSTANTS.common_label_messages());
        messageButton.addStyleName("notification-button");

        ClickHandler clickHandler = new ClickHandler() {
            public void onClick(ClickEvent event) {

                int numMessages = fetchMessages(messagePopup);
                if(numMessages==0)numMessages=1;

                int width = 250;
                int height = numMessages*35;

                messagePopup.setPopupPosition(
                        messageButton.getAbsoluteLeft() - (width+10- messageButton.getOffsetWidth()) ,
                        messageButton.getAbsoluteTop() + 18
                );

                messagePopup.show();
                Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                    @Override
                    public void execute() {
                        messagePopup.focusOnFirstMessage();
                    }
                });


                messagePopup.setWidth(width+"px");
                messagePopup.setHeight(height+"px");
            }
        };

        messageButton.addClickHandler(clickHandler);

        messageDisplay = new HorizontalPanel();
        messageDisplay.getElement().setAttribute("role", "log");
        messageDisplay.getElement().setAttribute("aria-live", "polite");
        messageDisplay.getElement().setAttribute("aria-atomic", "true");

        layout.add(messageDisplay);
        layout.add(messageButton);

        messageDisplay.getElement().getParentElement().setAttribute("style", "width:100%;padding-right:5px");
        messageDisplay.getElement().getParentElement().setAttribute("align", "right");

        messageButton.getElement().getParentElement().setAttribute("style", "width:60px");
        messageButton.getElement().getParentElement().setAttribute("align", "right");

        // register listener
        messageCenter.addMessageListener(this);
        Console.MODULES.getEventBus().addHandler(ReloadEvent.TYPE, this);

        return layout;
    }

    private int fetchMessages(MessageListPopup popup) {
        List<Message> messages = messageCenter.getMessages();
        popup.getMessageList().setRowCount(messages.size(), true);
        popup.getMessageList().setRowData(0, messages);
        return messages.size();
    }

    public void onMessage(final Message message) {
        if (!message.isTransient()) {

            // update the visible message count
            reflectMessageCount();

            if(message.isSticky())   // sticky messages override each other like this
            {
                lastSticky=message;
                displayNotification(message);
            }
            else if(null==lastSticky
                    || Message.Severity.Error == message.getSeverity()
                    || Message.Severity.Fatal == message.getSeverity()) // regular message don't replace sticky ones
            {
                clearSticky();

                displayNotification(message);

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
    }

    private void reflectMessageCount() {
        int numMessages = messageCenter.getNewMessageCount();
        messageButton.setText("("+ numMessages +") " +Console.CONSTANTS.common_label_messages());
    }

    private void displayNotification(final Message message) {

        final String css = getSeverityStyle(message.severity);
        HorizontalPanel panel = new HorizontalPanel();
        panel.addStyleName("notification-panel");
        panel.addStyleName(css);

        String actualMessage = message.getConciseMessage().length()>40 ?
                message.getConciseMessage().substring(0, 40)+" ..." :
                message.getConciseMessage();

        // TODO: beware of XSS
        final HTML label = new HTML(" "+actualMessage);

        final ImageResource iconSrc = getSeverityIcon(message.severity);

        Image icon = new Image(iconSrc);
        panel.add(icon);
        panel.add(label);

        icon.getElement().getParentElement().setAttribute("style", "padding-right:5px;");


        label.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent clickEvent) {
                if(message.isSticky()) {
                    MessageCenterView.this.lastSticky=null;
                }
                messageDisplay.clear();
                showDetail(message);
            }
        });

        messageDisplay.clear();
        messageDisplay.add(panel);

    }

    public static String getSeverityStyle(Message.Severity severity) {
        String css = null;
        switch (severity) {
            case Info:
                css = "InfoBlock";
                break;
            case Warning:
                css = "WarnBlock";
                break;
            case Error:
            case Fatal:
                css = "ErrorBlock";
                break;
        }
        return css;
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

    @Override
    public void onReload() {
        clearSticky();
    }
}
