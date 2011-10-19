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
import javax.inject.Inject;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.subsys.deploymentscanner.model.DeploymentScanner;
import org.jboss.as.console.client.shared.subsys.infinispan.model.CacheContainer;
import org.jboss.as.console.client.shared.viewframework.AbstractEntityView;
import org.jboss.as.console.client.shared.viewframework.Columns.NameColumn;
import org.jboss.as.console.client.shared.viewframework.EntityToDmrBridgeImpl;
import org.jboss.as.console.client.shared.viewframework.EntityToDmrBridge;
import org.jboss.as.console.client.widgets.forms.PropertyMetaData;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.FormAdapter;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;

/**
 * Main view class for Infinispan Cache Containers.
 * 
 * @author Stan Silvert
 */
public class CacheContainerView extends AbstractEntityView<CacheContainer> implements CacheContainerPresenter.MyView {

    private EntityToDmrBridge bridge;
    
    @Inject
    public CacheContainerView(PropertyMetaData propertyMetaData, DispatchAsync dispatcher) {
        super(CacheContainer.class, propertyMetaData);
        bridge = new EntityToDmrBridgeImpl(propertyMetaData, CacheContainer.class, this, dispatcher);
    }
    
    @Override
    protected EntityToDmrBridge getEntityBridge() {
        return bridge;
    }

    @Override
    protected String getEntityDisplayName() {
        return Console.CONSTANTS.subsys_infinispan_cache_containers();
    }

    @Override
    protected FormAdapter<CacheContainer> makeAddEntityForm() {
        Form<CacheContainer> form = new Form(DeploymentScanner.class);
        form.setNumColumns(1);
        form.setFields(getFormMetaData().findAttribute("name").getFormItemForAdd());
        return form;
    }

    @Override
    protected DefaultCellTable<CacheContainer> makeEntityTable() {
        DefaultCellTable<CacheContainer> table = new DefaultCellTable<CacheContainer>(4);
        
        table.addColumn(new NameColumn(), NameColumn.LABEL);
        
        TextColumn<CacheContainer> defaultCacheColumn = new TextColumn<CacheContainer>() {
            @Override
            public String getValue(CacheContainer record) {
                return record.getDefaultCache();
            }
        };
        
        table.addColumn(defaultCacheColumn, Console.CONSTANTS.subsys_infinispan_default_cache());
        
        return table;
    }
    
}
