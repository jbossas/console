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

package org.jboss.as.console.client.shared.subsys.jca;

import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.PageSizePager;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.Range;
import com.google.gwt.view.client.RangeChangeEvent;
import org.jboss.as.console.client.shared.subsys.jca.model.DataSource;
import org.jboss.as.console.client.widgets.DefaultButton;
import org.jboss.as.console.client.widgets.icons.Icons;
import org.jboss.as.console.client.widgets.tables.DefaultCellTable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 5/4/11
 */
public class DatasourceTable {

    private static final int PAGE_SIZE = 5;
    private CellTable<DataSource> dataSourceTable;

    Widget asWidget() {

        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("fill-layout-width");

        dataSourceTable = new DefaultCellTable<DataSource>(PAGE_SIZE);

        TextColumn<DataSource> nameColumn = new TextColumn<DataSource>() {
            @Override
            public String getValue(DataSource record) {
                return record.getName();
            }
        };

        TextColumn<DataSource> jndiNameColumn = new TextColumn<DataSource>() {
            @Override
            public String getValue(DataSource record) {
                return record.getJndiName();
            }
        };

        TextColumn<DataSource> poolColumn = new TextColumn<DataSource>() {
            @Override
            public String getValue(DataSource record) {
                return record.getPoolName();
            }
        };

        Column<DataSource, ImageResource> statusColumn =
                new Column<DataSource, ImageResource>(new ImageResourceCell()) {
                    @Override
                    public ImageResource getValue(DataSource dataSource) {

                        ImageResource res = null;

                        if(dataSource.isEnabled())
                            res = Icons.INSTANCE.statusGreen_small();
                        else
                            res = Icons.INSTANCE.statusRed_small();

                        return res;
                    }
                };


        dataSourceTable.addColumn(nameColumn, "Name");
        dataSourceTable.addColumn(jndiNameColumn, "JNDI");
        dataSourceTable.addColumn(poolColumn, "Pool");
        dataSourceTable.addColumn(statusColumn, "Enabled?");

        layout.add(dataSourceTable);

        // ---
        // http://code.google.com/p/google-web-toolkit/issues/detail?id=4988

        SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
        SimplePager pager = new SimplePager(SimplePager.TextLocation.CENTER, pagerResources, false, 0, true);
        //pager.setPageSize(PAGE_SIZE);
        //pager.setPageStart(0);

        //pager.setDisplay(dataSourceTable);

        //layout.add(pager);

        return layout;
    }

    public CellTable<DataSource> getCellTable() {
        return dataSourceTable;
    }
}
