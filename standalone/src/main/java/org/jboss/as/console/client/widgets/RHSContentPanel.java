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

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Heiko Braun
 * @date 2/22/11
 */
public class RHSContentPanel extends LayoutPanel {

    private VerticalPanel delegate;

    public RHSContentPanel(String title) {

        super();

        TitleBar titleBar = new TitleBar(title);
        super.add(titleBar);
        super.setWidgetTopHeight(titleBar, 0, Style.Unit.PX, 28, Style.Unit.PX);

        ScrollPanel scroll = new ScrollPanel();

        delegate = new VerticalPanel();
        delegate.setStyleName("fill-layout-width");
        delegate.getElement().setAttribute("style", "padding:15px;");

        scroll.add(delegate);
        super.add(scroll);
        super.setWidgetTopHeight(scroll, 35, Style.Unit.PX, 100, Style.Unit.PCT);

    }

    @Override
    public void add(Widget widget) {
        delegate.add(widget);
    }

}
