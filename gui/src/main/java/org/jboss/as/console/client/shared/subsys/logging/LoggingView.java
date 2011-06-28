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

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.DisposableViewImpl;
import org.jboss.as.console.client.shared.subsys.logging.model.LoggerConfig;
import org.jboss.as.console.client.shared.subsys.logging.model.LoggingHandler;
import org.jboss.as.console.client.widgets.forms.ComboBoxItem;
import org.jboss.as.console.client.widgets.forms.Form;
import org.jboss.as.console.client.widgets.forms.ListItem;
import org.jboss.as.console.client.widgets.forms.StatusItem;
import org.jboss.as.console.client.widgets.forms.TextItem;
import org.jboss.as.console.client.widgets.tables.DefaultCellTable;


/**
 * @author Stan Silvert
 * @date 3/29/11
 */
public class LoggingView extends DisposableViewImpl implements LoggingPresenter.MyView {

    private LoggingCmdAdapter loggerConfigCmdAdapger;
    private LoggingCmdAdapter handlerCmdAdapter;
    
    private LoggingEditor<LoggerConfig> loggerConfigEditor;
    private LoggingDetails<LoggerConfig> loggerConfigDetails;
    private LoggingEntityFormFactory<LoggerConfig> loggerFormFactory;
    
    private LoggingEditor<LoggingHandler> handlerEditor;
    private LoggingDetails<LoggingHandler> handlerDetails;
    private LoggingEntityFormFactory<LoggingHandler> handlerFormFactory;

    @Override
    public void setPresenter(LoggingPresenter presenter) {
        this.loggerConfigCmdAdapger = new LoggerConfigCmdAdapter(presenter);
        this.loggerFormFactory = new LoggerConfigFormFactory(presenter.getBeanFactory(), LoggerConfig.class);
        
        this.handlerCmdAdapter = new HandlerCmdAdapter(presenter);
        this.handlerFormFactory = new HandlerFormFactory(presenter.getBeanFactory(), LoggingHandler.class);
    }
    
    @Override
    public Widget createWidget() {
        loggerConfigEditor = makeLoggingEditor();
        handlerEditor = makeHandlerEditor();
        
        TabLayoutPanel tabLayoutpanel = new TabLayoutPanel(25, Style.Unit.PX);
        tabLayoutpanel.addStyleName("default-tabpanel");
        
        tabLayoutpanel.add(loggerConfigEditor.asWidget(), Console.CONSTANTS.subsys_logging_loggers());
        tabLayoutpanel.add(handlerEditor.asWidget(), Console.CONSTANTS.subsys_logging_handlers());

        return tabLayoutpanel;
    }
    
    private LoggingEditor<LoggingHandler> makeHandlerEditor() {
        handlerDetails = new LoggingDetails<LoggingHandler>(Console.CONSTANTS.subsys_logging_handlers(), 
                                                            handlerFormFactory.makeEditForm(), 
                                                            this.handlerCmdAdapter);
        String title = Console.CONSTANTS.common_label_add() + " " + Console.CONSTANTS.subsys_logging_handlers();
        AddLoggingEntityWindow<LoggingHandler> window = new AddLoggingEntityWindow<LoggingHandler>(title, handlerFormFactory.makeAddForm(), this.handlerCmdAdapter);
        DefaultCellTable<LoggingHandler> table = makeHandlerTable();
        return new LoggingEditor<LoggingHandler>(Console.CONSTANTS.subsys_logging_handlers(), window, table, handlerDetails);
    }
    
    private Form<LoggingHandler> makeHandlerForm() {
        TextItem nameItem = new TextItem("name", Console.CONSTANTS.common_label_name());
        TextItem typeItem = new TextItem("type", Console.CONSTANTS.subsys_logging_type());

        ComboBoxItem logLevelItem = new ComboBoxItem("level", Console.CONSTANTS.subsys_logging_logLevel());
        logLevelItem.setValueMap(LogLevel.STRINGS);
        
        StatusItem flushItem = new StatusItem("autoflush", Console.CONSTANTS.subsys_logging_autoFlush());

        TextItem formatterItem = new TextItem("formatter", Console.CONSTANTS.subsys_logging_formatter());
        TextItem encodingItem = new TextItem("encoding", Console.CONSTANTS.subsys_logging_encoding());
        TextItem queueItem = new TextItem("queueLength", Console.CONSTANTS.subsys_logging_queueLength());

        Form<LoggingHandler> form = new Form(LoggingHandler.class);
        form.setNumColumns(2);
        form.setFields(nameItem, typeItem, logLevelItem, flushItem, formatterItem, encodingItem, queueItem);
        return form;
    }
    
