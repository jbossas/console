package org.jboss.as.console.client.widgets.forms;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Heiko Braun
 * @date 3/1/11
 */
public class TextItem extends FormItem<String> {

    private HTML html;

    public TextItem(String name, String title) {
        super(name, title);
        this.html = new HTML();
    }

    @Override
    public String getValue() {
        return html.getText();
    }

    @Override
    public void setValue(String value) {
        html.setText(value);
    }

    @Override
    public Widget asWidget() {
        return html;
    }

    @Override
    public void setEnabled(boolean b) {
        // it's not editable anyway
    }
}
