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

package org.jboss.as.console.client.debug;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.widgets.LHSNavItem;
import org.jboss.as.console.client.widgets.StackSectionHeader;

/**
 * @author Heiko Braun
 * @date 2/10/11
 */
public class LHSDebugToolsNavigation {

    private StackLayoutPanel stack;
    TreeItem subsysRoot;

    public LHSDebugToolsNavigation() {
        super();

        stack = new StackLayoutPanel(Style.Unit.PX);
        stack.addStyleName("section-stack");
        stack.setWidth("180");

        LayoutPanel commonLayout = new LayoutPanel();
        commonLayout.setStyleName("stack-section");

        LHSNavItem[] commonItems = new LHSNavItem[] {
                new LHSNavItem("Browser", "debug/model-browser"),
                new LHSNavItem("Invocation Metrics", "debug/invocation-metrics")
                //new LHSNavItem("Operations", "debug/model-operations"),
        };

        int i =0;
        for(LHSNavItem item : commonItems)
        {
            commonLayout.add(item);
            commonLayout.setWidgetTopHeight(item, i, Style.Unit.PX, 25, Style.Unit.PX);
            i+=25;
        }

        stack.add(commonLayout, new StackSectionHeader("Domain Model"), 28);

    }

    public Widget asWidget()
    {
        return stack;
    }
}