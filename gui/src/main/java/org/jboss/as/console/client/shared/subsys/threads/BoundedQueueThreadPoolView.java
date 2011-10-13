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
package org.jboss.as.console.client.shared.subsys.threads;

import com.google.gwt.user.client.ui.Widget;
import java.util.ArrayList;
import java.util.List;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.subsys.threads.model.BoundedQueueThreadPool;
import org.jboss.as.console.client.shared.viewframework.AbstractEntityView;
import org.jboss.as.console.client.shared.viewframework.Columns.NameColumn;
import org.jboss.as.console.client.shared.viewframework.EntityToDmrBridgeImpl;
import org.jboss.as.console.client.shared.viewframework.FrameworkView;
import org.jboss.as.console.client.shared.viewframework.NamedEntity;
import org.jboss.ballroom.client.widgets.forms.ObservableFormItem;
import org.jboss.as.console.client.shared.viewframework.EntityToDmrBridge;
import org.jboss.as.console.client.widgets.forms.PropertyMetaData;
import org.jboss.ballroom.client.widgets.forms.ComboBoxItem;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.FormAdapter;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;

/**
 * Main view class for Bounded Queue Thread Pools
 * 
 * @author Stan Silvert
 */
public class BoundedQueueThreadPoolView extends AbstractEntityView<BoundedQueueThreadPool> implements FrameworkView {

    private EntityToDmrBridge threadPoolBridge;
    private ComboBoxItem threadFactoryComboForAdd;
    private ComboBoxItem threadFactoryComboForEdit;

    public BoundedQueueThreadPoolView(PropertyMetaData propertyMetaData, DispatchAsync dispatcher) {
        super(BoundedQueueThreadPool.class, propertyMetaData); //, FrameworkButton.asSet(FrameworkButton.values()));
        threadPoolBridge = new EntityToDmrBridgeImpl<BoundedQueueThreadPool>(propertyMetaData, BoundedQueueThreadPool.class, this, dispatcher);
    }

    @Override
    public void itemAction(Action action, ObservableFormItem item) {
        if (item.getPropertyBinding().getJavaName().equals("threadFactory") && (action == Action.CREATED)) {
            ComboBoxItem comboBox = (ComboBoxItem) item.getWrapped();
            if (threadFactoryComboForAdd == null) {
                threadFactoryComboForAdd = comboBox;
            } else {
                threadFactoryComboForEdit = comboBox;
            }
        }
    }

    public void setThreadFactoryComboValues(List<NamedEntity> threadFactories) {
        List<String> factoryNames = new ArrayList<String>();
        factoryNames.add("");  // factory not required.  Empty String is a valid choice.
        for (NamedEntity factory : threadFactories) {
            factoryNames.add(factory.getName());
        }

        this.threadFactoryComboForAdd.setValueMap(factoryNames);
        this.threadFactoryComboForEdit.setValueMap(factoryNames);
    }

    @Override
    public Widget createWidget() {
        entityEditor = makeEntityEditor();
        return entityEditor.asWidget();
    }

    @Override
    protected EntityToDmrBridge getEntityBridge() {
        return this.threadPoolBridge;
    }

    @Override
    protected String getPluralEntityName() {
        return "Bounded Queue Thread Pools";
    }

    @Override
    protected FormAdapter<BoundedQueueThreadPool> makeAddEntityForm() {
        Form<BoundedQueueThreadPool> form = new Form(BoundedQueueThreadPool.class);
        form.setNumColumns(1);
        form.setFields(formMetaData.findAttribute("name").getFormItemForAdd(),
                       formMetaData.findAttribute("threadFactory").getFormItemForAdd(this));
        return form;
    }

    @Override
    protected DefaultCellTable<BoundedQueueThreadPool> makeEntityTable() {
        DefaultCellTable<BoundedQueueThreadPool> table = new DefaultCellTable<BoundedQueueThreadPool>(4);

        table.addColumn(new NameColumn(), NameColumn.LABEL);

        return table;
    }
}
