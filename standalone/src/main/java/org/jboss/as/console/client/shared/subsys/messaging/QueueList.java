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

package org.jboss.as.console.client.shared.subsys.messaging;

import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.subsys.messaging.model.Queue;
import org.jboss.as.console.client.widgets.tables.DefaultCellTable;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 5/10/11
 */
public class QueueList {

    DefaultCellTable<Queue> queueTable;

    Widget asWidget() {
        
        VerticalPanel layout = new VerticalPanel();
        
        queueTable = new DefaultCellTable<Queue>(10);
        queueTable.getElement().setAttribute("style", "margin-top:10px" );
        TextColumn<Queue> nameColumn = new TextColumn<Queue>() {
            @Override
            public String getValue(Queue record) {
                return record.getName();
            }
        };

        TextColumn<Queue> jndiNameColumn = new TextColumn<Queue>() {
            @Override
            public String getValue(Queue record) {
                return record.getJndiName();
            }
        };
        
        
        TextColumn<Queue> durableColumn = new TextColumn<Queue>() {
            @Override
            public String getValue(Queue record) {
                return String.valueOf(record.isDurable());
            }
        };
        
        TextColumn<Queue> selectorColumn = new TextColumn<Queue>() {
            @Override
            public String getValue(Queue record) {
                return record.getSelector();
            }
        };
        
        queueTable.addColumn(nameColumn, "Name");
        queueTable.addColumn(jndiNameColumn, "JNDI");
        queueTable.addColumn(selectorColumn, "Selector");
        queueTable.addColumn(durableColumn, "Durable?");

        layout.add(queueTable);
        return layout;
    }

    void setQueues(List<Queue> queues)
    {
        queueTable.setRowData(0, queues);
    }

}
