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

package org.jboss.as.console.client.shared.properties;

import java.util.Comparator;
import java.util.List;

import com.google.gwt.cell.client.FieldUpdater;
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
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tables.DefaultEditTextCell;
import org.jboss.ballroom.client.widgets.tables.DefaultPager;
import org.jboss.ballroom.client.widgets.tables.MenuColumn;
import org.jboss.ballroom.client.widgets.tables.NamedCommand;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;
import org.jboss.ballroom.client.widgets.window.Feedback;

/**
 * @author Heiko Braun
 * @date 4/20/11
 */
public class PropertyEditor {

    private ListDataProvider<PropertyRecord> propertyProvider;
    private DefaultCellTable<PropertyRecord> propertyTable;
    private ToolButton addProp;

    private PropertyManagement presenter;
    private String reference;
    private boolean simpleView = false;
    private String helpText;
    private int numRows = 5;
    private boolean enabled = true;
    private boolean allowEditProps = true;

    public PropertyEditor(PropertyManagement presenter) {
        this.presenter = presenter;
    }

    public PropertyEditor(PropertyManagement presenter, int rows) {
        this.presenter = presenter;
        this.numRows = rows;
    }

    public PropertyEditor(PropertyManagement presenter, boolean simpleView) {
        this.presenter = presenter;
        this.simpleView = simpleView;
    }

    public PropertyEditor(PropertyManagement presenter, boolean simpleView, int rows) {
        this.presenter = presenter;
        this.simpleView = simpleView;
        this.numRows = rows;
    }

    public Widget asWidget() {
        VerticalPanel panel = new VerticalPanel();
        panel.addStyleName("fill-layout-width");

        propertyTable = new DefaultCellTable<PropertyRecord>(numRows);
        propertyTable.getElement().setAttribute("style", "margin-top:5px;");
        propertyProvider = new ListDataProvider<PropertyRecord>();
        propertyProvider.addDataDisplay(propertyTable);


        ToolStrip propTools = new ToolStrip();

        addProp = new ToolButton(Console.CONSTANTS.common_label_add());

        addProp.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.launchNewPropertyDialoge(reference);
            }
        });
        propTools.addToolButtonRight(addProp);

        panel.add(propTools);

        ColumnSortEvent.ListHandler<PropertyRecord> sortHandler =
                new ColumnSortEvent.ListHandler<PropertyRecord>(propertyProvider.getList());

        // Create columns
        Column<PropertyRecord, String> keyColumn = new Column<PropertyRecord, String>(new DefaultEditTextCell()) {
            {
                setFieldUpdater(new FieldUpdater<PropertyRecord, String>() {

                    @Override
                    public void update(int index, PropertyRecord object, String value) {
                        object.setKey(value);
                        presenter.onChangeProperty(reference, object);
                    }
                });
            }

            @Override
            public String getValue(PropertyRecord object) {
                return object.getKey();
            }

        };
        keyColumn.setSortable(true);
        sortHandler.setComparator(keyColumn, new Comparator<PropertyRecord>() {
            @Override
            public int compare(PropertyRecord o1, PropertyRecord o2) {
                return o1.getKey().compareTo(o2.getKey());
            }
        });

        Column<PropertyRecord, String> valueColumn = new Column<PropertyRecord, String>(new DefaultEditTextCell()) {
            {
                setFieldUpdater(new FieldUpdater<PropertyRecord, String>() {

                    @Override
                    public void update(int index, PropertyRecord object, String value) {
                        object.setValue(value);
                        presenter.onChangeProperty(reference, object);
                    }
                });
            }

            @Override
            public String getValue(PropertyRecord object) {
                return object.getValue();
            }
        };

        Column<PropertyRecord, String> bootColumn = new Column<PropertyRecord, String>(new DefaultEditTextCell()) {
            {
                setFieldUpdater(new FieldUpdater<PropertyRecord, String>() {

                    @Override
                    public void update(int index, PropertyRecord object, String value) {
                        object.setBootTime(Boolean.valueOf(value));
                    }
                });
            }

            @Override
            public String getValue(PropertyRecord object) {
                return String.valueOf(object.isBootTime());
            }
        };


        NamedCommand removeCmd = new NamedCommand(Console.CONSTANTS.common_label_delete()) {
            @Override
            public void execute(int rownum) {
                if (!PropertyEditor.this.propertyTable.isEnabled()) return;
                final PropertyRecord property = propertyProvider.getList().get(rownum);

                if(simpleView)
                {
                    presenter.onDeleteProperty(reference, property);
                }
                else
                {
                    Feedback.confirm(Console.MESSAGES.removeProperty(), Console.MESSAGES.removePropertyConfirm(property.getKey()),
                            new Feedback.ConfirmationHandler() {
                                @Override
                                public void onConfirmation(boolean isConfirmed) {
                                    if(isConfirmed)
                                        presenter.onDeleteProperty(reference, property);
                                }
                            });
                }
            }
        };


        MenuColumn menuCol = new MenuColumn("...", removeCmd);

        // Add the columns.
        propertyTable.addColumn(keyColumn, Console.CONSTANTS.common_label_key());
        propertyTable.addColumn(valueColumn, Console.CONSTANTS.common_label_value());

        if(!simpleView)
            propertyTable.addColumn(bootColumn, "Boot-Time?");

        propertyTable.addColumn(menuCol, Console.CONSTANTS.common_label_option());


        propertyTable.setColumnWidth(keyColumn, 30, Style.Unit.PCT);
        propertyTable.setColumnWidth(valueColumn, 30, Style.Unit.PCT);

        if(!simpleView)
            propertyTable.setColumnWidth(bootColumn, 20, Style.Unit.PCT);

        propertyTable.setColumnWidth(menuCol, 20, Style.Unit.PCT);

        propertyTable.addColumnSortHandler(sortHandler);
        propertyTable.getColumnSortList().push(keyColumn);

        if(helpText!=null)
        {
            StaticHelpPanel helpPanel = new StaticHelpPanel(helpText);
            panel.add(helpPanel.asWidget());
        }

        //propertyTable.setEnabled(false);
        panel.add(propertyTable);

        DefaultPager pager = new DefaultPager();
        pager.setDisplay(propertyTable);

        panel.add(pager);

        return panel;
    }

    public void setHelpText(String helpText) {
        this.helpText = helpText;
    }

    public void setProperties(String reference, List<PropertyRecord> properties) {
        assert properties!=null : "properties cannot be null!";
        this.reference= reference;
        propertyTable.setRowCount(properties.size(), true);

        List<PropertyRecord> propList = propertyProvider.getList();
        propList.clear(); // cannot call setList() as that breaks the sort handler
        propList.addAll(properties);

        // Make sure the new values are properly sorted
        ColumnSortEvent.fire(propertyTable, propertyTable.getColumnSortList());
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
        propertyTable.setEnabled(enabled && allowEditProps);
        addProp.setEnabled(enabled);
    }
    
    /**
     * If set to false, editor will only allow add and delete, but not
     * in-place editing.
     * 
     * @param allowEditProps 
     */
    public void setAllowEditProps(boolean allowEditProps) {
        this.allowEditProps = allowEditProps;
        propertyTable.setEnabled(enabled && allowEditProps);
    }
    
}
