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
import java.util.List;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.subsys.logging.model.LoggingHandler;
import org.jboss.as.console.client.widgets.ContentHeaderLabel;
import org.jboss.as.console.client.widgets.tables.DefaultCellTable;

/**
 * @author Stan Silvert
 * @date 3/29/11
 */
public class HandlerEditor {

    private LoggingPresenter presenter;
    private DefaultCellTable<LoggingHandler> handlerTable;
    private ListDataProvider<LoggingHandler> handlerProvider;
    
    public HandlerEditor(LoggingPresenter presenter) {
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
        handlerTable.setSelectionModel(new SingleSelectionModel<LoggingHandler>());
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

        return scroll;
    }
    
    
    public void updateHandlers(LoggingInfo loggingInfo) {
        List<LoggingHandler> handlers = loggingInfo.getHandlers();
        handlerProvider.setList(handlers);
        
        if(!handlerTable.isEmpty())
            handlerTable.getSelectionModel().setSelected(handlers.get(0), true);
    }
}
