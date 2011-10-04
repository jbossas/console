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
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Heiko Braun
 */
public class ExpressionCell extends AbstractCell<String> {

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

        Expression expr = Expression.fromString(value);
        SafeHtml html = new SafeHtmlBuilder()
                .appendHtmlConstant( "<div tabindex=\"-1\" class='cell-popup'>"+expr.toString()+"</div>").toSafeHtml();

        sb.append(html);

    }

    @Override
    protected void onEnterKeyDown(
            Context context, Element parent, String value,
            NativeEvent event, ValueUpdater<String> valueUpdater) {

        int rowSelection = context.getIndex();

        System.out.println("Clicked "+rowSelection);

    }

}
