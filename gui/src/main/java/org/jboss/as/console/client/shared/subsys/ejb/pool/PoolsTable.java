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

import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

import org.jboss.as.console.client.shared.subsys.ejb.pool.model.EJBPool;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tables.DefaultPager;

/**
 * @author David Bosschaert
 */
class PoolsTable {
    private static final int PAGE_SIZE = 5;
    private CellTable<EJBPool> poolsTable;
    private ListDataProvider<EJBPool> dataProvider;

    Widget asWidget() {
        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("fill-layout-width");

        poolsTable = new DefaultCellTable<EJBPool>(PAGE_SIZE);
        dataProvider = new ListDataProvider<EJBPool>();
        dataProvider.addDataDisplay(poolsTable);

        TextColumn<EJBPool> nameColumn = new TextColumn<EJBPool>() {
            @Override
            public String getValue(EJBPool record) {
                return record.getName();
            }
        };
        poolsTable.addColumn(nameColumn, "Name");
        layout.add(poolsTable);

        DefaultPager pager = new DefaultPager();
        pager.setDisplay(poolsTable);
        layout.add(pager);

        return layout;
    }

    CellTable<EJBPool> getCellTable() {
        return poolsTable;
    }

    ListDataProvider<EJBPool> getDataProvider() {
        return dataProvider;
    }
}
