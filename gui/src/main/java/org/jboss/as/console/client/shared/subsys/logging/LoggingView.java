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
import org.jboss.as.console.client.widgets.tables.DefaultCellTable;


/**
 * Main view class for Loggers and Handlers.  This class assembles the editor for each type and synchronizes
 * UI updates with the data on the back end.
 * 
 * @author Stan Silvert
 * @date 3/29/11
 */
public class LoggingView extends DisposableViewImpl implements LoggingPresenter.MyView {

    private EntityBridge loggerConfigBridge;
    private EntityBridge handlerBridge;
    
    private LoggingEditor<LoggerConfig> loggerConfigEditor;
    private LoggingDetails<LoggerConfig> loggerConfigDetails;
    private LoggingEntityFormFactory<LoggerConfig> loggerFormFactory;
    private AssignHandlerChooser<LoggerConfig> loggerConfigHandlerChooser;
    
    private LoggingEditor<LoggingHandler> handlerEditor;
    private LoggingDetails<LoggingHandler> handlerDetails;
    private DefaultCellTable<LoggingHandler> handlerTable;
    private LoggingEntityFormFactory<LoggingHandler> handlerFormFactory;
    private AssignHandlerChooser<LoggingHandler> loggingHandlerHandlerChooser;

    @Override
    public void setPresenter(LoggingPresenter presenter) {
        this.loggerConfigBridge = new LoggerConfigBridge(presenter);
        this.loggerFormFactory = new LoggerConfigFormFactory(LoggerConfig.class, this.loggerConfigBridge);
        
        this.handlerBridge = new HandlerBridge(presenter);
        this.handlerTable = makeHandlerTable(); // need this to be constructed here intead of in createWidget()
        this.handlerFormFactory = new HandlerFormFactory(LoggingHandler.class, this.handlerBridge);
    }
    
    @Override
    public Widget createWidget() {
        loggerConfigEditor = makeLoggerConfigEditor();
        handlerEditor = makeHandlerEditor();
        
        TabLayoutPanel tabLayoutpanel = new TabLayoutPanel(25, Style.Unit.PX);
        tabLayoutpanel.addStyleName("default-tabpanel");
        
        tabLayoutpanel.add(loggerConfigEditor.asWidget(), Console.CONSTANTS.subsys_logging_loggers());
        tabLayoutpanel.add(handlerEditor.asWidget(), Console.CONSTANTS.subsys_logging_handlers());

        return tabLayoutpanel;
    }
    
    private LoggingEditor<LoggingHandler> makeHandlerEditor() {
        this.loggingHandlerHandlerChooser = handlerFormFactory.makeAssignHandlerForm();
        AssignHandlerWindow<LoggingHandler> assignHandlerWindow = new AssignHandlerWindow<LoggingHandler>(Console.CONSTANTS.subsys_logging_addHandler(),
                                                                                                 this.loggingHandlerHandlerChooser,
                                                                                                 handlerBridge);
        UnassignHandlerChooser unassignChooser = handlerFormFactory.makeUnassignHandlerForm();
        UnassignHandlerWindow<LoggingHandler> unassignHandlerWindow = new UnassignHandlerWindow<LoggingHandler>(Console.CONSTANTS.subsys_logging_removeHandler(),
                                                                                                 unassignChooser,
                                                                                                 handlerBridge);
        handlerDetails = new LoggingDetails<LoggingHandler>(Console.CONSTANTS.subsys_logging_handlers(), 
                                                            handlerFormFactory.makeEditForm(), 
                                                            this.handlerBridge,
                                                            assignHandlerWindow,
                                                            unassignHandlerWindow);
        String title = Console.CONSTANTS.common_label_add() + " " + Console.CONSTANTS.subsys_logging_handlers();
        LoggingPopupWindow<LoggingHandler> window = new AddEntityWindow<LoggingHandler>(title, handlerFormFactory.makeAddEntityForm(), this.handlerBridge);
        return new LoggingEditor<LoggingHandler>(Console.CONSTANTS.subsys_logging_handlers(), window, handlerTable, handlerDetails);
    }
    
