package org.jboss.as.console.client.shared.general;

import com.google.gwt.regexp.shared.RegExp;
import org.jboss.as.console.client.Console;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;

/**
 * @author Heiko Braun
 * @date 1/26/12
 */
public class HeapBoxItem extends TextBoxItem {

    private final static String heapSizeValidationPattern = "[\\d]{2,4}[mM]";
    private static RegExp regex = RegExp.compile(heapSizeValidationPattern);

    public HeapBoxItem(String name, String title) {
        super(name, title);
    }

    public HeapBoxItem(String name, String title, boolean required) {
        super(name, title, required);
    }

    @Override
    public boolean validate(String value) {
        boolean parentValidation = super.validate(value);

        if(parentValidation && !value.isEmpty())
        {
            boolean matchPattern = regex.test(value);
            return parentValidation && matchPattern;
        }
        else
        {
            return parentValidation;
        }

    }

    @Override
    public String getErrMessage() {
        return Console.MESSAGES.common_validation_heapSize();
    }
}
