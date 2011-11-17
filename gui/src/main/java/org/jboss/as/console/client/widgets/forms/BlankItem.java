package org.jboss.as.console.client.widgets.forms;


import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.ballroom.client.widgets.forms.FormItem;

/**
 * @author Heiko Braun
 * @date 11/17/11
 */
public class BlankItem extends FormItem<String> {

    public static BlankItem INSTANCE = new BlankItem();

    private HTML widget;

    public BlankItem() {
        super("BlankItem", "");
        this.widget = new HTML();
    }

    @Override
    public Widget asWidget() {
        return widget;
    }

    @Override
    public void setEnabled(boolean b) {

    }

    @Override
    public boolean validate(String value) {
        return true;
    }

    @Override
    public void clearValue() {

    }

    @Override
    public String getValue() {
        return "";
    }

    @Override
    public void setValue(String value) {

    }
}
