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
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Heiko Braun
 * @date 2/23/11
 */
public class DefaultWindow extends ResizePanel {

    public final static double GOLDEN_RATIO = 1.618;
    private static final int ESCAPE = 27;

    LayoutPanel content;

    int width, height;

    private boolean dragged       = false;
    private int     dragStartX;
    private int     dragStartY;

    private Command afterShowEvent;
    private boolean fixedLocation = false;

    public DefaultWindow(String title) {

        DockLayoutPanel layout = new DockLayoutPanel(Style.Unit.PX);
        setStyleName("default-window");

        final PopupTitleBar header = new PopupTitleBar(title, this);

        // dnd

        header.addMouseDownHandler( new MouseDownHandler() {

            public void onMouseDown(MouseDownEvent event) {
                dragged = true;
                dragStartX = event.getRelativeX( getElement() );
                dragStartY = event.getRelativeY( getElement() );
                DOM.setCapture(header.getElement());
            }
        } );
        header.addMouseMoveHandler( new MouseMoveHandler() {

            public void onMouseMove(MouseMoveEvent event) {
                if ( dragged ) {
                    setPopupPosition( event.getClientX() - dragStartX,
                            event.getClientY() - dragStartY );
                }
            }
        } );
        header.addMouseUpHandler( new MouseUpHandler() {

            public void onMouseUp(MouseUpEvent event) {
                dragged = false;
                DOM.releaseCapture( header.getElement() );
            }
        } );


        layout.addNorth(header, 40);

        content = new LayoutPanel();
        content.setStyleName("default-window-content");
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
        super.setWidth(width);
    }

    @Override
    public void setHeight(String height) {
        super.setHeight(height);
    }
}
