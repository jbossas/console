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

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TreeItem;

/**
 * @author Heiko Braun
 * @date 3/24/11
 */
public class LHSNavTreeItem extends TreeItem {


    public LHSNavTreeItem(String text, String token) {
        setText(text);
        setStyleName("lhs-tree-item");
        getElement().setAttribute("token", token);
    }

    public LHSNavTreeItem(String text, ClickHandler handler) {
        setText(text);
        HTML html = new HTML(text);
        html.addClickHandler(handler);
        setWidget(html);
        setStyleName("lhs-tree-item");
    }

    /*public LHSNavTreeItem(String text, ImageResource icon, String token) {

        Image img = new Image(icon);
        Label label = new Label(text);

        HorizontalPanel horz = new HorizontalPanel();
        horz.getElement().setAttribute("style", "padding:0px;");
        horz.add(img);
        horz.add(label);

        img.getElement().getParentElement().setAttribute("style", "vertical-align:middle;padding-right:5px;");
        label.getElement().getParentElement().setAttribute("style", "vertical-align:middle");

        setWidget(horz);

        setStyleName("lhs-tree-item");
        getElement().setAttribute("token", token);
    } */

    public void setSelected(boolean selected) {
        super.setSelected(selected);
        if(selected)
            addStyleName("lhs-tree-item-selected");
        else
            removeStyleName("lhs-tree-item-selected");
    }

}
