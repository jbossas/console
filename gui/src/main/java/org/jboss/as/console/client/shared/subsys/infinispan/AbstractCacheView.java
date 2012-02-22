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

package org.jboss.as.console.client.shared.subsys.infinispan;

import com.google.gwt.user.cellview.client.TextColumn;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.infinispan.model.LocalCache;
import org.jboss.as.console.client.shared.viewframework.AbstractEntityView;
import org.jboss.as.console.client.shared.viewframework.Columns.NameColumn;
import org.jboss.as.console.client.shared.viewframework.DmrCallback;
import org.jboss.as.console.client.shared.viewframework.EntityToDmrBridge;
import org.jboss.as.console.client.shared.viewframework.FrameworkView;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.ballroom.client.widgets.forms.ComboBoxItem;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.FormAdapter;
import org.jboss.ballroom.client.widgets.forms.ObservableFormItem;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.dmr.client.ModelNode;

import java.util.ArrayList;
import java.util.List;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * Abstract cache view base class for Infinispan caches.
 *
 * @author Stan Silvert
 */
public abstract class AbstractCacheView<T extends LocalCache> extends AbstractEntityView<T> implements FrameworkView {

    protected EntityToDmrBridge bridge;
    protected DispatchAsync dispatcher;

    protected ComboBoxItem cacheContainerForAdd;

    public AbstractCacheView(Class<T> type, ApplicationMetaData propertyMetaData, DispatchAsync dispatcher) {
        super(type, propertyMetaData);
        bridge = new CacheEntityToDmrBridge(propertyMetaData, type, this, dispatcher);
        this.dispatcher = dispatcher;
    }

    @Override
    public EntityToDmrBridge getEntityBridge() {
        return bridge;
    }

    @Override
    public void itemAction(Action action, ObservableFormItem item) {
        if (item.getPropertyBinding().getJavaName().equals("cacheContainer") &&
           (action == Action.CREATED) && (item.getWrapped() instanceof ComboBoxItem)) {
            cacheContainerForAdd = (ComboBoxItem) item.getWrapped();
            cacheContainerForAdd.setDefaultToFirstOption(true);
        }
    }

    @Override
    public void initialLoad() {
        updateCacheContainerList();
        super.initialLoad();
    }

    protected void updateCacheContainerList() {
        if (this.cacheContainerForAdd == null) return;

        ModelNode operation = new ModelNode();
        operation.get(ADDRESS).set(Baseadress.get());
        operation.get(ADDRESS).add("subsystem", "infinispan");
        operation.get(OP).set(READ_CHILDREN_NAMES_OPERATION);
        operation.get(CHILD_TYPE).set("cache-container");

        dispatcher.execute(new DMRAction(operation), new DmrCallback() {
            @Override
            public void onDmrSuccess(ModelNode response) {
                List<String> cacheContainers = new ArrayList<String>();
                for (ModelNode container : response.get(RESULT).asList()) {
                    cacheContainers.add(container.asString());
                }
                AbstractCacheView.this.cacheContainerForAdd.setValueMap(cacheContainers);
            }
        });
    }

    @Override
    protected DefaultCellTable<T> makeEntityTable() {
        DefaultCellTable<T> table = new DefaultCellTable<T>(4);

        table.addColumn(new NameColumn(), NameColumn.LABEL);

        TextColumn<T> cacheContainerColumn = new TextColumn<T>() {
            @Override
            public String getValue(T record) {
                return record.getCacheContainer();
            }
        };
        table.addColumn(cacheContainerColumn, Console.CONSTANTS.subsys_infinispan_cache_container());

        return table;
    }

}
