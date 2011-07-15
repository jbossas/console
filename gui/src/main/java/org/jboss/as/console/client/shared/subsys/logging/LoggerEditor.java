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
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SingleSelectionModel;
import java.util.ArrayList;
import java.util.Iterator;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.subsys.logging.model.LoggerConfig;
import org.jboss.ballroom.client.widgets.ContentGroupLabel;
import org.jboss.ballroom.client.widgets.ContentHeaderLabel;
import org.jboss.ballroom.client.widgets.tables.DefaultPager;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;

import java.util.List;

/**
 * @author Stan Silvert
 * @date 3/29/11
 */
public class LoggerEditor {
    private static int PAGE_SIZE = 15;

    private LoggingPresenter presenter;
    
    private ListDataProvider<LoggerConfig> loggerProvider;
    private DefaultCellTable<LoggerConfig> loggerConfigTable;
    private LoggerConfigDetails details;
    private boolean doneInitialSelection = false;
    private DefaultPager pager;

    public LoggerEditor(LoggingPresenter presenter) {
        this.presenter = presenter;
    }

    public Widget asWidget() {

        ScrollPanel scroll = new ScrollPanel();

        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("rhs-content-panel");
        
        scroll.add(layout);

        layout.add(new ContentHeaderLabel(Console.CONSTANTS.subsys_logging_loggers()));
        
        loggerConfigTable = new DefaultCellTable<LoggerConfig>(PAGE_SIZE);
        loggerConfigTable.setSelectionModel(new SingleSelectionModel<LoggerConfig>());
        loggerProvider = new ListDataProvider<LoggerConfig>();
        loggerProvider.addDataDisplay(loggerConfigTable);
        
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
        
        layout.add(loggerConfigTable);
        
        pager = new DefaultPager();
        pager.setDisplay(loggerConfigTable);
        layout.add(pager);
        
        details = new LoggerConfigDetails(presenter);
        details.bind(loggerConfigTable);
        layout.add(new ContentGroupLabel(Console.CONSTANTS.common_label_details()));
        layout.add(details.asWidget());
        
        return scroll;
    }

    public void updateLoggerConfigs(LoggingInfo loggingInfo) {
        List<LoggerConfig> loggers = new ArrayList();
        loggers.add(loggingInfo.getRootLogger());
        loggers.addAll(loggingInfo.getLoggers());
        loggerProvider.setList(loggers); 

        if (loggerConfigTable.isEmpty()) return;
        
        if (!doneInitialSelection) {
            setSelected(loggers.get(0));
            return;
        }
        
        if (details.getEditedLoggerConfig() == null) {
            setSelected(loggers.get(0));
            return;
        }
        
        LoggerConfig clone = loggingInfo.findLoggerConfig(details.getEditedLoggerConfig().getName());
        if(clone == null) {
            setSelected(loggers.get(0));
            return;
        }
        
        setSelected(clone);
    }
    
    private void setSelected(LoggerConfig logger) {
        loggerConfigTable.getSelectionModel().setSelected(logger, true);
        doneInitialSelection = true;
        List<LoggerConfig> loggers = loggerProvider.getList();
        int position = loggers.indexOf(logger);
        int page = position/PAGE_SIZE;
        pager.setPage(page);
    }
    
    public void enableLoggerDetails(boolean isEnabled) {
        this.details.setEnabled(isEnabled);
    }
}
