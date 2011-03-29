package org.jboss.as.console.client.widgets.forms;

import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Heiko Braun
 * @date 2/21/11
 */
public class PasswordBoxItem extends FormItem<String> {

    private PasswordTextBox textBox;
    private InputElementWrapper wrapper;

    public PasswordBoxItem(String name, String title) {
        super(name, title);

        textBox = new PasswordTextBox();
        textBox.setName(name);
        textBox.setTitle(title);

        wrapper = new InputElementWrapper(textBox, this);
    }

    @Override
    public Widget asWidget() {
        return wrapper;
    }

    @Override
    public String getValue() {
        return textBox.getValue();
    }

    @Override
    public void setValue(String value) {
        textBox.setValue(value);
    }

    @Override
    public void setEnabled(boolean b) {
        textBox.setEnabled(b);
    }

    @Override
    public void setErroneous(boolean b) {
        super.setErroneous(b);
        wrapper.setErroneous(b);

    }

    @Override
    public boolean validate(String value) {
        if(isRequired() && value.equals(""))
        {
            return false;
        }
        else
        {
            return true;
        }
    }
}
