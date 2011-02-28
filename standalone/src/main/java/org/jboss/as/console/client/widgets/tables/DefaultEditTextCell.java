package org.jboss.as.console.client.widgets.tables;

import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.dom.client.Element;

/**
 * @author Heiko Braun
 * @date 2/28/11
 */
public class DefaultEditTextCell extends EditTextCell {

    private boolean enabled = true;

    @Override
    protected void edit(Context context, Element parent, String value) {
        if(this.enabled)
            super.edit(context, parent, value);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
