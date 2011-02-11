package org.jboss.as.console.client.util.message;

import com.google.gwt.user.client.Timer;
import com.google.inject.Inject;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.DoubleClickEvent;
import com.smartgwt.client.widgets.events.DoubleClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;

/**
 * Displays messages to the user.
 * Will be notified through {@link MessageCenter}
 *
 * @author Heiko Braun
 * @date 1/28/11
 */
public class MessageBar extends HLayout implements MessageCenter.MessageListener {

    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    private Label label = new Label();
    private Message stickyMessage;

    private static final String NON_BREAKING_SPACE = "&nbsp;";

    private MessageCenter messageCenter;

    @Inject
    public MessageBar(MessageCenter messageCenter) {
        super();
        setOverflow(Overflow.VISIBLE);
        addStyleName("header-messagebar");
        this.messageCenter = messageCenter;
    }

    @Override
    protected void onDraw() {
        super.onDraw();

        setWidth100();
        setAlign(Alignment.CENTER);

        label.setAlign(Alignment.CENTER);
        label.setWidth("600px");
        label.setHeight("25px");

        setLabelEmpty();
        addMember(label);

        // sometimes its annoying to have the error message hang around for too long
        // let the user click the message so it goes away on demand
        addDoubleClickHandler(new DoubleClickHandler() {
            @Override
            public void onDoubleClick(DoubleClickEvent event) {
                clearMessage(true);
            }
        });

        messageCenter.addMessageListener(this);
    }

    @Override
    public void onMessage(Message message) {
        if (!message.isBackgroundJobResult()) {
            updateLabel(message);

            // Auto-clear the message after some time unless it's been designated as sticky.
            if (message.isSticky()) {
                this.stickyMessage = message;
            } else {
                new Timer() {
                    @Override
                    public void run() {
                        clearMessage(false);
                        if (stickyMessage != null) {
                            updateLabel(stickyMessage);
                        }
                    }
                }.schedule(AUTO_HIDE_DELAY_MILLIS);
            }
        }
    }

    private void clearMessage(boolean clearSticky) {
        setLabelEmpty();
        markForRedraw();

        if (clearSticky) {
            this.stickyMessage = null;
        }
    }

    private void setLabelEmpty() {
        label.setContents(NON_BREAKING_SPACE);
        label.setIcon(Message.Severity.Blank.getIcon());
        label.setStyleName(Message.Severity.Blank.getStyle());
    }

    private void updateLabel(Message message) {
        String contents = (message.getConciseMessage() != null) ? message.getConciseMessage() : message
            .getDetailedMessage();
        label.setContents(contents);

        String styleName = (contents != null) ? message.getSeverity().getStyle() : null;
        label.setStyleName(styleName);

        // TODO: Create some custom edge images in greed, yellow, red, etc. so we can add nice rounded corners to the
        //       label.
        //label.setShowEdges(true);

        String icon = (contents != null) ? message.getSeverity().getIcon() : null;
        label.setIcon(icon);

        markForRedraw();
    }
}
