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
package org.jboss.as.console.client.shared.subsys.logging.refactored;
import com.google.gwt.user.cellview.client.TextColumn;
import org.jboss.as.console.client.shared.subsys.logging.model.ConsoleHandler;

import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.subsys.logging.refactored.LoggingLevelProducer.LogLevelConsumer;
import org.jboss.as.console.client.shared.viewframework.Columns.NameColumn;
import org.jboss.as.console.client.shared.viewframework.EntityToDmrBridgeImpl;
import org.jboss.as.console.client.shared.viewframework.FrameworkView;
import org.jboss.as.console.client.shared.viewframework.EntityToDmrBridge;
import org.jboss.as.console.client.widgets.forms.PropertyMetaData;
import org.jboss.ballroom.client.widgets.forms.ComboBoxItem;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.FormAdapter;
import org.jboss.ballroom.client.widgets.forms.ObservableFormItem;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;

/**
 * Main view class for Loggers.
 * 
 * @author Stan Silvert
 */
public class ConsoleHandlerSubview extends AbstractLoggingSubview<ConsoleHandler> implements FrameworkView, LogLevelConsumer {

    private EntityToDmrBridge loggerBridge;
    
    public ConsoleHandlerSubview(PropertyMetaData propertyMetaData, DispatchAsync dispatcher) {
        super(ConsoleHandler.class, propertyMetaData);
        loggerBridge = new EntityToDmrBridgeImpl<ConsoleHandler>(propertyMetaData, ConsoleHandler.class, this, dispatcher);
    }

    @Override
    public void itemAction(Action action, ObservableFormItem item) {
        super.itemAction(action, item);
        if (item.getPropertyBinding().getJavaName().equals("target") && (action == Action.CREATED)) {
            ComboBoxItem targetItem = (ComboBoxItem) item.getWrapped();
            targetItem.setValueMap(new String[] {"System.out", "System.err"});
        }
    }
    
    @Override
    protected EntityToDmrBridge getEntityBridge() {
        return this.loggerBridge;
    }

    @Override
    protected String getEntityDisplayName() {
        return Console.CONSTANTS.subsys_logging_consoleHandlers();
    }

    @Override
    protected FormAdapter<ConsoleHandler> makeAddEntityForm() {
        Form<ConsoleHandler> form = new Form(ConsoleHandler.class);
        form.setNumColumns(1);
        form.setFields(formMetaData.findAttribute("name").getFormItemForAdd(), 
                       levelItemForAdd);
        return form;
    }

    @Override
    protected DefaultCellTable<ConsoleHandler> makeEntityTable() {
        DefaultCellTable<ConsoleHandler> table = new DefaultCellTable<ConsoleHandler>(4);

        table.addColumn(new NameColumn(), NameColumn.LABEL);

        TextColumn<ConsoleHandler> levelColumn = new TextColumn<ConsoleHandler>() {
            @Override
            public String getValue(ConsoleHandler record) {
                return record.getLevel();
            }
        };
        table.addColumn(levelColumn, Console.CONSTANTS.subsys_logging_logLevel());
        
        return table;
    }

}
