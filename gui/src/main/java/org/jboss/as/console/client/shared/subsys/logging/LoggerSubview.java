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
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.subsys.logging.LoggingLevelProducer.LogLevelConsumer;
import org.jboss.as.console.client.shared.subsys.logging.model.Logger;
import org.jboss.as.console.client.shared.viewframework.Columns.NameColumn;
import org.jboss.as.console.client.shared.viewframework.EntityToDmrBridge;
import org.jboss.as.console.client.shared.viewframework.EntityToDmrBridgeImpl;
import org.jboss.as.console.client.shared.viewframework.FrameworkPresenter;
import org.jboss.as.console.client.shared.viewframework.FrameworkView;
import org.jboss.as.console.client.shared.viewframework.SingleEntityView;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.as.console.client.widgets.forms.FormMetaData;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.FormAdapter;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;

import java.util.ArrayList;
import java.util.List;

/**
 * Main view class for Loggers.
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2011 Red Hat Inc.
 */
public class LoggerSubview extends AbstractLoggingSubview<Logger>
        implements FrameworkView, LogLevelConsumer, HandlerConsumer {

    private EntityToDmrBridge loggerBridge;

    private EmbeddedHandlerView handlerView;

    public LoggerSubview(ApplicationMetaData applicationMetaData, DispatchAsync dispatcher) {
        super(Logger.class, applicationMetaData);
        loggerBridge = new EntityToDmrBridgeImpl<Logger>(applicationMetaData, Logger.class, this, dispatcher);
    }

    @Override
    public void handlersUpdated(List<String> handlerList) {
        this.handlerView.getListView().setAvailableChoices(handlerList);
    }

    @Override
    public EntityToDmrBridge getEntityBridge() {
        return this.loggerBridge;
    }

    @Override
    protected String getEntityDisplayName() {
        return Console.CONSTANTS.subsys_logging_loggers();
    }

    @Override
    protected FormAdapter<Logger> makeAddEntityForm() {
        Form<Logger> form = new Form(Logger.class);
        form.setNumColumns(1);
        form.setFields(formMetaData.findAttribute("name").getFormItemForAdd(),
                levelItemForAdd,
                formMetaData.findAttribute("useParentHandlers").getFormItemForAdd());
        return form;
    }

    @Override
    protected DefaultCellTable<Logger> makeEntityTable() {
        DefaultCellTable<Logger> table = new DefaultCellTable<Logger>(4);

        table.addColumn(new NameColumn(), NameColumn.LABEL);

        TextColumn<Logger> levelColumn = new TextColumn<Logger>() {
            @Override
            public String getValue(Logger record) {
                return record.getLevel();
            }
        };
        table.addColumn(levelColumn, Console.CONSTANTS.subsys_logging_logLevel());

        return table;
    }

    @Override
    protected List<SingleEntityView<Logger>> provideAdditionalTabs(
            Class<?> beanType,
            FormMetaData formMetaData,
            FrameworkPresenter presenter) {

        List<SingleEntityView<Logger>> additionalTabs =
                new ArrayList<SingleEntityView<Logger>>();

        this.handlerView = new EmbeddedHandlerView(new FrameworkPresenter() {
            @Override
            public EntityToDmrBridge getEntityBridge() {
                return LoggerSubview.this.getEntityBridge();
            }
        });
        additionalTabs.add(handlerView);

        return additionalTabs;
    }

}
