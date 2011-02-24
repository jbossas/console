package org.jboss.as.console.client.core;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.auth.AuthenticationEvent;
import org.jboss.as.console.client.auth.AuthenticationListener;
import org.jboss.as.console.client.widgets.icons.Icons;
import org.jboss.as.console.client.core.message.MessageCenterView;

/**
 * @author Heiko Braun
 * @date 1/28/11
 */
public class Footer implements AuthenticationListener {

    private Label userName;

    @Inject
    public Footer(EventBus bus) {
        bus.addHandler(AuthenticationEvent.TYPE, this);
    }

    @Override
    public void onUserAuthenticated(AuthenticationEvent event) {
        userName.setText(event.getUser().getUserName());
    }

    public Widget asWidget() {

        LayoutPanel layout = new LayoutPanel();
        layout.setStyleName("footer-panel");
        Image userImg = new Image(Icons.INSTANCE.user());
        layout.add(userImg);

        userName = new Label();
        userName.setStyleName("footer-item");
        layout.add(userName);

        MessageCenterView messageCenterView = Console.MODULES.getMessageCenterView();
        Widget messageCenter = messageCenterView.asWidget();
        //messageCenter.getElement().addClassName("footer-item-right");
        layout.add(messageCenter);

        layout.setWidgetLeftWidth(userImg, 5, Style.Unit.PX, 16, Style.Unit.PX);
        layout.setWidgetTopHeight(userImg, 6, Style.Unit.PX, 16, Style.Unit.PX);

        layout.setWidgetLeftWidth(userName, 25, Style.Unit.PX, 100, Style.Unit.PX);
        layout.setWidgetTopHeight(userName, 6, Style.Unit.PX, 16, Style.Unit.PX);

        layout.setWidgetRightWidth(messageCenter, 15, Style.Unit.PX, 300, Style.Unit.PX);
        layout.setWidgetTopHeight(messageCenter, 2, Style.Unit.PX, 28, Style.Unit.PX);
        return layout;
    }
}
