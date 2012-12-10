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

import com.google.gwt.cell.client.ActionCell;
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
import org.jboss.as.console.client.widgets.tables.TextLinkCell;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tables.DefaultEditTextCell;
import org.jboss.ballroom.client.widgets.tables.DefaultPager;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;
import org.jboss.ballroom.client.widgets.window.Feedback;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author Heiko Braun
 * @author David Bosschaert
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
    private boolean hideButtons = false;

    public PropertyEditor(PropertyManagement presenter) {
        this.presenter = presenter;
    }

    public PropertyEditor(int numRows) {
        this.numRows = numRows;
        this.simpleView = true;
        this.hideButtons = true;
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

    /**
     * This constructor creates a read-only instance
     */
    public PropertyEditor() {
        this.presenter = new ReadOnlyPropertyManagement();
        this.simpleView = true;
        this.hideButtons = true;
    }

    public Widget asWidget() {
        VerticalPanel panel = new VerticalPanel();
        panel.addStyleName("fill-layout-width");

        propertyTable = new DefaultCellTable<PropertyRecord>(numRows);
        propertyTable.getElement().setAttribute("style", "margin-top:5px;");
        propertyProvider = new ListDataProvider<PropertyRecord>();
        propertyProvider.addDataDisplay(propertyTable);

        if (!hideButtons) {
            ToolStrip propTools = new ToolStrip();

            addProp = new ToolButton(Console.CONSTANTS.common_label_add());

            addProp.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    if(PropertyEditor.this.enabled)
                        presenter.launchNewPropertyDialoge(reference);
                    else
                        System.out.println("PropertyEditor is disabled!");
                }
            });
            addProp.ensureDebugId(Console.DEBUG_CONSTANTS.debug_label_add_propertyEditor());
            propTools.addToolButtonRight(addProp);
            panel.add(propTools);
        }

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


        /*Column<PropertyRecord, SafeHtml> valueColumn = new Column<PropertyRecord, SafeHtml>(new SafeHtmlCell()) {
            @Override
            public SafeHtml getValue(PropertyRecord object) {
                String val = object.getValue();
                return new SafeHtmlBuilder().appendHtmlConstant("<span title='" +
                        new SafeHtmlBuilder().appendEscaped(val).toSafeHtml().asString() + "'>" + val + "</span>").toSafeHtml();
            }
        };*/


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


        Column<PropertyRecord, PropertyRecord> removeCol = new Column<PropertyRecord, PropertyRecord>(
                new TextLinkCell<PropertyRecord>(Console.CONSTANTS.common_label_delete(), new ActionCell.Delegate<PropertyRecord>() {
                    @Override
                    public void execute(final PropertyRecord o) {

                        Feedback.confirm(
                                Console.MESSAGES.removeProperty(),
                                Console.MESSAGES.removePropertyConfirm(o.getKey())
                                , new Feedback.ConfirmationHandler() {
                            @Override
                            public void onConfirmation(boolean isConfirmed) {
                                if(isConfirmed)
                                    presenter.onDeleteProperty(reference, o);
                            }
                        });

                    }
                })
        ) {
            @Override
            public PropertyRecord getValue(PropertyRecord propertyRecord) {
                return propertyRecord;
            }
        };

        // Add the columns.
        propertyTable.addColumn(keyColumn, Console.CONSTANTS.common_label_key());
        propertyTable.addColumn(valueColumn, Console.CONSTANTS.common_label_value());

        if(!simpleView)
            propertyTable.addColumn(bootColumn, "Boot-Time?");

        if(!hideButtons)
            propertyTable.addColumn(removeCol, Console.CONSTANTS.common_label_option());


        propertyTable.setColumnWidth(keyColumn, 30, Style.Unit.PCT);
        propertyTable.setColumnWidth(valueColumn, 30, Style.Unit.PCT);

        if(!simpleView)
            propertyTable.setColumnWidth(bootColumn, 20, Style.Unit.PCT);

        if(!hideButtons)
            propertyTable.setColumnWidth(removeCol, 20, Style.Unit.PCT);

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
        if(null==propertyTable)
            throw new IllegalStateException("You need to call asWidget() before enabling the PropertyEditor");

        this.enabled = enabled;
        propertyTable.setEnabled(enabled && allowEditProps);

    }

    /**
     * If set to false, editor will only allow add and delete, but not
     * in-place editing.
     *
     * @param allowEditProps
     */
    public void setAllowEditProps(boolean allowEditProps) {

        if(null==propertyTable)
            throw new IllegalStateException("You need to call asWidget() before setAllowEditProps() is called.");

        this.allowEditProps = allowEditProps;
        propertyTable.setEnabled(enabled && allowEditProps);
    }

    public void setHideButtons(boolean hideButtons) {
        if(null!=propertyTable)
            throw new IllegalStateException("You need to call this method before asWidget() is called.");

        this.hideButtons = hideButtons;
    }

    public void clearValues() {

        if(null==propertyTable)
            throw new IllegalStateException("You need to call asWidget() before clearing the values");

        propertyProvider.setList(new ArrayList<PropertyRecord>());
    }

    private static class ReadOnlyPropertyManagement implements PropertyManagement {
        @Override
        public void onCreateProperty(String reference, PropertyRecord prop) {
        }

        @Override
        public void onDeleteProperty(String reference, PropertyRecord prop) {
        }

        @Override
        public void onChangeProperty(String reference, PropertyRecord prop) {
        }

        @Override
        public void launchNewPropertyDialoge(String reference) {
        }

        @Override
        public void closePropertyDialoge() {
        }
    }

    public DefaultCellTable<PropertyRecord> getPropertyTable() {
        return propertyTable;
    }
}
