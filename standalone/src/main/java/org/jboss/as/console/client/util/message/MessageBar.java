package org.jboss.as.console.client.util.message;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;
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
public class MessageBar extends LayoutPanel implements MessageCenter.MessageListener {

    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    private Message stickyMessage;
    private MessageCenter messageCenter;
    private MessagePopup popup;

    private static class MessagePopup extends PopupPanel
    {
        private Label label;

        public MessagePopup()
        {
            super(true);

            label = new Label();
            setWidget(label);
            setStyleName(Message.Severity.Blank.getStyle());
        }

        public void setMessage(Message message) {
            label.setText(message.getConciseMessage());
            setStyleName(Message.Severity.Blank.getStyle());
            addStyleName(message.getSeverity().getStyle());
        }
    }

    @Inject
    public MessageBar(MessageCenter messageCenter) {
        super();
        this.messageCenter = messageCenter;
        this.popup = new MessagePopup();

        messageCenter.addMessageListener(this);
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
