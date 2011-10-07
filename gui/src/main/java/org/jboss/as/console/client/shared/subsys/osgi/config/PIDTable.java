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
package org.jboss.as.console.client.shared.subsys.osgi.config;

import java.util.List;

import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SingleSelectionModel;

import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.subsys.osgi.config.model.OSGiConfigAdminData;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tables.DefaultPager;

/**
 * @author David Bosschaert
 */
public class PIDTable {

    private DefaultCellTable<OSGiConfigAdminData> table;
    private ListDataProvider<OSGiConfigAdminData> dataProvider;
    private SingleSelectionModel<OSGiConfigAdminData> selectionModel;

    Widget asWidget() {
        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("fill-layout-width");

        table = new DefaultCellTable<OSGiConfigAdminData>(20);
        dataProvider = new ListDataProvider<OSGiConfigAdminData>();
        dataProvider.addDataDisplay(table);

        TextColumn<OSGiConfigAdminData> pidColumn = new TextColumn<OSGiConfigAdminData>() {
            @Override
            public String getValue(OSGiConfigAdminData pidEntry) {
                return pidEntry.getPid();
            }
        };
        table.addColumn(pidColumn, Console.CONSTANTS.subsys_osgi_configAdminPIDShort());
        layout.add(table);

        DefaultPager pager = new DefaultPager();
        pager.setDisplay(table);
        layout.add(pager);

        return layout;
    }

    void setSelectionModel(SingleSelectionModel<OSGiConfigAdminData> sm) {
        table.setSelectionModel(sm);
        selectionModel = sm;
    }

    public OSGiConfigAdminData getSelection() {
        return selectionModel.getSelectedObject();
    }

    public List<OSGiConfigAdminData> getData() {
        return dataProvider.getList();
    }

    void setData(List<OSGiConfigAdminData> data, String selectPid) {
        OSGiConfigAdminData sel = selectionModel.getSelectedObject();
        if (selectPid == null && sel != null)
            selectPid = sel.getPid();

        dataProvider.getList().clear();
        dataProvider.getList().addAll(data);

        if (selectPid != null) {
            for (OSGiConfigAdminData d : data) {
                if (d.getPid().equals(selectPid)) {
                    selectionModel.setSelected(d, true);
                    return;
                }
            }
        }

        // No previous selection
        if (data.size() > 0) {
            selectionModel.setSelected(data.get(0), true);
        }
    }
}
