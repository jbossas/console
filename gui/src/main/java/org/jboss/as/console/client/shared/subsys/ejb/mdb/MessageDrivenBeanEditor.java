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
package org.jboss.as.console.client.shared.subsys.ejb.mdb;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import org.jboss.ballroom.client.widgets.ContentGroupLabel;
import org.jboss.ballroom.client.widgets.ContentHeaderLabel;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;

/**
 * @author David Bosschaert
 */
public class MessageDrivenBeanEditor {
    private final MessageDrivenBeanPresenter presenter;

    public MessageDrivenBeanEditor(MessageDrivenBeanPresenter presenter) {
        this.presenter = presenter;
    }

    public Widget asWidget() {
        LayoutPanel layout = new LayoutPanel();
        ScrollPanel scroll = new ScrollPanel();
        VerticalPanel vpanel = new VerticalPanel();
        vpanel.setStyleName("rhs-content-panel");
        scroll.add(vpanel);

        // Add an empty toolstrip to make this panel look similar to others
        ToolStrip toolStrip = new ToolStrip();
        layout.add(toolStrip);

        vpanel.add(new ContentHeaderLabel("Message Driven Beans"));
        vpanel.add(new ContentGroupLabel("Bean Instance Pools"));

        layout.add(scroll);
        layout.setWidgetTopHeight(toolStrip, 0, Style.Unit.PX, 26, Style.Unit.PX);
        layout.setWidgetTopHeight(scroll, 26, Style.Unit.PX, 100, Style.Unit.PCT);

        return layout;
    }
}
