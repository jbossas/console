package org.jboss.as.console.client.shared.expr;

/**
 * @author Heiko Braun
 * @date 10/4/11
 */

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

/**
 * @author Heiko Braun
 */
public class ExpressionCell extends AbstractCell<String> {

    private Expression expr;

    public ExpressionCell() {
        super("click", "keydown");
    }

    @Override
    public void onBrowserEvent(Context context, Element parent, String value,
                               NativeEvent event, ValueUpdater<String> valueUpdater) {
        super.onBrowserEvent(context, parent, value, event, valueUpdater);
        if ("click".equals(event.getType())) {
            onEnterKeyDown(context, parent, value, event, valueUpdater);
        }
    }

    @Override
    public void render(Context context, String value, SafeHtmlBuilder sb) {

        expr = Expression.fromString(value);
        SafeHtml html = new SafeHtmlBuilder()
                .appendHtmlConstant( "<div tabindex=\"-1\" class='expression-cell'>"+expr.toString()+"</div>").toSafeHtml();

        sb.append(html);

    }

    /*@Override
    protected void onEnterKeyDown(
            Context context, Element parent, String value,
            NativeEvent event, ValueUpdater<String> valueUpdater) {

        int rowSelection = context.getIndex();

        Console.MODULES.getExpressionManager().resolveValue(
                expr,new SimpleCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Window.confirm("Resolves to: "+ result);
            }
        });

        //System.out.println("Clicked "+rowSelection);

    } */

}
