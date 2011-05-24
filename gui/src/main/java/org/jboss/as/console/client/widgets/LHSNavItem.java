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

package org.jboss.as.console.client.widgets;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.LayoutPanel;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.Places;

/**
 * @author Heiko Braun
 * @date 2/21/11
 */
@Deprecated
public class LHSNavItem extends LayoutPanel {

    public LHSNavItem(String title, final String token) {

        setStyleName("lhs-nav-item");
        HTML html = addText(token, title);
        setWidgetLeftWidth(html, 15, Style.Unit.PX, 100, Style.Unit.PCT);
    }

    public LHSNavItem(String title, final String token, ImageResource icon) {
        setStyleName("lhs-nav-item");
        Image image = new Image(icon);
        add(image);
        HTML html = addText(token, title);

        setWidgetLeftWidth(image, 15, Style.Unit.PX, 10, Style.Unit.PX);
        setWidgetTopHeight(image, 5, Style.Unit.PX, 10, Style.Unit.PX);
        setWidgetLeftWidth(html, 29, Style.Unit.PX, 100, Style.Unit.PCT);
    }

    public LHSNavItem(String title, ImageResource icon, ClickHandler clickHandler) {
        setStyleName("lhs-nav-item");
        Image image = new Image(icon);
        add(image);
        HTML text = new HTML(title);
        text.getElement().setAttribute("style", "padding-top:5px;");
        text.addClickHandler(clickHandler);
        add(text);

        setWidgetLeftWidth(image, 15, Style.Unit.PX, 10, Style.Unit.PX);
        setWidgetTopHeight(image, 5, Style.Unit.PX, 10, Style.Unit.PX);
        setWidgetLeftWidth(text, 29, Style.Unit.PX, 100, Style.Unit.PCT);
    }

    private HTML addText(final String token, String title) {
        HTML text = new HTML(title);
        text.getElement().setAttribute("style", "padding-top:5px;");
        text.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Console.MODULES.getPlaceManager().revealPlaceHierarchy(
                        Places.fromString(token)
                );
            }
        });

        add(text);
        return text;
    }
}
