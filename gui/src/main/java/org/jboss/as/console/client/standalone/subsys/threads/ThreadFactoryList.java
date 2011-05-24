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

package org.jboss.as.console.client.standalone.subsys.threads;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.LayoutPanel;
import org.jboss.as.console.client.widgets.tables.DefaultCellTable;

import java.util.Collections;

/**
 * @author Heiko Braun
 * @date 2/10/11
 */
class ThreadFactoryList extends LayoutPanel {

    public ThreadFactoryList(final ThreadManagementPresenter presenter) {

        DefaultCellTable factoryTable = new DefaultCellTable(10);

        TextColumn<ThreadFactoryRecord> nameColumn = new TextColumn<ThreadFactoryRecord>() {
            @Override
            public String getValue(ThreadFactoryRecord record) {
                return record.getName();
            }
        };

        TextColumn<ThreadFactoryRecord> groupColumn = new TextColumn<ThreadFactoryRecord>() {
            @Override
            public String getValue(ThreadFactoryRecord record) {
                return record.getGroup();
            }
        };

        TextColumn<ThreadFactoryRecord> prioColumn = new TextColumn<ThreadFactoryRecord>() {
            @Override
            public String getValue(ThreadFactoryRecord record) {
                return String.valueOf(record.getPriority());
            }
        };

        factoryTable.addColumn(nameColumn, "Factory Name");
        factoryTable.addColumn(groupColumn, "Group");
        factoryTable.addColumn(prioColumn, "Priority");

        factoryTable.setRowData(0, Collections.EMPTY_LIST);
        add(factoryTable);
        setWidgetTopHeight(factoryTable, 0, Style.Unit.PX, 100, Style.Unit.PCT);
    }
}
