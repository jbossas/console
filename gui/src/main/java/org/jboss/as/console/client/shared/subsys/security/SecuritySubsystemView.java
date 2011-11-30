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
package org.jboss.as.console.client.shared.subsys.security;

import java.util.EnumSet;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.subsys.security.model.SecuritySubsystem;
import org.jboss.as.console.client.shared.viewframework.AbstractEntityView;
import org.jboss.as.console.client.shared.viewframework.EntityDetails;
import org.jboss.as.console.client.shared.viewframework.EntityEditor;
import org.jboss.as.console.client.shared.viewframework.EntityToDmrBridge;
import org.jboss.as.console.client.shared.viewframework.FrameworkButton;
import org.jboss.as.console.client.shared.viewframework.FrameworkPresenter;
import org.jboss.as.console.client.shared.viewframework.SingleEntityToDmrBridgeImpl;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.ballroom.client.widgets.forms.FormAdapter;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;

/**
 * @author David Bosschaert
 */
public class SecuritySubsystemView extends AbstractEntityView<SecuritySubsystem>
        implements SecuritySubsystemPresenter.MyView, FrameworkPresenter {
    private final SingleEntityToDmrBridgeImpl<SecuritySubsystem> bridge;

    @Inject
    public SecuritySubsystemView(ApplicationMetaData propertyMetaData, DispatchAsync dispatcher) {
        super(SecuritySubsystem.class, propertyMetaData, EnumSet.of(FrameworkButton.ADD, FrameworkButton.REMOVE));

        bridge = new SingleEntityToDmrBridgeImpl<SecuritySubsystem>(
                propertyMetaData,
                SecuritySubsystem.class,
                this,
                dispatcher
        );
    }

    @Override
    public Widget createWidget() {
        TabLayoutPanel tabLayoutpanel = new TabLayoutPanel(25, Style.Unit.PX);
        tabLayoutpanel.addStyleName("default-tabpanel");

        tabLayoutpanel.add(createEmbeddableWidget(), getEntityDisplayName());
        tabLayoutpanel.selectTab(0);

        return tabLayoutpanel;
    }

    @Override
    protected EntityEditor<SecuritySubsystem> makeEntityEditor() {
        EntityDetails<SecuritySubsystem> details = new EntityDetails<SecuritySubsystem>(
                this, getEntityDisplayName(),
                makeEditEntityDetailsForm(),
                getAddress(),
                hideButtons);
        return new EntityEditor<SecuritySubsystem>(this, getEntityDisplayName(), null, makeEntityTable(), details, hideButtons);
    }
    @Override
    public EntityToDmrBridge<SecuritySubsystem> getEntityBridge() {
        return bridge;
    }

    @Override
    protected DefaultCellTable<SecuritySubsystem> makeEntityTable() {
        DefaultCellTable<SecuritySubsystem> table = new DefaultCellTable<SecuritySubsystem>(5);
        table.setVisible(false); // Hide this table
        return table;
    }

    @Override
    protected FormAdapter<SecuritySubsystem> makeAddEntityForm() {
        return null; // This entity can't be created
    }

    @Override
    protected String getEntityDisplayName() {
        return Console.CONSTANTS.subsys_security();
    }
}
