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

package org.jboss.as.console.client.widgets.stack;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.StackLayoutPanel;

/**
 * @author Heiko Braun
 * @date 3/28/11
 */
public class DefaultStackLayoutPanel extends StackLayoutPanel {

    public DefaultStackLayoutPanel() {
        super(Style.Unit.PX);

        addStyleName("section-stack");
    }

    @Override
    public void showWidget(int index) {
        super.showWidget(index);

        for(int i=0; i<getWidgetCount(); i++)
        {
            if(index==i)
            {
                getHeaderWidget(i).getElement().addClassName("stack-section-header-selected");
            }
            else
            {
                getHeaderWidget(i).getElement().removeClassName("stack-section-header-selected");
            }
        }
    }

}
