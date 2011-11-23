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

import com.google.gwt.user.cellview.client.TextColumn;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import com.google.gwt.user.client.ui.Widget;
import java.util.ArrayList;
import java.util.List;
import org.jboss.as.console.client.shared.subsys.threads.model.ThreadPool;
import org.jboss.as.console.client.shared.viewframework.AbstractEntityView;
import org.jboss.as.console.client.shared.viewframework.Columns.NameColumn;
import org.jboss.as.console.client.shared.viewframework.EntityToDmrBridge;
import org.jboss.as.console.client.shared.viewframework.EntityToDmrBridgeImpl;
import org.jboss.as.console.client.shared.viewframework.FrameworkView;
import org.jboss.as.console.client.shared.viewframework.NamedEntity;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.ballroom.client.widgets.forms.ComboBoxItem;
import org.jboss.ballroom.client.widgets.forms.FormAdapter;
import org.jboss.ballroom.client.widgets.forms.ObservableFormItem;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;

/**
 * Common superclass for thread pool views.
 *
 * @author ssilvert
 */
public abstract class AbstractThreadPoolView<T extends ThreadPool> extends AbstractEntityView<T> implements FrameworkView {

    protected EntityToDmrBridgeImpl threadPoolBridge;
    private ComboBoxItem threadFactoryComboForEdit;
    
    public AbstractThreadPoolView(Class<?> beanType, ApplicationMetaData propertyMetaData, DispatchAsync dispatcher) {
        super(beanType, propertyMetaData);
        this.threadPoolBridge = new ThreadsEntityToDmrBridge(propertyMetaData, beanType, this, dispatcher);
    }

    @Override
    public Widget createWidget() {
        return super.createEmbeddableWidget();
    }

    @Override
    public EntityToDmrBridge getEntityBridge() {
        return this.threadPoolBridge;
    }

    @Override
    protected abstract String getEntityDisplayName();
    
    @Override
    protected abstract FormAdapter<T> makeAddEntityForm();

    @Override
    public void itemAction(Action action, ObservableFormItem item) {
        if (item.getPropertyBinding().getJavaName().equals("threadFactory") && (action == Action.CREATED)) {
            threadFactoryComboForEdit = (ComboBoxItem) item.getWrapped();
        }
    }

    @Override
    protected DefaultCellTable<T> makeEntityTable() {
        DefaultCellTable<T> table = new DefaultCellTable<T>(4);
        table.addColumn(new NameColumn(), NameColumn.LABEL);
        
        TextColumn<T> maxThreadsColumn = new TextColumn<T>() {
            @Override
            public String getValue(T record) {
                return ((ThreadPool)record).getMaxThreadsCount().toString();
            }
        };
        table.addColumn(maxThreadsColumn, formMetaData.findAttribute("maxThreadsCount").getLabel());
        
        TextColumn<T> maxThreadsPerCPUColumn = new TextColumn<T>() {
            @Override
            public String getValue(T record) {
                return ((ThreadPool)record).getMaxThreadsPerCPU().toString();
            }
        };
        table.addColumn(maxThreadsPerCPUColumn, formMetaData.findAttribute("maxThreadsPerCPU").getLabel());
        
        return table;
    }

    public void setThreadFactoryComboValues(List<NamedEntity> threadFactories) {
        List<String> factoryNames = new ArrayList<String>();
        factoryNames.add(""); // factory not required.  Empty String is a valid choice.
        
        for (NamedEntity factory : threadFactories) {
            factoryNames.add(factory.getName());
        }
        
        this.threadFactoryComboForEdit.setValueMap(factoryNames);
    }
    
}
