package org.jboss.as.console.client.forms;

import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Heiko Braun
 * @date 2/21/11
 */
public class ComboBoxItem extends FormItem<String> {

    private ListBox listBox;
    private boolean defaultToFirst;

    public ComboBoxItem(String name, String title) {
        super(name, title);
        this.listBox = new ListBox();
    }

    @Override
    public String getValue() {
        return listBox.getValue(listBox.getSelectedIndex());
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
        return listBox;
    }

    public void setDefaultToFirstOption(boolean b) {
        this.defaultToFirst = b;
    }

    public void setValueMap(String[] values) {
        listBox.clear();
        for(String s : values)
        {
            listBox.addItem(s);
        }
    }
}
