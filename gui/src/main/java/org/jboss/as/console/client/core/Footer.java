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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.layout.client.Layout;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.auth.CurrentUser;
import org.jboss.ballroom.client.widgets.window.Feedback;

/**
 * @author Heiko Braun
 * @date 1/28/11
 */
public class Footer {

    private Label userName;
    private PlaceManager placeManager;

    @Inject
    public Footer(EventBus bus, CurrentUser user, PlaceManager placeManager) {
        this.userName = new Label();
        this.userName.setText(user.getUserName());
        this.placeManager = placeManager;
    }

    public Widget asWidget() {

        LayoutPanel layout = new LayoutPanel();
        layout.setStyleName("footer-panel");

        HTML dmrBrowser = new HTML("Browser");
        dmrBrowser.addStyleName("footer-link");
        dmrBrowser.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                placeManager.revealPlace(
                        new PlaceRequest(NameTokens.ToolsPresenter).with("name","browser")
                );
            }
        });


        HTML settings = new HTML(Console.CONSTANTS.common_label_settings());
        settings.addStyleName("footer-link");
        settings.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                placeManager.revealPlace(
                        new PlaceRequest(NameTokens.SettingsPresenter)
                );
            }
        });


        HTML logout = new HTML(Console.CONSTANTS.common_label_logout());
        logout.addStyleName("footer-link");
        logout.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Feedback.confirm(
                        Console.CONSTANTS.common_label_logout(),
                        Console.CONSTANTS.logout_confirm(),
                        new Feedback.ConfirmationHandler() {
                            @Override
                            public void onConfirmation(boolean isConfirmed) {
                                if(isConfirmed)
                                {
                                    new LogoutCmd().execute();
                                }
                            }
                        }
                );

            }
        });


        HorizontalPanel tools = new HorizontalPanel();
        tools.add(dmrBrowser);
        tools.add(settings);
        tools.add(logout);

        layout.add(tools);

        HTML version = new HTML(org.jboss.as.console.client.Build.VERSION);
        version.getElement().setAttribute("style", "color:#ffffff;font-size:10px; align:left");
        layout.add(version);

        layout.setWidgetLeftWidth(version, 20, Style.Unit.PX, 200, Style.Unit.PX);
        layout.setWidgetTopHeight(version, 3, Style.Unit.PX, 16, Style.Unit.PX);

        layout.setWidgetRightWidth(tools, 5, Style.Unit.PX, 500, Style.Unit.PX);
        layout.setWidgetTopHeight(tools, 2, Style.Unit.PX, 28, Style.Unit.PX);

        layout.setWidgetHorizontalPosition(tools, Layout.Alignment.END);

        layout.getElement().setAttribute("role", "complementary");

        return layout;
    }
}
