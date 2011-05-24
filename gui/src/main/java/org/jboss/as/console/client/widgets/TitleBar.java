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

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * A title bar to be displayed at the top of a content view -
 * contains a label and/or an icon.
 *
 * @author Heiko Braun
 */
public class TitleBar extends HorizontalPanel {


    public TitleBar(String title) {
        super();

        getElement().setAttribute("style", "width:100%");

        HTML spacerLeft = new HTML("&nbsp;");
        add(spacerLeft);
        spacerLeft.getElement().getParentElement().setAttribute("style", "border-bottom:1px solid #A7ABB4;");

        add(new TabHeader(title));

        HTML spacerRight= new HTML("&nbsp;");
        add(spacerRight);
        spacerRight.getElement().getParentElement().setAttribute("style", "width:100%;border-bottom:1px solid #A7ABB4;");
    }

    public void setIcon(ImageResource icon) {

    }

}
