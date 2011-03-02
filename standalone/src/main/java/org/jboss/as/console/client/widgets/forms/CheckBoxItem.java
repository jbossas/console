package org.jboss.as.console.client.widgets.forms;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Heiko Braun
 * @date 3/2/11
 */
public class CheckBoxItem extends FormItem<Boolean> {

    private CheckBox checkBox;

    public CheckBoxItem(String name, String title) {
        super(name, title);
    }

    @Override
    public Boolean getValue() {
        return checkBox.getValue();
    }

    @Override
    public void setValue(Boolean value) {
        checkBox.setValue(value);
    }

    @Override
    public Widget asWidget() {
        checkBox = new CheckBox();
        return checkBox;
    }

    @Override
    public void setEnabled(boolean b) {
        checkBox.setEnabled(b);
    }
}
