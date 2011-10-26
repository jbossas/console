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

import java.util.EnumSet;

import com.google.gwt.user.client.ui.Widget;

import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.subsys.ejb3.model.ThreadPool;
import org.jboss.as.console.client.shared.viewframework.AbstractEntityView;
import org.jboss.as.console.client.shared.viewframework.Columns;
import org.jboss.as.console.client.shared.viewframework.EntityToDmrBridge;
import org.jboss.as.console.client.shared.viewframework.EntityToDmrBridgeImpl;
import org.jboss.as.console.client.shared.viewframework.FrameworkButton;
import org.jboss.as.console.client.widgets.forms.PropertyMetaData;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.FormAdapter;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.dmr.client.ModelNode;

/**
 * @author David Bosschaert
 */
public class ThreadPoolsView extends AbstractEntityView<ThreadPool>{
    private final EntityToDmrBridgeImpl<ThreadPool> bridge;
    private EJB3Presenter presenter;

    public ThreadPoolsView(PropertyMetaData propertyMetaData, DispatchAsync dispatcher) {
        super(ThreadPool.class, propertyMetaData, EnumSet.of(FrameworkButton.EDIT_SAVE));
        bridge = new EntityToDmrBridgeImpl<ThreadPool>(propertyMetaData, ThreadPool.class, this, dispatcher) {
            @Override
            protected void onLoadEntitiesSuccess(ModelNode response) {
                super.onLoadEntitiesSuccess(response);
                presenter.propagateThreadPoolNames(entityList);
            }
        };
    }

    @Override
    public Widget createWidget() {
        return createEmbeddableWidget();
    }

    @Override
    protected EntityToDmrBridge<ThreadPool> getEntityBridge() {
        return bridge;
    }

    @Override
    protected DefaultCellTable<ThreadPool> makeEntityTable() {
        DefaultCellTable<ThreadPool> table = new DefaultCellTable<ThreadPool>(10);
        table.addColumn(new Columns.NameColumn(), Columns.NameColumn.LABEL);
        return table;
    }

    @Override
    protected FormAdapter<ThreadPool> makeAddEntityForm() {
        Form<ThreadPool> form = new Form<ThreadPool>(ThreadPool.class);
        form.setNumColumns(1);
        form.setFields(formMetaData.findAttribute("name").getFormItemForAdd(),
                formMetaData.findAttribute("maxThreads").getFormItemForAdd(),
                formMetaData.findAttribute("keepAliveTime").getFormItemForAdd());
        return form;
    }

    @Override
    protected String getEntityDisplayName() {
        return "Thread Pools";
    }

    public void setPresenter(EJB3Presenter presenter) {
        this.presenter = presenter;
    }
}
