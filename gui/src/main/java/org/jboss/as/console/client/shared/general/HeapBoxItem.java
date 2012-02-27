package org.jboss.as.console.client.shared.general;

import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.ballroom.client.widgets.forms.SuggestBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;

/**
 * @author Heiko Braun
 * @date 1/26/12
 */
public class HeapBoxItem extends SuggestBoxItem {

    private final static String heapSizeValidationPattern = "[\\d]{2,4}[mM]";
    private static RegExp regex = RegExp.compile(heapSizeValidationPattern);

    public HeapBoxItem(String name, String title) {
        super(name, title);
    }

    public HeapBoxItem(String name, String title, boolean required) {
        super(name, title, required);
    }

    @Override
    public Widget asWidget() {

        MultiWordSuggestOracle oracle = new MultiWordSuggestOracle();
        oracle.add("32m");
        oracle.add("64m");
        oracle.add("128m");
        oracle.add("256m");
        oracle.add("512m");
        oracle.add("1024m");

        setOracle(oracle);
        return super.asWidget();
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
