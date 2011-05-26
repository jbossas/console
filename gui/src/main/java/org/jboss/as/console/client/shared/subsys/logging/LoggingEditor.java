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
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import java.util.ArrayList;
import java.util.Iterator;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.subsys.logging.model.LoggerConfig;
import org.jboss.as.console.client.shared.subsys.logging.model.LoggingHandler;
import org.jboss.as.console.client.widgets.ContentHeaderLabel;
import org.jboss.as.console.client.widgets.icons.Icons;
import org.jboss.as.console.client.widgets.tables.DefaultCellTable;

import java.util.List;

/**
 * @author Stan Silvert
 * @date 3/29/11
 */
public class LoggingEditor {

    private LoggingPresenter presenter;
    private DefaultCellTable<LoggingHandler> handlerTable;
    private ListDataProvider<LoggingHandler> handlerProvider;
    
    private ListDataProvider<LoggerConfig> loggerProvider;

    public LoggingEditor(LoggingPresenter presenter) {
        this.presenter = presenter;
    }

    public Widget asWidget() {

        ScrollPanel scroll = new ScrollPanel();

        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("fill-layout-width");
        
        scroll.add(layout);

        /*
        ToolStrip toolstrip = new ToolStrip();
        toolstrip.addToolButton(new ToolButton("Add", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                Console.MODULES.getMessageCenter().notify(
                        new Message("Adding logging handlers not implemented",Message.Severity.Warning)
                );
            }
        }));

        layout.add(toolstrip); */

        // ---

        layout.add(new ContentHeaderLabel(Console.CONSTANTS.subsys_logging_handlerConfigurations()));

        handlerTable = new DefaultCellTable<LoggingHandler>(20);
        handlerProvider = new ListDataProvider<LoggingHandler>();
        handlerProvider.addDataDisplay(handlerTable);

        TextColumn<LoggingHandler> nameColumn = new TextColumn<LoggingHandler>() {
            @Override
            public String getValue(LoggingHandler record) {
                return record.getName();
            }
        };

        TextColumn<LoggingHandler> autoflushColumn = new TextColumn<LoggingHandler>() {
            @Override
            public String getValue(LoggingHandler record) {
                return Boolean.toString(record.isAutoflush());
            }
        };
        
        TextColumn<LoggingHandler> encodingColumn = new TextColumn<LoggingHandler>() {
            @Override
            public String getValue(LoggingHandler record) {
                return record.getEncoding();
            }
        };
        
        TextColumn<LoggingHandler> formatterColumn = new TextColumn<LoggingHandler>() {
            @Override
            public String getValue(LoggingHandler record) {
                return record.getFormatter();
            }
        };
        
        TextColumn<LoggingHandler> handlerTypeColumn = new TextColumn<LoggingHandler>() {
            @Override
            public String getValue(LoggingHandler record) {
                return record.getType();
            }
        };
        
        TextColumn<LoggingHandler> levelColumn = new TextColumn<LoggingHandler>() {
            @Override
            public String getValue(LoggingHandler record) {
                return record.getLevel();
            }
        };
        
        TextColumn<LoggingHandler> queueLengthColumn = new TextColumn<LoggingHandler>() {
            @Override
            public String getValue(LoggingHandler record) {
                return record.getQueueLength();
            }
        };

        handlerTable.addColumn(nameColumn, Console.CONSTANTS.common_label_name());
        handlerTable.addColumn(handlerTypeColumn, Console.CONSTANTS.subsys_logging_type());
        handlerTable.addColumn(levelColumn, Console.CONSTANTS.subsys_logging_logLevel());
        handlerTable.addColumn(autoflushColumn, Console.CONSTANTS.subsys_logging_autoFlush());
        handlerTable.addColumn(encodingColumn, Console.CONSTANTS.subsys_logging_encoding());
        handlerTable.addColumn(formatterColumn, Console.CONSTANTS.subsys_logging_formatter());
        handlerTable.addColumn(queueLengthColumn, Console.CONSTANTS.subsys_logging_queueLength());

        layout.add(handlerTable);

        layout.add(new ContentHeaderLabel("Loggers")); // localize me!!!!
        layout.add(makeLoggerConfigTable());
        
        return scroll;
    }
    
    private DefaultCellTable<LoggerConfig> makeLoggerConfigTable() {
        DefaultCellTable<LoggerConfig> loggerTable = new DefaultCellTable<LoggerConfig>(20);
        loggerProvider = new ListDataProvider<LoggerConfig>();
        loggerProvider.addDataDisplay(loggerTable);
        
        TextColumn<LoggerConfig> nameColumn = new TextColumn<LoggerConfig>() {
            @Override
            public String getValue(LoggerConfig record) {
                return record.getName();
            }
        };
        
        TextColumn<LoggerConfig> levelColumn = new TextColumn<LoggerConfig>() {
            @Override
            public String getValue(LoggerConfig record) {
                return record.getLevel();
            }
        };
        
        TextColumn<LoggerConfig> handlersColumn = new TextColumn<LoggerConfig>() {
            @Override
            public String getValue(LoggerConfig record) {
                List<String> handlers = record.getHandlers();
                StringBuilder builder = new StringBuilder();
                for (Iterator<String> i = handlers.iterator(); i.hasNext();) {
                    builder.append(i.next());
                    if (i.hasNext()) builder.append(", ");
                }
                return builder.toString();
            }
        };
        
        loggerTable.addColumn(nameColumn, Console.CONSTANTS.common_label_name());
        loggerTable.addColumn(levelColumn, Console.CONSTANTS.subsys_logging_logLevel());
        loggerTable.addColumn(handlersColumn, "Handlers"); /// localize me!!!
        
        return loggerTable;
    }

    public void updateLoggingHandlers(LoggingInfo loggingInfo) {
        handlerProvider.setList(loggingInfo.getHandlers());
        
        List<LoggerConfig> loggers = new ArrayList();
        loggers.add(loggingInfo.getRootLogger());
        loggers.addAll(loggingInfo.getLoggers());
        loggerProvider.setList(loggers); 

        // FIXME - NPE - handlerTable null on first display?
       //  if(!handlers.isEmpty())
       //     handlerTable.getSelectionModel().setSelected(handlers.get(0), true);

    }
}
