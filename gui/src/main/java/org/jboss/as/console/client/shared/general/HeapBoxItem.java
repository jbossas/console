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

    @Override
    public boolean validate(String value) {
        boolean hasValue = super.validate(value);
        boolean matchPattern = regex.test(value);
        return hasValue && matchPattern;
    }

    @Override
    public String getErrMessage() {
        return Console.MESSAGES.common_validation_heapSize();
    }
}
