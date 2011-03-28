package org.jboss.as.console.client.widgets.forms;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.widgets.icons.Icons;

/**
 * @author Heiko Braun
 * @date 3/28/11
 */
class InputElementWrapper extends HorizontalPanel {

    private Image img = new Image(Icons.INSTANCE.statusRed_small());

    public InputElementWrapper(Widget inputElement) {
        super();
        add(inputElement);
        add(img);
        img.setVisible(false);
        img.getElement().getParentElement().setAttribute("style", "vertical-align:middle");
    }

    public void setErroneous(boolean hasErrors)
    {
        img.setVisible(hasErrors);
    }

}
