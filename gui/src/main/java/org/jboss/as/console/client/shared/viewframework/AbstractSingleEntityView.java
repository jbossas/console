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
package org.jboss.as.console.client.shared.viewframework;

import java.util.EnumSet;

import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.widgets.forms.PropertyMetaData;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.FormAdapter;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;

/**
 * @author David Bosschaert
 */
public abstract class AbstractSingleEntityView<T> extends AbstractEntityView<T> {
    private final EntityToDmrBridge<T> bridge;

    protected AbstractSingleEntityView(Class<? extends T> beanType, PropertyMetaData propertyMetaData,
            DispatchAsync dispatcher, EnumSet<FrameworkButton> hideButtons) {
        super(beanType, propertyMetaData, hideButtons);
        bridge = new SingleEntityToDmrBridgeImpl<T>(propertyMetaData, beanType, this, dispatcher);
    }

    @Override
    protected EntityToDmrBridge<T> getEntityBridge() {
        return bridge;
    }

    @Override
    protected DefaultCellTable<T> makeEntityTable() {
        DefaultCellTable<T> table = new DefaultCellTable<T>(5);
        table.setVisible(false);
        return table;
    }

    @Override
    protected FormAdapter<T> makeAddEntityForm() {
        return new Form<T>(beanType);
    }
}
