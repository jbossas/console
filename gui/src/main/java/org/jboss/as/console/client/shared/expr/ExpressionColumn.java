package org.jboss.as.console.client.shared.expr;

import com.google.gwt.user.cellview.client.TextColumn;

/**
 * @author Heiko Braun
 * @date 10/17/11
 */
public abstract class ExpressionColumn<T> extends TextColumn<T> {

    private String javaName;

    protected ExpressionColumn(String javaName) {
        this.javaName = javaName;
    }

    @Override
    public String getValue(T object) {

        String columnValue = ExpressionAdapter.getExpressionValue(object, javaName);
        if(null==columnValue)
            columnValue = getRealValue(object);

        return columnValue;
    }

    abstract protected String getRealValue(T object);

}
