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

import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.subsys.threads.model.ThreadFactory;
import org.jboss.as.console.client.shared.viewframework.AbstractEntityView;
import org.jboss.as.console.client.shared.viewframework.Columns.NameColumn;
import org.jboss.as.console.client.shared.viewframework.EntityToDmrBridgeImpl;
import org.jboss.as.console.client.shared.viewframework.FrameworkView;
import org.jboss.ballroom.client.widgets.forms.ObservableFormItem;
import org.jboss.as.console.client.shared.viewframework.EntityToDmrBridge;
import org.jboss.as.console.client.widgets.forms.PropertyMetaData;
import org.jboss.ballroom.client.widgets.forms.ComboBoxItem;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.FormAdapter;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;

/**
 * Main view class for Thread Factories.
 * 
 * @author Stan Silvert
 */
public class ThreadFactoryView extends AbstractEntityView<ThreadFactory> implements FrameworkView {

    private EntityToDmrBridge threadPoolBridge;
    private BoundedQueueThreadPoolView poolview;

    public ThreadFactoryView(PropertyMetaData propertyMetaData, DispatchAsync dispatcher, BoundedQueueThreadPoolView poolView) {
        super(ThreadFactory.class, propertyMetaData);
        this.poolview = poolView;
        threadPoolBridge = new EntityToDmrBridgeImpl<ThreadFactory>(propertyMetaData, ThreadFactory.class, this, dispatcher);
    }

    @Override
    public Widget createWidget() {

        return super.createEmbeddableWidget();
    }

    @Override
    public void itemAction(Action action, ObservableFormItem item) {
        if (item.getPropertyBinding().getJavaName().equals("priority") && (action == Action.CREATED)) {
            ComboBoxItem comboBox = (ComboBoxItem) item.getWrapped();
            comboBox.setValueMap(new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"});
        }
    }

    @Override
    protected EntityToDmrBridge getEntityBridge() {
        return this.threadPoolBridge;
    }

    @Override
    protected String getEntityDisplayName() {
        return "Thread Factories";
    }

    @Override
    protected FormAdapter<ThreadFactory> makeAddEntityForm() {
        Form<ThreadFactory> form = new Form(ThreadFactory.class);
        form.setNumColumns(1);
        form.setFields(formMetaData.findAttribute("name").getFormItemForAdd(),
                       formMetaData.findAttribute("priority").getFormItemForAdd(this));
        return form;
    }

    @Override
    protected DefaultCellTable<ThreadFactory> makeEntityTable() {
        DefaultCellTable<ThreadFactory> table = new DefaultCellTable<ThreadFactory>(4);

        table.addColumn(new NameColumn(), NameColumn.LABEL);

        return table;
    }

    @Override
    public void refresh() {
        super.refresh();
        this.poolview.setThreadFactoryComboValues(this.getEntityBridge().getEntityList());
    }
    
}