    private DefaultCellTable<LoggingHandler> makeHandlerTable() {
        DefaultCellTable<LoggingHandler> table = new DefaultCellTable<LoggingHandler>(15);

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

        table.addColumn(nameColumn, Console.CONSTANTS.common_label_name());
        table.addColumn(handlerTypeColumn, Console.CONSTANTS.subsys_logging_type());
        table.addColumn(levelColumn, Console.CONSTANTS.subsys_logging_logLevel());
        
        return table;
    }
    
    private LoggingEditor<LoggerConfig> makeLoggerConfigEditor() {
        this.loggerConfigHandlerChooser = loggerFormFactory.makeAssignHandlerForm();
        AssignHandlerWindow<LoggerConfig> assignHandlerWindow = new AssignHandlerWindow<LoggerConfig>(Console.CONSTANTS.subsys_logging_addHandler(),
                                                                                             this.loggerConfigHandlerChooser,
                                                                                             loggerConfigBridge);
        UnassignHandlerChooser unassignChooser = loggerFormFactory.makeUnassignHandlerForm();
        UnassignHandlerWindow<LoggerConfig> unassignHandlerWindow = new UnassignHandlerWindow<LoggerConfig>(Console.CONSTANTS.subsys_logging_removeHandler(),
                                                                                             unassignChooser,
                                                                                             loggerConfigBridge);
        loggerConfigDetails = new LoggingDetails<LoggerConfig>(Console.CONSTANTS.subsys_logging_loggers(), 
                                                               loggerFormFactory.makeEditForm(), 
                                                               loggerConfigBridge,
                                                               assignHandlerWindow,
                                                               unassignHandlerWindow);
        String title = Console.CONSTANTS.common_label_add() + " " + Console.CONSTANTS.subsys_logging_loggers();
        LoggingPopupWindow<LoggerConfig> window = new AddEntityWindow<LoggerConfig>(title, 
                                                                                    loggerFormFactory.makeAddEntityForm(), 
                                                                                    loggerConfigBridge);
        DefaultCellTable<LoggerConfig> table = makeLoggerConfigTable();
        return new LoggingEditor<LoggerConfig>(Console.CONSTANTS.subsys_logging_loggers(), window, table, loggerConfigDetails);
    }
    
    private DefaultCellTable<LoggerConfig> makeLoggerConfigTable() {
        DefaultCellTable<LoggerConfig> table = new DefaultCellTable<LoggerConfig>(15);
        
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
        
        table.addColumn(nameColumn, Console.CONSTANTS.common_label_name());
        table.addColumn(levelColumn, Console.CONSTANTS.subsys_logging_logLevel());
        table.addColumn(handlersColumn, Console.CONSTANTS.subsys_logging_handlers());
        
        return table;
    }

    @Override
    public void updateLoggingInfo(LoggingInfo loggingInfo) {
        List<LoggerConfig> loggers = new ArrayList();
        loggers.add(loggingInfo.getRootLogger()); // root logger is always first in list
        loggers.addAll(loggingInfo.getLoggers());
        LoggerConfig lastLoggerConfigEdited = null;
        if (loggingInfo.getLoggerConfigEdited() != null) {
            // Look up by name.
            lastLoggerConfigEdited = loggingInfo.findLoggerConfig(loggingInfo.getLoggerConfigEdited());
        }
        loggerConfigEditor.updateEntityList(loggers, lastLoggerConfigEdited);
        
        LoggingHandler lastHandlerEdited = null;
        if (loggingInfo.getHandlerEdited() != null) {
            // Look up by name.
            lastHandlerEdited = loggingInfo.findHandler(loggingInfo.getHandlerEdited());
        }
        handlerEditor.updateEntityList(loggingInfo.getHandlers(), lastHandlerEdited);
        
        this.loggerConfigHandlerChooser.updateAvailableHandlers(loggingInfo.getHandlerNames());
        this.loggingHandlerHandlerChooser.updateAvailableHandlers(loggingInfo.getHandlerNames());
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
