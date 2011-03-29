package org.jboss.as.console.client.widgets.forms;

import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.widgets.ComboBox;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 2/21/11
 */
public class ComboBoxItem extends FormItem<String> {

    private ComboBox comboBox;
    private boolean defaultToFirst;
    private Widget widget = null;

    public ComboBoxItem(String name, String title) {
        super(name, title);
        this.comboBox = new ComboBox();
    }

    @Override
    public String getValue() {
        return comboBox.getSelectedValue();
    }

    @Override
    public void setValue(String value) {

        comboBox.clearSelection();

        for(int i=0; i< comboBox.getItemCount(); i++)
        {
            if(comboBox.getValue(i).equals(value))
            {
                comboBox.setItemSelected(i, true);
                break;
            }
        }
    }

    @Override
    public Widget asWidget() {
        if(null==widget)
            widget = comboBox.asWidget();
        return widget;
    }

    public void setDefaultToFirstOption(boolean b) {
        this.defaultToFirst = b;
    }

    public void setValueMap(String[] values) {
        comboBox.clearValues();
        for(String s : values)
        {
            comboBox.addItem(s);
        }

        if(defaultToFirst)
            comboBox.setItemSelected(0, true);
    }

    public void setValueMap(List<String> values) {
        comboBox.clearValues();
        for(String s : values)
        {
            comboBox.addItem(s);
        }

        if(defaultToFirst)
            comboBox.setItemSelected(0, true);
    }

    @Override
    public void setEnabled(boolean b) {
        comboBox.setEnabled(b);
    }

    @Override
    public boolean validate(String value) {
        if(isRequired() && comboBox.getSelectedValue().equals(""))
        {
            return false;
        }
        else
        {
            return true;
        }
    }

}
