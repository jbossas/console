package org.jboss.as.console.client.widgets.forms;

import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Heiko Braun
 * @date 3/28/11
 */
public class NumberBoxItem extends FormItem<Integer> {

    private TextBox textBox;

    public NumberBoxItem(String name, String title) {
        super(name, title);

        textBox = new TextBox();
        textBox.setName(name);
        textBox.setTitle(title);
    }

    @Override
    public Widget asWidget() {
        return textBox;
    }

    @Override
    public Integer getValue() {
        return Integer.valueOf(textBox.getValue());
    }

    @Override
    public void setValue(Integer number) {
        textBox.setValue(String.valueOf(number));
    }

    @Override
    public void setEnabled(boolean b) {
        textBox.setEnabled(b);
    }
}
