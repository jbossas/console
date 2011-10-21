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

import java.util.Collection;

import com.google.gwt.user.client.ui.Widget;

import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.subsys.ejb3.model.StrictMaxBeanPool;
import org.jboss.as.console.client.shared.viewframework.AbstractEntityView;
import org.jboss.as.console.client.shared.viewframework.Columns;
import org.jboss.as.console.client.shared.viewframework.EntityToDmrBridge;
import org.jboss.as.console.client.shared.viewframework.EntityToDmrBridgeImpl;
import org.jboss.as.console.client.widgets.forms.PropertyMetaData;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.FormAdapter;
import org.jboss.ballroom.client.widgets.forms.FormItem;
import org.jboss.ballroom.client.widgets.forms.ObservableFormItem;
import org.jboss.ballroom.client.widgets.forms.UnitBoxItem;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;

/**
 * @author David Bosschaert
 */
public class BeanPoolsView extends AbstractEntityView<StrictMaxBeanPool> {
    private final EntityToDmrBridgeImpl<StrictMaxBeanPool> bridge;
    private UnitBoxItem<?> timeoutItem;

    public BeanPoolsView(PropertyMetaData propertyMetaData, DispatchAsync dispatcher) {
        super(StrictMaxBeanPool.class, propertyMetaData);
        bridge = new EntityToDmrBridgeImpl<StrictMaxBeanPool>(propertyMetaData, StrictMaxBeanPool.class, this, dispatcher);
    }

    @Override
    public Widget createWidget() {
        return createEmbeddableWidget();
    }

    @Override
    public void itemAction(Action action, ObservableFormItem item) {
        if (item.getPropertyBinding().getJavaName().equals("timeout") && action == Action.CREATED) {
            FormItem<?> wrapped = item.getWrapped();
            if (wrapped instanceof UnitBoxItem) {
                timeoutItem = (UnitBoxItem<?>) wrapped;
                timeoutItem.setUnitPropertyName("timeoutUnit");
            }
        }
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
    protected String getEntityDisplayName() {
        return "Bean Pools";
    }

    void setTimeoutUnits(Collection<String> units, String defUnit) {
        if (timeoutItem != null) {
            timeoutItem.setChoices(units, defUnit);
        }
    }
}
