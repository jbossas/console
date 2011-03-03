package org.jboss.as.console.client.shared;

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.ListDataProvider;
import org.jboss.as.console.client.domain.groups.PropertyRecord;
import org.jboss.as.console.client.widgets.tables.DefaultCellTable;
import org.jboss.as.console.client.widgets.tables.DefaultEditTextCell;
import org.jboss.as.console.client.widgets.tables.DefaultOptionRolloverHandler;
import org.jboss.as.console.client.widgets.tables.OptionCell;

/**
 * @author Heiko Braun
 * @date 3/3/11
 */
public class PropertyTable extends VerticalPanel {

    BeanFactory beanFactory = GWT.create(BeanFactory.class);
    DefaultCellTable<PropertyRecord> propertyTable;
    ListDataProvider<PropertyRecord> propertyProvider;

    public PropertyTable() {
        super();
        setStyleName("fill-layout-width");

        propertyTable = new DefaultCellTable<PropertyRecord>(5);
        propertyProvider  = new ListDataProvider<PropertyRecord>();
        propertyProvider.addDataDisplay(propertyTable);

        Button addProp = new Button("Add");
        addProp.setStyleName("default-button");

        addProp.getElement().setAttribute("style", "float:right");
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

        add(addProp);


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

        add(propertyTable);

    }
    public DefaultCellTable<PropertyRecord> asTable() {
        return propertyTable;
    }

    public ListDataProvider<PropertyRecord> asProvider() {
        return propertyProvider;
    }
}
