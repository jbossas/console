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

import javax.inject.Inject;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.subsys.threads.model.BoundedQueueThreadPool;
import org.jboss.as.console.client.shared.viewframework.AbstractEntityView;
import org.jboss.as.console.client.shared.viewframework.Columns.NameColumn;
import org.jboss.as.console.client.shared.viewframework.EntityToDmrBridgeImpl;
import org.jboss.as.console.client.widgets.forms.FormMetaData;
import org.jboss.as.console.client.shared.viewframework.EntityToDmrBridge;
import org.jboss.as.console.client.widgets.forms.PropertyMetaData;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.FormAdapter;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;

/**
 * Main view class for Deployment Scanners.  This class assembles the editor and reacts to 
 * FrameworkView callbacks.
 * 
 * @author Stan Silvert
 */
public class BoundedQueueThreadPoolView extends AbstractEntityView<BoundedQueueThreadPool> implements BoundedQueueThreadPoolPresenter.MyView {

    private EntityToDmrBridge threadPoolBridge;
    private FormMetaData formMetaData;

    @Inject
    public BoundedQueueThreadPoolView(PropertyMetaData propertyMetaData, DispatchAsync dispatcher) {
        super(BoundedQueueThreadPool.class);
        formMetaData = propertyMetaData.getBeanMetaData(BoundedQueueThreadPool.class).getFormMetaData();
        threadPoolBridge = new EntityToDmrBridgeImpl<BoundedQueueThreadPool>(propertyMetaData, BoundedQueueThreadPool.class, this, dispatcher);
    }
    
    @Override
    protected FormMetaData getFormMetaData() {
        return this.formMetaData;
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
        form.setFields(formMetaData.findAttribute("name").getFormItemForAdd());
        return form;
    }

    @Override
    protected DefaultCellTable<BoundedQueueThreadPool> makeEntityTable() {
        DefaultCellTable<BoundedQueueThreadPool> table = new DefaultCellTable<BoundedQueueThreadPool>(4);
        
        table.addColumn(new NameColumn(), NameColumn.LABEL);
        
        return table;
    }
  
}
