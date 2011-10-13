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
import org.jboss.as.console.client.shared.subsys.ejb3.model.StrictMaxBeanPool;
import org.jboss.as.console.client.shared.subsys.ejb3.model.TimerService;
import org.jboss.as.console.client.shared.viewframework.AbstractEntityView;
import org.jboss.as.console.client.shared.viewframework.EntityDetails;
import org.jboss.as.console.client.shared.viewframework.EntityEditor;
import org.jboss.as.console.client.shared.viewframework.EntityToDmrBridge;
import org.jboss.as.console.client.shared.viewframework.FrameworkButton;
import org.jboss.as.console.client.widgets.forms.PropertyMetaData;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.FormAdapter;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;

/**
 * @author David Bosschaert
 */
public class TimerServiceView extends AbstractEntityView<TimerService> {
    private final EntityToDmrBridge<TimerService> bridge;

    public TimerServiceView(PropertyMetaData propertyMetaData, DispatchAsync dispatcher) {
        super(TimerService.class, propertyMetaData, EnumSet.of(FrameworkButton.ADD));
        bridge = new SingleEntityToDmrBridgeImpl<TimerService>(propertyMetaData, TimerService.class, this, dispatcher);
    }

    @Override
    public Widget createWidget() {
        entityEditor = makeEntityEditor();
        return entityEditor.asWidget();
    }

    @Override
    protected EntityEditor<TimerService> makeEntityEditor() {
        EntityDetails<TimerService> details = new EntityDetails<TimerService>(getPluralEntityName(), 
                                                                              makeEditEntityDetailsForm(), 
                                                                              getEntityBridge(), 
                                                                              getAddress(),
                                                                              hideButtons);
        return new EntityEditor<TimerService>(getPluralEntityName(), null, makeEntityTable(), details, hideButtons);
    }

    @Override
    protected EntityToDmrBridge<TimerService> getEntityBridge() {
        return bridge;
    }

    @Override
    protected DefaultCellTable<TimerService> makeEntityTable() {
        DefaultCellTable<TimerService> table = new DefaultCellTable<TimerService>(5);
        table.setVisible(false);
        return table;
    }

    @Override
    protected FormAdapter<TimerService> makeAddEntityForm() {
        // TODO delete this!
        Form<TimerService> form = new Form<TimerService>(StrictMaxBeanPool.class);
        form.setNumColumns(1);
        form.setFields(getFormMetaData().findAttribute("path").getFormItemForAdd());
        return form;
    }

    @Override
    protected String getPluralEntityName() {
        return "Timer Service"; // needed?
    }
}
