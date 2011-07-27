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
import org.jboss.as.console.client.shared.subsys.messaging.model.JMSEndpoint;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;

/**
 * @author Heiko Braun
 * @date 3/29/11
 */
class EndpointTable extends DefaultCellTable<JMSEndpoint>{

    public EndpointTable() {
        super(20);

        TextColumn<JMSEndpoint> nameColumn = new TextColumn<JMSEndpoint>() {
            @Override
            public String getValue(JMSEndpoint record) {
                return record.getName();
            }
        };

        TextColumn<JMSEndpoint> jndiNameColumn = new TextColumn<JMSEndpoint>() {
            @Override
            public String getValue(JMSEndpoint record) {
                return record.getJndiName();
            }
        };


        addColumn(nameColumn, "Name");
        addColumn(jndiNameColumn, "JNDI");

    }
}
