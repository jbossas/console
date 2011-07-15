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
import org.jboss.as.console.client.widgets.ContentGroupLabel;
import org.jboss.as.console.client.widgets.ContentHeaderLabel;
import org.jboss.as.console.client.widgets.tables.DefaultPager;
import org.jboss.as.console.client.widgets.tables.DefaultCellTable;

/**
 * @author Stan Silvert
 * @date 3/29/11
 */
public class HandlerEditor {
    private static int PAGE_SIZE = 15;

    private LoggingPresenter presenter;
    private DefaultCellTable<LoggingHandler> handlerTable;
    private ListDataProvider<LoggingHandler> handlerProvider;
    private HandlerDetails details;
    private boolean doneInitialSelection = false;
    private DefaultPager pager;

    public HandlerEditor(LoggingPresenter presenter) {
        this.presenter = presenter;
    }

    public Widget asWidget() {

        ScrollPanel scroll = new ScrollPanel();

        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("rhs-content-panel");
        
        scroll.add(layout);

        layout.add(new ContentHeaderLabel(Console.CONSTANTS.subsys_logging_handlerConfigurations()));

        handlerTable = new DefaultCellTable<LoggingHandler>(PAGE_SIZE);
        SingleSelectionModel<LoggingHandler> selectionModel = new SingleSelectionModel<LoggingHandler>();
        handlerTable.setSelectionModel(selectionModel);
        handlerProvider = new ListDataProvider<LoggingHandler>();
        handlerProvider.addDataDisplay(handlerTable);

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

        layout.add(handlerTable);
        
        pager = new DefaultPager();
        pager.setDisplay(handlerTable);
        layout.add(pager);

        details = new HandlerDetails(presenter);
        details.bind(handlerTable);

        layout.add(new ContentGroupLabel(Console.CONSTANTS.common_label_details()));
        layout.add(details.asWidget());

        return scroll;
    }
    
    public void updateHandlers(LoggingInfo loggingInfo) {
        List<LoggingHandler> handlers = loggingInfo.getHandlers();
        handlerProvider.setList(handlers);
        
        if (handlerTable.isEmpty()) return;
        
        if (!doneInitialSelection) {
            setSelected(handlers.get(0));
            return;
        }
        
        if (details.getEditedLogger() == null) {
            setSelected(handlers.get(0));
            return;
        }
        
        // LoggingHandler instances were rebuilt from the server, so find the new one that
        // corresponds to the one that was just edited.
        LoggingHandler clone = loggingInfo.findHandler(details.getEditedLogger().getName());
        if (clone == null) {
            setSelected(handlers.get(0));
            return;
        }
        
        setSelected(clone);
    }
    
    private void setSelected(LoggingHandler handler) {
        handlerTable.getSelectionModel().setSelected(handler, true);
        doneInitialSelection = true;
        List<LoggingHandler> handlers = handlerProvider.getList();
        int position = handlers.indexOf(handler);
        int page = position/PAGE_SIZE;
        pager.setPage(page);
    }
    
    public void enableHandlerDetails(boolean isEnabled) {
        this.details.setEnabled(isEnabled);
    }
}
