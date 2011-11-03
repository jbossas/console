package org.jboss.as.console.client.shared.runtime.charts;

import com.google.gwt.visualization.client.DataTable;

import java.util.Date;

/**
 * @author Heiko Braun
 * @date 11/3/11
 */
public class DataTableAdapter {

    DataTable delegate;

    public DataTableAdapter(DataTable delegate) {
        this.delegate = delegate;
    }

    public void setValue(int row, int col, Object value)
    {
        if(value instanceof String)
            setValue(row,col, (String)value);
        else if(value instanceof Integer)
            setValue(row,col, (Integer)value);
        else if(value instanceof Date)
            setValue(row,col, (Date)value);
    }

    public void setValue(int row, int col, Integer value)
    {
        delegate.setValue(row, col, value.intValue());
    }

    public void setValue(int row, int col, String value)
    {
        delegate.setValue(row, col, value);
    }

    public void setValue(int row, int col, Date value)
    {
        delegate.setValue(row, col, value);
    }
}
