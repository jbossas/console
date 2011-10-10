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
package org.jboss.as.console.client.shared.subsys.ejb3;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.subsys.ejb3.model.StrictMaxBeanPool;
import org.jboss.as.console.client.shared.viewframework.AbstractEntityView;
import org.jboss.as.console.client.shared.viewframework.Columns;
import org.jboss.as.console.client.shared.viewframework.EntityToDmrBridge;
import org.jboss.as.console.client.shared.viewframework.EntityToDmrBridgeImpl;
import org.jboss.as.console.client.widgets.forms.FormMetaData;
import org.jboss.as.console.client.widgets.forms.PropertyMetaData;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.FormAdapter;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;

/**
 * @author David Bosschaert
 */
public class PoolsSection extends AbstractEntityView<StrictMaxBeanPool> {
    private final EntityToDmrBridgeImpl<StrictMaxBeanPool> bridge;
    private final FormMetaData formMetaData;

    public PoolsSection(PropertyMetaData propertyMetaData, DispatchAsync dispatcher) {
        super(StrictMaxBeanPool.class);

        formMetaData = propertyMetaData.getBeanMetaData(StrictMaxBeanPool.class).getFormMetaData();
        bridge = new EntityToDmrBridgeImpl<StrictMaxBeanPool>(propertyMetaData, StrictMaxBeanPool.class, this, dispatcher);
    }

    @Override
    public Widget createWidget() {
        entityEditor = makeEntityEditor();
        VerticalPanel vpanel = new VerticalPanel();
        entityEditor.addWidgetToPanel(vpanel);
        return vpanel;
    }

    @Override
    protected FormMetaData getFormMetaData() {
        return formMetaData;
    }

    @Override
    protected EntityToDmrBridge<StrictMaxBeanPool> getEntityBridge() {
        return bridge;
    }

    @Override
    protected DefaultCellTable<StrictMaxBeanPool> makeEntityTable() {
        DefaultCellTable<StrictMaxBeanPool> table = new DefaultCellTable<StrictMaxBeanPool>(10);
        table.addColumn(new Columns.NameColumn(), Columns.NameColumn.LABEL);
        return table;
    }

    @Override
    protected FormAdapter<StrictMaxBeanPool> makeAddEntityForm() {
        Form<StrictMaxBeanPool> form = new Form<StrictMaxBeanPool>(StrictMaxBeanPool.class);
        form.setNumColumns(1);
        form.setFields(getFormMetaData().findAttribute("name").getFormItemForAdd());
        return form;
    }

    @Override
    protected String getPluralEntityName() {
        return "Pools";
    }

}
