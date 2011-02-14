package org.jboss.as.console.client;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripSeparator;
import org.jboss.as.console.client.auth.AuthenticationEvent;
import org.jboss.as.console.client.auth.AuthenticationListener;
import org.jboss.as.console.client.util.message.MessageCenterView;

/**
 * @author Heiko Braun
 * @date 1/28/11
 */
public class Footer implements AuthenticationListener {

    private Label label;

    @Inject
    public Footer(EventBus bus) {
        bus.addHandler(AuthenticationEvent.TYPE, this);
    }

    @Override
    public void onUserAuthenticated(AuthenticationEvent event) {
        label.setContents("&nbsp;"+event.getUser().getUserName());
    }

    public Widget asWidget() {

        ToolStrip toolstrip = new ToolStrip();

        toolstrip.setHeight(30);
        toolstrip.setAlign(VerticalAlignment.CENTER);
        toolstrip.setWidth100();
        toolstrip.setMembersMargin(10);
        toolstrip.setLayoutRightMargin(15);
        toolstrip.setLayoutLeftMargin(10);

        Img img = new Img("global/User_16.png");
        img.setWidth(16);
        img.setHeight(16);
        toolstrip.addMember(img);

        label = new Label();
        label.setMargin(5);
        toolstrip.addMember(label);

        toolstrip.addMember(new ToolStripSeparator());

        MessageCenterView messageCenterView = Console.MODULES.getMessageCenterView();
        toolstrip.addMember(messageCenterView.asWidget());
        return toolstrip;

    }
}
