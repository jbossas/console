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

package org.jboss.as.console.client.widgets;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Collection of feedback windows.
 * Info, Confirmation, Alert, etc.
 * @author Heiko Braun
 * @date 3/2/11
 */
public class Feedback {

    public static void confirm(String title, String message, final ConfirmationHandler handler)
    {
        final DefaultWindow window = new DefaultWindow(title);

        int width = 200;
        int height = (int) (width / DefaultWindow.GOLDEN_RATIO);

        window.setWidth(width);
        window.setHeight(height);

        window.setGlassEnabled(true);

        VerticalPanel panel = new VerticalPanel();
        panel.setStyleName("fill-layout-width");

        HTML text = new HTML(message);

        DefaultButton ok = new DefaultButton("OK");
        ok.getElement().setAttribute("style", "width:50px;height:18px");
        ok.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                handler.onConfirmation(true);
                window.hide();
            }
        });

        Label cancel = new Label("Cancel");
        cancel.setStyleName("html-link");
        cancel.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                handler.onConfirmation(false);
                window.hide();
            }
        });

        HorizontalPanel options = new HorizontalPanel();
        options.getElement().setAttribute("style", "width:100%");

        HTML spacer = new HTML("&nbsp;");
        options.add(spacer);
        //spacer.getElement().getParentElement().setAttribute("width", "100%");

        options.add(ok);
        options.add(spacer);
        options.add(cancel);
        cancel.getElement().getParentElement().setAttribute("style","vertical-align:middle");
        ok.getElement().getParentElement().setAttribute("style","vertical-align:middle");

        ok.getElement().getParentElement().setAttribute("align", "right");
        ok.getElement().getParentElement().setAttribute("width", "100%");

        panel.add(text);
        panel.add(options);

        //panel.setWidgetBottomHeight(text, 30, Style.Unit.PX, height-60, Style.Unit.PX);
        //panel.setWidgetBottomHeight(options, 0, Style.Unit.PX, 30, Style.Unit.PX);

        window.setWidget(panel);

        window.center();
    }

    public interface ConfirmationHandler
    {
        void onConfirmation(boolean isConfirmed);
    }
}
