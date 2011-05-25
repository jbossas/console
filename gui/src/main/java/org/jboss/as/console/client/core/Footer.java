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

package org.jboss.as.console.client.core;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.auth.CurrentUser;
import org.jboss.as.console.client.core.message.MessageCenterView;
import org.jboss.as.console.client.widgets.icons.Icons;

/**
 * @author Heiko Braun
 * @date 1/28/11
 */
public class Footer {

    private Label userName;

    @Inject
    public Footer(EventBus bus, CurrentUser user) {
        this.userName = new Label();
        this.userName.setText(user.getUserName());
    }

    public Widget asWidget() {

        LayoutPanel layout = new LayoutPanel();
        layout.setStyleName("footer-panel");
        //Image userImg = new Image(Icons.INSTANCE.user());
        //layout.add(userImg);

        //userName.setStyleName("footer-item");
        //layout.add(userName);

        MessageCenterView messageCenterView = Console.MODULES.getMessageCenterView();
        Widget messageCenter = messageCenterView.asWidget();
        //messageCenter.getElement().addClassName("footer-item-right");
        layout.add(messageCenter);

        //layout.setWidgetLeftWidth(userImg, 5, Style.Unit.PX, 16, Style.Unit.PX);
        //layout.setWidgetTopHeight(userImg, 6, Style.Unit.PX, 16, Style.Unit.PX);

        //layout.setWidgetLeftWidth(userName, 25, Style.Unit.PX, 100, Style.Unit.PX);
        //layout.setWidgetTopHeight(userName, 6, Style.Unit.PX, 16, Style.Unit.PX);


        HTML version = new HTML(org.jboss.as.console.client.Build.VERSION);
        version.getElement().setAttribute("style", "color:#000000;font-size:10px; align:center");
        layout.add(version);

        layout.setWidgetLeftWidth(version, 15, Style.Unit.PX, 100, Style.Unit.PX);
        layout.setWidgetTopHeight(version, 6, Style.Unit.PX, 16, Style.Unit.PX);


        layout.setWidgetRightWidth(messageCenter, 15, Style.Unit.PX, 300, Style.Unit.PX);
        layout.setWidgetTopHeight(messageCenter, 2, Style.Unit.PX, 28, Style.Unit.PX);
        return layout;
    }
}
