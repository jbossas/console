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

package org.jboss.as.console.client.domain.groups;

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.widgets.tables.DefaultCellTable;
import org.jboss.as.console.client.widgets.tables.DefaultEditTextCell;
import org.jboss.as.console.client.widgets.tables.DefaultOptionRolloverHandler;
import org.jboss.as.console.client.widgets.tables.OptionCell;
import org.jboss.as.console.client.widgets.tools.ToolButton;
import org.jboss.as.console.client.widgets.tools.ToolStrip;

/**
 * @author Heiko Braun
 * @date 4/20/11
 */
public class PropertyEditor {

    private ListDataProvider<PropertyRecord> propertyProvider;
    private DefaultCellTable<PropertyRecord> propertyTable;
    private ToolButton addProp;
    private BeanFactory beanFactory = GWT.create(BeanFactory.class);
    private ServerGroupPresenter presenter;
    private ServerGroupRecord selectedRecord;

    public PropertyEditor(ServerGroupPresenter presenter) {
        this.presenter = presenter;
    }

    public Widget asWidget() {
        VerticalPanel panel = new VerticalPanel();
        panel.addStyleName("fill-layout-width");

        propertyTable = new DefaultCellTable<PropertyRecord>(5);
        propertyProvider = new ListDataProvider<PropertyRecord>();
        propertyProvider.addDataDisplay(propertyTable);


        ToolStrip propTools = new ToolStrip();

        addProp = new ToolButton("Add");

        addProp.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                PropertyRecord newRecord = beanFactory.property().as();
                newRecord.setKey("key");
                newRecord.setValue("value");
                propertyProvider.getList().add(newRecord);
                propertyProvider.refresh();
            }
        });
        propTools.addToolButton(addProp);
        panel.add(propTools);

        // Create columns
        Column<PropertyRecord, String> keyColumn = new Column<PropertyRecord, String>(new DefaultEditTextCell()) {

            {
                setFieldUpdater(new FieldUpdater<PropertyRecord, String>() {

                    @Override
                    public void update(int index, PropertyRecord object, String value) {
                        object.setKey(value);
                    }
                });
            }

            @Override
            public String getValue(PropertyRecord object) {
                return object.getKey();
            }

        };

        Column<PropertyRecord, String> valueColumn = new Column<PropertyRecord, String>(new DefaultEditTextCell()) {
            {
                setFieldUpdater(new FieldUpdater<PropertyRecord, String>() {

                    @Override
                    public void update(int index, PropertyRecord object, String value) {
                        object.setValue(value);
                    }
                });
            }

            @Override
            public String getValue(PropertyRecord object) {
                return object.getValue();
            }
        };

        OptionCell optionCell = new OptionCell("remove", new ActionCell.Delegate<String>()
        {
            @Override
            public void execute(String rowNum) {
                Integer row = Integer.valueOf(rowNum);
                PropertyRecord propertyRecord = propertyProvider.getList().get(row);
                propertyProvider.getList().remove(propertyRecord);
                propertyProvider.refresh();
            }
        });

        Column<PropertyRecord, String> optionColumn = new Column<PropertyRecord, String>(optionCell) {
            @Override
            public String getValue(PropertyRecord object) {
                return "";
            }

        };

        // Add the columns.
        propertyTable.addColumn(keyColumn, "Key");
        propertyTable.addColumn(valueColumn, "Value");
        propertyTable.addColumn(optionColumn);


        propertyTable.setColumnWidth(keyColumn, 50, Style.Unit.PCT);
        propertyTable.setColumnWidth(valueColumn, 40, Style.Unit.PCT);
        propertyTable.setColumnWidth(optionColumn, 10, Style.Unit.PCT);


        propertyTable.setRowOverHandler(
                new DefaultOptionRolloverHandler(propertyProvider, propertyTable)
        );

        panel.add(propertyTable);

        return panel;
    }

    public void setSelectedRecord(ServerGroupRecord record) {
        this.selectedRecord = record;
    }
}
