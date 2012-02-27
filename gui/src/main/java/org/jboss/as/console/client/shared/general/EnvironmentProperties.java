package org.jboss.as.console.client.shared.general;

import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.TextAreaItem;
import org.jboss.ballroom.client.widgets.forms.TextItem;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tables.DefaultPager;

import java.util.Comparator;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 2/27/12
 */
public class EnvironmentProperties {

    private ListDataProvider<PropertyRecord> propertyProvider;
    private DefaultCellTable<PropertyRecord> propertyTable;

   public Widget asWidget() {
        propertyTable = new DefaultCellTable<PropertyRecord>(8, new ProvidesKey<PropertyRecord>() {
            @Override
            public Object getKey(PropertyRecord item) {
                return item.getKey();
            }
        });
        propertyProvider = new ListDataProvider<PropertyRecord>();
        propertyProvider.addDataDisplay(propertyTable);


        ColumnSortEvent.ListHandler<PropertyRecord> sortHandler =
                new ColumnSortEvent.ListHandler<PropertyRecord>(propertyProvider.getList());

        // Create columns
        Column<PropertyRecord, String> keyColumn = new Column<PropertyRecord, String>(
                new TextCell()) {

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


        Column<PropertyRecord, SafeHtml> valueColumn = new Column<PropertyRecord, SafeHtml>(
                new SafeHtmlCell()) {
            @Override
            public SafeHtml getValue(PropertyRecord object) {
                String val = object.getValue();
                return new SafeHtmlBuilder().appendHtmlConstant("<span title='" +
                        new SafeHtmlBuilder().appendEscaped(val).toSafeHtml().asString() + "'>" + val + "</span>").toSafeHtml();
            }
        };


        // Add the columns.
        propertyTable.addColumn(keyColumn, Console.CONSTANTS.common_label_key());
        propertyTable.addColumn(valueColumn, Console.CONSTANTS.common_label_value());


        propertyTable.addColumnSortHandler(sortHandler);
        propertyTable.getColumnSortList().push(keyColumn);



        // --

        Form<PropertyRecord> form = new Form<PropertyRecord>(PropertyRecord.class);

        TextItem name = new TextItem("key", "Name");
        TextAreaItem value = new TextAreaItem("value", "Value");

        form.setFields(name, value);
        form.setNumColumns(2);

        form.bind(propertyTable);

        // --

        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("fill-layout-width");

        layout.add(propertyTable);

        DefaultPager pager = new DefaultPager();
        pager.setDisplay(propertyTable);

        layout.add(pager);

        layout.add(form.asWidget());

        return layout;
    }

    public void setProperties(List<PropertyRecord> environment) {
        propertyTable.setRowCount(environment.size(), true);

        List<PropertyRecord> propList = propertyProvider.getList();
        propList.clear(); // cannot call setList() as that breaks the sort handler
        propList.addAll(environment);

        // Make sure the new values are properly sorted
        ColumnSortEvent.fire(propertyTable, propertyTable.getColumnSortList());

    }

    public DefaultCellTable<PropertyRecord> getPropertyTable() {
        return propertyTable;
    }
}
