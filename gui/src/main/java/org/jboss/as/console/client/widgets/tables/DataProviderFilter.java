package org.jboss.as.console.client.widgets.tables;

import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 7/31/12
 */
public class DataProviderFilter<T> {

    private ListDataProvider<T> delegate;
    private ArrayList<T> origValues;
    private Predicate predicate;
    private TextBox filter;

    public interface Predicate<T> {
        boolean match(String prefix, T candiate);
    }

    public DataProviderFilter(ListDataProvider<T> delegate, Predicate<T> predicate) {
        this.delegate = delegate;
        this.predicate = predicate;
    }

    public void reset() {
        this.origValues = new ArrayList<T>(delegate.getList().size());
        this.origValues.addAll(delegate.getList());
        clearFilter();
        filter.setText("");

    }

    public Widget asWidget() {

        filter = new TextBox();
        filter.setMaxLength(30);
        filter.setVisibleLength(20);
        filter.getElement().setAttribute("style", "width:120px;");

        filter.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent keyUpEvent) {
                String prefix = filter.getText();

                if (prefix != null && !prefix.equals("")) {
                    // filter by prefix
                    filterByPrefix(prefix);
                } else {
                    clearFilter();
                }
            }
        });
        HorizontalPanel panel = new HorizontalPanel();
        Label label = new Label("Filter: ");
        panel.add(label);
        panel.add(filter);

        label.getElement().setAttribute("style", "padding-top:8px");
        return panel;
    }

    public void filterByPrefix(String prefix) {

        final List<T> next  = new ArrayList<T>();
        for(T item : origValues)
        {
            if(predicate.match(prefix, item))
                next.add(item);
        }


        delegate.getList().clear(); // cannot call setList() as that breaks the sort handler
        delegate.getList().addAll(next);
        delegate.flush();

    }

    public void clearFilter() {

        delegate.getList().clear(); // cannot call setList() as that breaks the sort handler
        delegate.getList().addAll(origValues);
        delegate.flush();

    }
}
