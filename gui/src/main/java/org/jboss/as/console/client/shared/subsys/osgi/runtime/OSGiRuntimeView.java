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
package org.jboss.as.console.client.shared.subsys.osgi.runtime;

import com.google.gwt.user.cellview.client.TextColumn;
import com.google.inject.Inject;

import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.subsys.osgi.runtime.model.Bundle;
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
public class OSGiRuntimeView extends AbstractEntityView<Bundle> implements OSGiRuntimePresenter.MyView {
    private final FormMetaData formMetaData;
    private final EntityToDmrBridgeImpl<Bundle> bridge;

    @Inject
    public OSGiRuntimeView(PropertyMetaData propertyMetaData, DispatchAsync dispatcher) {
        super(Bundle.class);
        formMetaData = propertyMetaData.getBeanMetaData(Bundle.class).getFormMetaData();
        bridge = new EntityToDmrBridgeImpl<Bundle>(propertyMetaData, Bundle.class, this, dispatcher);
    }

    @Override
    protected FormMetaData getFormMetaData() {
        return formMetaData;
    }

    @Override
    protected EntityToDmrBridge<Bundle> getEntityBridge() {
        return bridge;
    }

    @Override
    protected DefaultCellTable<Bundle> makeEntityTable() {
        DefaultCellTable<Bundle> table = new DefaultCellTable<Bundle>(15);

        table.addColumn(new Columns.NameColumn(), "Bundle ID");
        TextColumn<Bundle> symbolicNameColumn = new TextColumn<Bundle>() {
            @Override
            public String getValue(Bundle record) {
                return record.getSymbolicName();
            }
        };
        table.addColumn(symbolicNameColumn, "Symbolic Name");

        TextColumn<Bundle> versionColumn = new TextColumn<Bundle>() {
            @Override
            public String getValue(Bundle record) {
                return record.getVersion();
            }
        };
        table.addColumn(versionColumn, "Version");

        return table;
    }

    @Override
    // TODO remove!
    protected FormAdapter<Bundle> makeAddEntityForm() {
        Form<Bundle> form = new Form<Bundle>(Bundle.class);
        form.setNumColumns(1);
        form.setFields(formMetaData.findAttribute("name").getFormItemForAdd());
        return form;
        // Cannot add a Bundle here
        // return null;
    }

    @Override
    protected String getPluralEntityName() {
        return "Bundles";
    }
}