    private DefaultCellTable<LoggingHandler> makeHandlerTable() {
        DefaultCellTable<LoggingHandler> handlerTable = new DefaultCellTable<LoggingHandler>(15);

        TextColumn<LoggingHandler> nameColumn = new TextColumn<LoggingHandler>() {
            @Override
            public String getValue(LoggingHandler record) {
                return record.getName();
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

        handlerTable.addColumn(nameColumn, Console.CONSTANTS.common_label_name());
        handlerTable.addColumn(handlerTypeColumn, Console.CONSTANTS.subsys_logging_type());
        handlerTable.addColumn(levelColumn, Console.CONSTANTS.subsys_logging_logLevel());
        
        return handlerTable;
    }
    
    private LoggingEditor<LoggerConfig> makeLoggingEditor() {
        loggerConfigDetails = new LoggingDetails<LoggerConfig>(Console.CONSTANTS.subsys_logging_loggers(), 
                                                               loggerFormFactory.makeEditForm(), 
                                                               loggerConfigCmdAdapger);
        String title = Console.CONSTANTS.common_label_add() + " " + Console.CONSTANTS.subsys_logging_loggers();
        AddLoggingEntityWindow<LoggerConfig> window = new AddLoggingEntityWindow<LoggerConfig>(title, 
                                                                                               loggerFormFactory.makeAddForm(), 
                                                                                               loggerConfigCmdAdapger);
        DefaultCellTable<LoggerConfig> table = makeLoggerConfigTable();
        return new LoggingEditor<LoggerConfig>(Console.CONSTANTS.subsys_logging_loggers(), window, table, loggerConfigDetails);
    }
    
    private Form<LoggerConfig> makeEditLoggerConfigForm() {
        TextItem nameItem = new TextItem("name", Console.CONSTANTS.common_label_name());

        ComboBoxItem logLevelItem = new ComboBoxItem("level", Console.CONSTANTS.subsys_logging_logLevel());
        logLevelItem.setValueMap(LogLevel.STRINGS);

        ListItem handlersItem = new ListItem("handlers", Console.CONSTANTS.subsys_logging_handlers(), true);

        Form<LoggerConfig> form = new Form(LoggerConfig.class);
        form.setNumColumns(1);
        form.setFields(nameItem, logLevelItem, handlersItem);
        return form;
    }
    
    private DefaultCellTable<LoggerConfig> makeLoggerConfigTable() {
        DefaultCellTable<LoggerConfig> loggerConfigTable = new DefaultCellTable<LoggerConfig>(15);
        
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
        
        loggerConfigTable.addColumn(nameColumn, Console.CONSTANTS.common_label_name());
        loggerConfigTable.addColumn(levelColumn, Console.CONSTANTS.subsys_logging_logLevel());
        loggerConfigTable.addColumn(handlersColumn, Console.CONSTANTS.subsys_logging_handlers());
        
        return loggerConfigTable;
    }

    @Override
    public void updateLoggingInfo(LoggingInfo loggingInfo) {
        List<LoggerConfig> loggers = new ArrayList();
        loggers.add(loggingInfo.getRootLogger()); // root logger is always first in list
        loggers.addAll(loggingInfo.getLoggers());
        LoggerConfig lastLoggerConfigEdited = null;
        if (loggerConfigDetails.getEditedEntity() != null) {
            // Instance inside details now different from instance in list.  Look up by name.
            lastLoggerConfigEdited = loggingInfo.findLoggerConfig(loggerConfigDetails.getEditedEntity().getName());
        }
        loggerConfigEditor.updateEntityList(loggers, lastLoggerConfigEdited);
        
        LoggingHandler lastHandlerEdited = null;
        if (handlerDetails.getEditedEntity() != null) {
            // Instance inside details now different from instance in list.  Look up by name.
            lastHandlerEdited = loggingInfo.findHandler(handlerDetails.getEditedEntity().getName());
        }
        handlerEditor.updateEntityList(loggingInfo.getHandlers(), lastHandlerEdited);
    }
    
    @Override
    public void enableLoggerDetails(boolean isEnabled) {
        loggerConfigEditor.enableDetails(isEnabled);
    }
    
    @Override
    public void enableHandlerDetails(boolean isEnabled) {
        handlerEditor.enableDetails(isEnabled);
    }
}
