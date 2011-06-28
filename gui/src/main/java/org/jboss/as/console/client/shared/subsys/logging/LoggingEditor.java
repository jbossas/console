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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SingleSelectionModel;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.widgets.ContentGroupLabel;
import org.jboss.as.console.client.widgets.ContentHeaderLabel;
import org.jboss.as.console.client.widgets.DefaultPager;
import org.jboss.as.console.client.widgets.tables.DefaultCellTable;

import java.util.List;
import org.jboss.as.console.client.widgets.tools.ToolButton;
import org.jboss.as.console.client.widgets.tools.ToolStrip;

/**
 * @author Stan Silvert
 * @date 3/29/11
 */
public class LoggingEditor<T> {
    private String entitiesName;
    private AddLoggingEntityWindow<T> window;
    private ListDataProvider<T> dataProvider;
    private DefaultCellTable<T> table;
    private LoggingDetails<T> details;
    private boolean doneInitialSelection = false;
    private DefaultPager pager;

    /**
     * 
     * @param entitiesName The display name (plural) of the entities.
     * @param table The table that holds the entities.
     * @param details  The LoggingDetails that manages CRUD for the selected entity.
     */
    public LoggingEditor(String entitiesName, AddLoggingEntityWindow<T> window, DefaultCellTable<T> table, LoggingDetails<T> details) {
        this.entitiesName = entitiesName;
        this.window = window;
        this.table = table;
        this.details = details;
    }

    public Widget asWidget() {
        ScrollPanel scroll = new ScrollPanel();

        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("rhs-content-panel");
        
        scroll.add(layout);

        final ToolStrip toolStrip = new ToolStrip();
        toolStrip.addToolButtonRight(new ToolButton(Console.CONSTANTS.common_label_add(), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                window.show();
            }
        }));
        layout.add(toolStrip);
        
        layout.add(new ContentHeaderLabel(entitiesName));
        
        table.setSelectionModel(new SingleSelectionModel<T>());
        dataProvider = new ListDataProvider<T>();
        dataProvider.addDataDisplay(table);
        
        layout.add(table);
        
        pager = new DefaultPager();
        pager.setDisplay(table);
        layout.add(pager);
        
        details.bind(table);
        layout.add(new ContentGroupLabel(Console.CONSTANTS.common_label_details()));
        layout.add(details.asWidget());
        
        return scroll;
    }
    
    public void updateEntityList(List<T> entityList, T lastEdited) {
        dataProvider.setList(entityList); 

        if (table.isEmpty()) return;
        
        if (!doneInitialSelection) {
            setSelected(entityList.get(0));
            return;
        }
        
        if (details.getEditedEntity() == null) {
            setSelected(entityList.get(0));
            return;
        }
        
        if(lastEdited == null) {
            setSelected(entityList.get(0));
            return;
        }
        
        setSelected(lastEdited);
    }
    
    private void setSelected(T entity) {
        table.getSelectionModel().setSelected(entity, true);
        doneInitialSelection = true;
        List<T> loggers = dataProvider.getList();
        int position = loggers.indexOf(entity);
        int page = position/table.getPageSize();
        pager.setPage(page);
    }
    
    public void enableDetails(boolean isEnabled) {
        this.details.setEnabled(isEnabled);
    }
}
