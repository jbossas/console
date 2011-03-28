package org.jboss.as.console.client.widgets.forms;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.widgets.DefaultButton;

/**
 * @author Heiko Braun
 * @date 3/9/11
 */
public class ButtonItem extends FormItem<Boolean> {

    protected DefaultButton button;

    public ButtonItem(String name, String title) {
        super(name, title);
        this.button = new DefaultButton(title);
    }

    @Override
    public Boolean getValue() {
        return true;
    }

    @Override
    public void setValue(Boolean value) {

    }

    @Override
    public Widget asWidget() {
        return button;
    }

    @Override
    public void setEnabled(boolean b) {
        button.setEnabled(b);
    }

    public void addClickHandler(ClickHandler handler)
    {
        this.button.addClickHandler(handler);
    }

    @Override
    public ValidationHandler getValidationHandler() {
        return new ValidationHandler<Boolean> () {
            @Override
            public ValidationResult validate(Boolean value) {
                return FormItem.VALIDATION_SUCCESS;
            }
        };
    }
}
