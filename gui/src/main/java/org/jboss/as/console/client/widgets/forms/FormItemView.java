package org.jboss.as.console.client.widgets.forms;


import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.RowStyles;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.domain.model.ProfileRecord;
import org.jboss.ballroom.client.widgets.forms.FormItem;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 11/9/11
 */
public class FormItemView {


    private CellTable<Tuples> table;
    private FormItem[] items;
    private List<Tuples> tuples;

    public FormItemView(FormItem... items) {
        this.items = items;
    }

    public Widget asWidget() {

        table = new CellTable<Tuples>();
        table.setStyleName("fill-layout-width");

        Column firstCol = new Column<Tuples, String>(new TitleCell()) {
            @Override
            public String getValue(Tuples tuple) {
                return tuple.getFirst().getTitle();
            }
        };


        Column secondCol = new TextColumn<Tuples>() {
            @Override
            public String getValue(Tuples tuple) {
                return String.valueOf(tuple.getFirst().getValue());
            }
        };


        Column thirdCol = new Column<Tuples, String>(new TitleCell()) {
            @Override
            public String getValue(Tuples tuple) {
                if(tuple.getSecond()!=null)
                    return tuple.getSecond().getTitle();
                else
                    return "";
            }
        };

        Column fourthCol = new TextColumn<Tuples>() {
            @Override
            public String getValue(Tuples tuple) {
                if(tuple.getSecond()!=null)
                    return String.valueOf(tuple.getSecond().getValue());
                else
                    return "";
            }
        };


        //table.setColumnWidth(firstCol, 25, Style.Unit.PCT);
        table.setColumnWidth(secondCol, 35, Style.Unit.PCT);
        //table.setColumnWidth(thirdCol, 25, Style.Unit.PCT);
        table.setColumnWidth(fourthCol, 35, Style.Unit.PCT);


        table.addColumn(firstCol);
        table.addColumn(secondCol);
        table.addColumn(thirdCol);
        table.addColumn(fourthCol);


        table.setTableLayoutFixed(true);

        tuples = groupItems();

        return table;
    }

    public void refresh() {

        table.setRowCount(tuples.size(), true);
        table.setRowData(tuples);
    }

    private List<Tuples> groupItems() {
        List<Tuples> tuples = new ArrayList<Tuples>();

        for(int i=1; i<items.length; i+=2)
        {
            if(i>items.length)
            {
                // just the first item
                tuples.add(new Tuples(items[i-1], null));

            }
            else
            {
                // both items
                tuples.add(new Tuples(items[i-1], items[i]));
            }
        }
        return tuples;
    }

    private final class Tuples {
        FormItem first;
        FormItem second;

        Tuples(FormItem first, FormItem second) {
            this.first = first;
            this.second = second;
        }

        public FormItem getFirst() {
            return first;
        }

        public FormItem getSecond() {
            return second;
        }
    }


    interface Template extends SafeHtmlTemplates {
        @Template("<div class='form-item-title' style='outline:none;'>{0}: </div>")
        SafeHtml render(String title);
    }

    private static final Template TEMPLATE = GWT.create(Template.class);

    private class TitleCell extends AbstractCell<String> {

        @Override
        public void render(
                Context context,
                String title,
                SafeHtmlBuilder safeHtmlBuilder)
        {

            safeHtmlBuilder.append(
                    TEMPLATE.render(title)
            );

        }

    }

}
