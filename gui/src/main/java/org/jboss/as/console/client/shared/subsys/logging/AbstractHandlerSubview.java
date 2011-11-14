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
package org.jboss.as.console.client.shared.subsys.logging;
import com.google.gwt.user.cellview.client.TextColumn;
import java.util.List;

import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.subsys.logging.model.HasLevel;
import org.jboss.as.console.client.shared.subsys.logging.LoggingLevelProducer.LogLevelConsumer;
import org.jboss.as.console.client.shared.viewframework.Columns.NameColumn;
import org.jboss.as.console.client.shared.viewframework.EntityToDmrBridgeImpl;
import org.jboss.as.console.client.shared.viewframework.FrameworkView;
import org.jboss.as.console.client.shared.viewframework.EntityToDmrBridge;
import org.jboss.as.console.client.shared.viewframework.NamedEntity;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.FormAdapter;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;

/**
 * Main view class for Loggers.
 * 
 * @author Stan Silvert
 */
public abstract class AbstractHandlerSubview<T extends NamedEntity> extends AbstractLoggingSubview implements FrameworkView, LogLevelConsumer, HandlerProducer {

    private EntityToDmrBridge<T> loggerBridge;
    private HandlerListManager handlerListManager;
    protected Class<T> type;
    
    public AbstractHandlerSubview(Class<T> type,
                                 ApplicationMetaData applicationMetaData, 
                                 DispatchAsync dispatcher, 
                                 HandlerListManager handlerListManager) {
        super(type, applicationMetaData);
        this.type = type;
        loggerBridge = new EntityToDmrBridgeImpl(applicationMetaData, type, this, dispatcher);
        this.handlerListManager = handlerListManager;
    }

    @Override
    public List<NamedEntity> getHandlers() {
        return (List<NamedEntity>)getEntityBridge().getEntityList();
    }
    
    @Override
    protected EntityToDmrBridge getEntityBridge() {
        return this.loggerBridge;
    }

    @Override
    protected FormAdapter<T> makeAddEntityForm() {
        Form<T> form = new Form(type);
        form.setNumColumns(1);
        form.setFields(formMetaData.findAttribute("name").getFormItemForAdd(), 
                       levelItemForAdd);
        return form;
    }

    @Override
    protected DefaultCellTable<T> makeEntityTable() {
        DefaultCellTable<T> table = new DefaultCellTable<T>(4);

        table.addColumn(new NameColumn(), NameColumn.LABEL);

        TextColumn<HasLevel> levelColumn = new TextColumn<HasLevel>() {
            @Override
            public String getValue(HasLevel record) {
                return record.getLevel();
            }
        };
        table.addColumn(levelColumn, Console.CONSTANTS.subsys_logging_logLevel());
        
        return table;
    }

    @Override
    public void refresh() {
        super.refresh();
        this.handlerListManager.handlerListUpdated();
    }

}
