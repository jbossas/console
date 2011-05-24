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

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Heiko Braun
 * @date 4/29/11
 */
public class EditOverlay {

    static PopupPanel p = null;

    public static void on(Widget parent, boolean isBeingEdited)
    {
        if(parent !=null && isBeingEdited)
        {
            int left = parent.getAbsoluteLeft();
            int top = parent.getAbsoluteTop();

            int width = parent.getOffsetWidth();
            int height = parent.getOffsetHeight();

            p = new PopupPanel();
            HTML widget = new HTML();
            widget.setText("Editing ...");
            widget.setStyleName("edit-overlay");
            p.setWidget(widget);
            p.setPopupPosition(left+(width)-75, top);
            p.show();

        }
        else
        {
            if(p!=null)
            {
                p.hide();
                p = null;
            }
        }
    }

    public static void hide() {
        if(p!=null) p.hide();
    }
}
