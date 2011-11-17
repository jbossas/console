package org.jboss.as.console.client.widgets.forms;


import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.ballroom.client.widgets.forms.FormItem;

/**
 * @author Heiko Braun
 * @date 11/17/11
 */
public class GroupTitleItem extends FormItem<String> {

    private Label widget;

    public GroupTitleItem(String title) {
        super("GroupTitleItem", title);
        this.widget = new Label(title);
        this.widget.setStyleName("group-title-item");
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
