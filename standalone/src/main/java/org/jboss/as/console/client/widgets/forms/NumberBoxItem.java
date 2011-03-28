package org.jboss.as.console.client.widgets.forms;

import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Heiko Braun
 * @date 3/28/11
 */
public class NumberBoxItem extends FormItem<Integer> {

    private TextBox textBox;
    private InputElementWrapper wrapper;

    public NumberBoxItem(String name, String title) {
        super(name, title);

        textBox = new TextBox();
        textBox.setName(name);
        textBox.setTitle(title);

        wrapper = new InputElementWrapper(textBox);

    }

    @Override
    public Widget asWidget() {
        return wrapper;
    }

    @Override
    public Integer getValue() {
        String value = textBox.getValue().equals("") ? "0" : textBox.getValue();
        return Integer.valueOf(value);
    }

    @Override
    public void setValue(Integer number) {
        textBox.setValue(String.valueOf(number));
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

    private ValidationHandler validationHandler = new ValidationHandler<Integer>()
    {
        @Override
        public ValidationResult validate(Integer value) {
            if(isRequired() && textBox.getValue().equals(""))
                return new ValidationResult(false, "Field '"+title+ "' is required.");
            else
                return FormItem.VALIDATION_SUCCESS;
        }
    };

    @Override
    public ValidationHandler getValidationHandler() {
        return validationHandler;
    }
}
