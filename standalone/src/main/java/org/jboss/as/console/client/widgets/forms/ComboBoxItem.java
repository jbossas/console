package org.jboss.as.console.client.widgets.forms;

import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.widgets.ComboBox;

/**
 * @author Heiko Braun
 * @date 2/21/11
 */
public class ComboBoxItem extends FormItem<String> {

    private ComboBox listBox;
    private boolean defaultToFirst;

    public ComboBoxItem(String name, String title) {
        super(name, title);
        this.listBox = new ComboBox();
    }

    @Override
    public String getValue() {
        return listBox.getSelectedValue();
    }

    @Override
    public void setValue(String value) {
        for(int i=0; i<listBox.getItemCount(); i++)
        {
            if(listBox.getValue(i).equals(value))
            {
                listBox.setItemSelected(i, true);
                break;
            }
        }
    }

    @Override
    public Widget asWidget() {
        return listBox.asWidget();
    }

    public void setDefaultToFirstOption(boolean b) {
        this.defaultToFirst = b;
    }

    public void setValueMap(String[] values) {
        listBox.clearValues();
        for(String s : values)
        {
            listBox.addItem(s);
        }
    }
}
