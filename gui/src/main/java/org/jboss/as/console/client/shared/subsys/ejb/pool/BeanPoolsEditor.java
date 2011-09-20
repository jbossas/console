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
package org.jboss.as.console.client.shared.subsys.ejb.pool;

import java.util.List;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.subsys.ejb.pool.model.EJBPool;
import org.jboss.ballroom.client.widgets.ContentGroupLabel;
import org.jboss.ballroom.client.widgets.ContentHeaderLabel;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;

/**
 * @author David Bosschaert
 */
class BeanPoolsEditor {
    private final BeanPoolsPresenter presenter;
    private PoolDetails poolDetails;
    private PoolsTable poolsTable;

    BeanPoolsEditor(BeanPoolsPresenter presenter) {
        this.presenter = presenter;
    }

    Widget asWidget() {
        LayoutPanel layout = new LayoutPanel();
        ScrollPanel scroll = new ScrollPanel();
        VerticalPanel vpanel = new VerticalPanel();
        vpanel.setStyleName("rhs-content-panel");
        scroll.add(vpanel);

        ToolStrip toolStrip = new ToolStrip();
        toolStrip.addToolButtonRight(new ToolButton(Console.CONSTANTS.common_label_add(),
            new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    presenter.launchNewPoolWizard();
                }
            }));
        layout.add(toolStrip);

        vpanel.add(new ContentHeaderLabel("Bean Instance Pools"));
        poolsTable = new PoolsTable();
        vpanel.add(poolsTable.asWidget());

        poolDetails = new PoolDetails(presenter);
        poolDetails.bind(poolsTable.getCellTable());

        TabPanel bottomPanel = new TabPanel();
        bottomPanel.setStyleName("default-tabpanel");
        bottomPanel.add(poolDetails.asWidget(), "Attributes");
        bottomPanel.selectTab(0);

        vpanel.add(new ContentGroupLabel("Pool"));
        vpanel.add(bottomPanel);
        layout.add(scroll);
        layout.setWidgetTopHeight(toolStrip, 0, Style.Unit.PX, 26, Style.Unit.PX);
        layout.setWidgetTopHeight(scroll, 26, Style.Unit.PX, 100, Style.Unit.PCT);

        return layout;
    }

    void updatePools(List<EJBPool> pools) {
        poolsTable.getDataProvider().setList(pools);
    }

    public void enablePoolDetails(boolean b) {
        poolDetails.setEnabled(b);
    }
}
