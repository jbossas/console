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

package org.jboss.as.console.client.server.subsys.threads;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.widgets.RHSHeader;

/**
 * @author Heiko Braun
 * @date 2/9/11
 */
public class ThreadManagementView extends SuspendableViewImpl implements ThreadManagementPresenter.MyView {

    private ThreadManagementPresenter presenter;

    @Override
    public void setPresenter(ThreadManagementPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget createWidget() {

        LayoutPanel layout = new LayoutPanel();

        RHSHeader title = new RHSHeader("Thread Management");
        layout.add(title);
        layout.setWidgetTopHeight(title, 0, Style.Unit.PX, 28, Style.Unit.PX);

        VerticalPanel vpanel = new VerticalPanel();
        vpanel.setStyleName("fill-layout");
        vpanel.getElement().setAttribute("style", "padding:15px;");

        // -----------

        TabLayoutPanel tabLayoutpanel = new TabLayoutPanel(25, Style.Unit.PX);
        tabLayoutpanel.addStyleName("default-tabpanel");

        ThreadFactoryList threadFactoryList = new ThreadFactoryList(presenter);
        tabLayoutpanel.add(threadFactoryList, "Default Thread Factories");
        tabLayoutpanel.add(new HTML("Bar"), "Bounded Queue Factories");
        tabLayoutpanel.add(new HTML("Baz"), "Other");

        tabLayoutpanel.selectTab(0);

        vpanel.add(tabLayoutpanel);

        layout.add(vpanel);
        layout.setWidgetTopHeight(vpanel, 35, Style.Unit.PX, 100, Style.Unit.PCT);

        return layout;
    }

}