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
package org.jboss.as.console.client.shared.subsys.osgi;

import java.util.Comparator;
import java.util.List;

import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SingleSelectionModel;

import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.subsys.osgi.model.OSGiCapability;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tables.DefaultPager;

/**
 * @author David Bosschaert
 */
public class CapabilitiesTable {
    private CellTable<OSGiCapability> table;
    private ListDataProvider<OSGiCapability> dataProvider;
    private SingleSelectionModel<OSGiCapability> selectionModel;

    Widget asWidget() {
        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("fill-layout-width");

        table = new DefaultCellTable<OSGiCapability>(25);
        dataProvider = new ListDataProvider<OSGiCapability>();
        dataProvider.addDataDisplay(table);

        ColumnSortEvent.ListHandler<OSGiCapability> sortHandler =
            new ColumnSortEvent.ListHandler<OSGiCapability>(dataProvider.getList());

        TextColumn<OSGiCapability> identifierColumn = new TextColumn<OSGiCapability>() {
            @Override
            public String getValue(OSGiCapability record) {
                return record.getIdentifier();
            }
        };
        identifierColumn.setSortable(true);
        sortHandler.setComparator(identifierColumn, new Comparator<OSGiCapability>() {
            @Override
            public int compare(OSGiCapability o1, OSGiCapability o2) {
                return o1.getIdentifier().compareTo(o2.getIdentifier());
            }
        });

        TextColumn<OSGiCapability> startLevelColumn = new TextColumn<OSGiCapability>() {
            @Override
            public String getValue(OSGiCapability record) {
                if (record.getStartLevel() == null)
                    return "";
                else
                    return record.getStartLevel();
            }
        };
        startLevelColumn.setSortable(true);
        sortHandler.setComparator(startLevelColumn, new Comparator<OSGiCapability>() {
            @Override
            public int compare(OSGiCapability o1, OSGiCapability o2) {
                if (o1.getStartLevel() == null) {
                    // Don't use MIN_VALUE because -MIN_VALUE doesn't fit in an int
                    return -Integer.MAX_VALUE;
                }
                if (o2.getStartLevel() == null) {
                    return Integer.MAX_VALUE;
                }
                int cv = o1.getStartLevel().compareTo(o2.getStartLevel());
                if (cv != 0)
                    return cv;

                // Sort on identifier within startlevel
                return o1.getIdentifier().compareTo(o2.getIdentifier());
            }
        });

        table.addColumn(identifierColumn, Console.CONSTANTS.subsys_osgi_capabilityId());
        table.addColumn(startLevelColumn, Console.CONSTANTS.subsys_osgi_capabilityStartLevel());
        table.addColumnSortHandler(sortHandler);
        table.getColumnSortList().push(identifierColumn);

        selectionModel = new SingleSelectionModel<OSGiCapability>();
        table.setSelectionModel(selectionModel);
        layout.add(table);

        DefaultPager pager = new DefaultPager();
        pager.setDisplay(table);
        layout.add(pager);

        return layout;
    }

    CellTable<OSGiCapability> getCellTable() {
        return table;
    }

    OSGiCapability getSelection() {
        return selectionModel.getSelectedObject();
    }

    void setCapabilities(List<OSGiCapability> capabilities) {
        dataProvider.getList().clear();
        dataProvider.getList().addAll(capabilities);
    }
}
