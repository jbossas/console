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

package org.jboss.ballroom.client.widgets.forms;

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.help.StaticHelpPanel;
import org.jboss.as.console.client.widgets.tables.ButtonCell;
import org.jboss.ballroom.client.I18n;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tables.DefaultPager;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;
import org.jboss.ballroom.client.widgets.window.Feedback;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Stan Silvert
 * @date 10/28/2011
 */
public class ListEditor<T extends Comparable> {

    private ListDataProvider<T> listProvider;
    private DefaultCellTable<T> listTable;
    private ToolButton addItem;

    private ListManagement<T> listManager;
    private String helpText;
    private int numRows;
    private boolean enabled = true;
    private boolean allowEditItems = true;

    private String headerLabel;

    public ListEditor(ListManagement<T> listManager) {
        this(listManager, 5);
    }


    public ListEditor(ListManagement<T> listManager, int rows) {
        this(listManager, Console.CONSTANTS.common_label_value(), rows);
    }

    public ListEditor(ListManagement<T> listManager, String headerLabel, int rows) {
        this.headerLabel = headerLabel;
        this.listManager = listManager;
        this.numRows = rows;
    }

    public void setValueColumnHeader(String headerLabel) {
        this.headerLabel = headerLabel;
    }

    public Widget asWidget() {
        VerticalPanel panel = new VerticalPanel();
        panel.addStyleName("fill-layout-width");

        listTable = new DefaultCellTable<T>(numRows);
        listTable.getElement().setAttribute("style", "margin-top:5px;");
        listProvider = new ListDataProvider<T>();
        listProvider.addDataDisplay(listTable);

        ToolStrip itemTools = new ToolStrip();

        addItem = new ToolButton(Console.CONSTANTS.common_label_add());

        addItem.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if(ListEditor.this.enabled)
                    listManager.launchNewItemDialoge();
            }
        });
        addItem.ensureDebugId(Console.DEBUG_CONSTANTS.debug_label_add_listEditor());
        itemTools.addToolButtonRight(addItem);

        panel.add(itemTools);

        //       ColumnSortEvent.ListHandler<T> sortHandler =
        //               new ColumnSortEvent.ListHandler<T>(listProvider.getList());

        // Create columns
        Column<T, String> valueColumn = new Column<T, String>(new TextCell()) {

            @Override
            public String getValue(T object) {
                return object.toString();
            }

        };
        //      valueColumn.setSortable(true);

        Column<T, T> removeCol = new Column<T, T>(
                new ButtonCell<T>(I18n.CONSTANTS.common_label_remove(), new ActionCell.Delegate<T>() {
                    @Override
                    public void execute(final T o) {
                        Feedback.confirm(
                                Console.MESSAGES.deleteTitle(Console.CONSTANTS.common_label_item()),
                                Console.MESSAGES.deleteConfirm(Console.CONSTANTS.common_label_item())
                                , new Feedback.ConfirmationHandler() {
                            @Override
                            public void onConfirmation(boolean isConfirmed) {
                                if (isConfirmed)
                                     listManager.onDeleteItem(o);
                            }
                        });


                    }
                })
        ) {
            @Override
            public T getValue(T item) {
                return item;
            }
        };

        // Add the columns.
        listTable.addColumn(valueColumn, this.headerLabel);

        listTable.addColumn(removeCol, Console.CONSTANTS.common_label_option());


        listTable.setColumnWidth(valueColumn, 30, Style.Unit.PCT);

        listTable.setColumnWidth(removeCol, 20, Style.Unit.PCT);

//        listTable.addColumnSortHandler(sortHandler);
        listTable.getColumnSortList().push(valueColumn);

        if(helpText!=null)
        {
            StaticHelpPanel helpPanel = new StaticHelpPanel(helpText);
            panel.add(helpPanel.asWidget());
        }

        panel.add(listTable);

        DefaultPager pager = new DefaultPager();
        pager.setDisplay(listTable);

        panel.add(pager);

        return panel;
    }

    public void setHelpText(String helpText) {
        this.helpText = helpText;
    }

    public void setList(List<T> items) {
        assert items!=null : "items cannot be null!";
        listTable.setRowCount(items.size(), true);

        List<T> itemList = listProvider.getList();
        itemList.clear(); // cannot call setList() as that breaks the sort handler
        itemList.addAll(items);

        // Make sure the new values are properly sorted
        ColumnSortEvent.fire(listTable, listTable.getColumnSortList());
    }

    public void setEnabled(boolean enabled)
    {
        if(null==listTable)
            throw new IllegalStateException("You need to call asWidget() before enabling the ListEditor");

        this.enabled = enabled;
        listTable.setEnabled(enabled && allowEditItems);
        addItem.setEnabled(enabled);
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    /**
     * If set to false, editor will only allow add and delete, but not
     * in-place editing.
     *
     * @param allowEditItems
     */
    public void setAllowEditItems(boolean allowEditItems) {

        if(null==listTable)
            throw new IllegalStateException("You need to call asWidget() before enabling the ListEditor");

        this.allowEditItems = allowEditItems;
        listTable.setEnabled(enabled && allowEditItems);
    }

    public void clearValues() {

        if(null==listTable)
            throw new IllegalStateException("You need to call asWidget() before clearing the values");

        listProvider.setList(new ArrayList<T>());
        setEnabled(false);
    }
}
