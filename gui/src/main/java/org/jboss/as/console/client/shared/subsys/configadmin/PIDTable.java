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
package org.jboss.as.console.client.shared.subsys.configadmin;

import java.util.List;

import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SingleSelectionModel;

import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.subsys.configadmin.model.ConfigAdminData;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tables.DefaultPager;

/**
 * @author David Bosschaert
 */
public class PIDTable {

    private DefaultCellTable<ConfigAdminData> table;
    private ListDataProvider<ConfigAdminData> dataProvider;
    private SingleSelectionModel<ConfigAdminData> selectionModel;

    Widget asWidget() {
        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("fill-layout-width");

        table = new DefaultCellTable<ConfigAdminData>(10);
        dataProvider = new ListDataProvider<ConfigAdminData>();
        dataProvider.addDataDisplay(table);

        TextColumn<ConfigAdminData> pidColumn = new TextColumn<ConfigAdminData>() {
            @Override
            public String getValue(ConfigAdminData pidEntry) {
                return pidEntry.getPid();
            }
        };
        table.addColumn(pidColumn, Console.CONSTANTS.subsys_configadmin_PIDShort());
        layout.add(table);

        DefaultPager pager = new DefaultPager();
        pager.setDisplay(table);
        layout.add(pager);

        return layout;
    }

    void setSelectionModel(SingleSelectionModel<ConfigAdminData> sm) {
        table.setSelectionModel(sm);
        selectionModel = sm;
    }

    public ConfigAdminData getSelection() {
        return selectionModel.getSelectedObject();
    }

    public List<ConfigAdminData> getData() {
        return dataProvider.getList();
    }

    void setData(List<ConfigAdminData> data, String selectPid) {
        ConfigAdminData sel = selectionModel.getSelectedObject();
        if (selectPid == null && sel != null)
            selectPid = sel.getPid();

        dataProvider.getList().clear();
        dataProvider.getList().addAll(data);

        if (selectPid != null) {
            for (ConfigAdminData d : data) {
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
