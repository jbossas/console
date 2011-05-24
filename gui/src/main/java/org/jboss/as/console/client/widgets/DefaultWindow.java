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
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.widgets.icons.Icons;

/**
 * @author Heiko Braun
 * @date 2/23/11
 */
public class DefaultWindow extends PopupPanel {

    public final static double GOLDEN_RATIO = 1.618;
    private static final int ESCAPE = 27;

    LayoutPanel content;

    int width, height;

    public DefaultWindow(String title) {

        DockLayoutPanel layout = new DockLayoutPanel(Style.Unit.PX);
        setStyleName("default-window");

        HorizontalPanel header = new HorizontalPanel();
        header.setStyleName("default-window-header");
        header.getElement().setAttribute("cellpadding", "4");

        HTML titleText = new HTML(title);

        Image closeIcon = new Image(Icons.INSTANCE.close());
        closeIcon.setAltText("Close");
        closeIcon.addClickHandler(new ClickHandler(){
            @Override
            public void onClick(ClickEvent clickEvent) {
                hide();
            }
        });

        header.add(titleText);
        header.add(closeIcon);

        // it's just a table ...
        titleText.getElement().getParentElement().setAttribute("width", "100%");
        closeIcon.getElement().getParentElement().setAttribute("width", "16px");

        //header.setWidgetRightWidth(closeIcon, 5, Style.Unit.PX, 16, Style.Unit.PX);
        //header.setWidgetRightWidth(titleText, 21, Style.Unit.PX, 95, Style.Unit.PCT);

        layout.addNorth(header, 25);

        content = new LayoutPanel();
        content.getElement().setAttribute("style", "margin:5px;");
        layout.add(content);

        super.setWidget(layout);

        // default width(height
        int winWidth = (int)(Window.getClientWidth()*0.9);
        int winHeight = (int) ( winWidth / GOLDEN_RATIO );

        setWidth(winWidth);
        setHeight(winHeight);

        //layout.setWidgetTopHeight(header, 0, Style.Unit.PX, 25, Style.Unit.PX);
        //layout.setWidgetTopHeight(content, 25, Style.Unit.PX, 500, Style.Unit.PX);
    }

    @Override
    protected void onPreviewNativeEvent(Event.NativePreviewEvent event) {
        if (Event.ONKEYUP == event.getTypeInt()) {
            if (event.getNativeEvent().getKeyCode() == ESCAPE) {
                // Dismiss when escape is pressed
                hide();
            }
        }
    }

    @Override
    public void setWidget(Widget w) {
        content.clear();
        content.add(w);
    }

    @Override
    public void center() {
        setPopupPosition(
                (Window.getClientWidth()/2)-(width/2),
                (Window.getClientHeight()/2)-(height/2)
        );
        show();

        super.setWidth(width+"px");
        super.setHeight(height+"px");
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public void setWidth(String width) {
        throw new IllegalArgumentException("Use the numeric setter!") ;
    }

    @Override
    public void setHeight(String height) {
        throw new IllegalArgumentException("Use the numeric setter!") ;
    }
}
