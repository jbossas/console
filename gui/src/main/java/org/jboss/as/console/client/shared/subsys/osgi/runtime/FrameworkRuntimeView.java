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

import java.util.EnumSet;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Widget;

import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.subsys.osgi.runtime.model.OSGiFramework;
import org.jboss.as.console.client.shared.viewframework.AbstractEntityView;
import org.jboss.as.console.client.shared.viewframework.EntityDetails;
import org.jboss.as.console.client.shared.viewframework.EntityEditor;
import org.jboss.as.console.client.shared.viewframework.EntityToDmrBridge;
import org.jboss.as.console.client.shared.viewframework.FrameworkButton;
import org.jboss.as.console.client.shared.viewframework.SingleEntityToDmrBridgeImpl;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.ballroom.client.widgets.forms.FormAdapter;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;

/**
 * @author David Bosschaert
 */
public class FrameworkRuntimeView extends AbstractEntityView<OSGiFramework> {
    private SingleEntityToDmrBridgeImpl<OSGiFramework> bridge;

    public FrameworkRuntimeView(ApplicationMetaData propertyMetaData, DispatchAsync dispatcher) {
        super(OSGiFramework.class, propertyMetaData, EnumSet.of(FrameworkButton.ADD, FrameworkButton.REMOVE));
        bridge = new SingleEntityToDmrBridgeImpl<OSGiFramework>(propertyMetaData, OSGiFramework.class, this, dispatcher);
    }

    @Override
    public Widget createWidget() {
        return createEmbeddableWidget();
    }

    @Override
    protected ToolStrip createToolStrip() {
        ToolStrip toolStrip = super.createToolStrip();
        toolStrip.addToolButtonRight(new ToolButton(Console.CONSTANTS.common_label_refresh(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                initialLoad();
            }
        }));
        return toolStrip;
    }

    @Override
    protected EntityEditor<OSGiFramework> makeEntityEditor() {
        EntityDetails<OSGiFramework> details = new EntityDetails<OSGiFramework>(getEntityDisplayName(),
                                                             makeEditEntityDetailsForm(),
                                                             getEntityBridge(),
                                                             getAddress(),
                                                             hideButtons);
        return new EntityEditor<OSGiFramework>(getEntityDisplayName(), null, makeEntityTable(), details, hideButtons);
    }

    @Override
    protected EntityToDmrBridge<OSGiFramework> getEntityBridge() {
        return bridge;
    }

    @Override
    protected DefaultCellTable<OSGiFramework> makeEntityTable() {
        DefaultCellTable<OSGiFramework> table = new DefaultCellTable<OSGiFramework>(5);
        table.setVisible(false); // This table is not visible...
        return table;
    }

    @Override
    protected FormAdapter<OSGiFramework> makeAddEntityForm() {
        return null; // This entity can't be created
    }

    @Override
    protected String getEntityDisplayName() {
        return Console.CONSTANTS.subsys_osgi_framework();
    }

}
