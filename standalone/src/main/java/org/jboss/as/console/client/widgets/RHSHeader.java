package org.jboss.as.console.client.widgets;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * @author Heiko Braun
 * @date 3/2/11
 */
public class RHSHeader extends HorizontalPanel{

    public RHSHeader(String title) {

        super();

        getElement().setAttribute("style", "width:100%");

        HTML spacerLeft = new HTML("&nbsp;");
        add(spacerLeft);
        spacerLeft.getElement().getParentElement().setAttribute("style", "border-bottom:1px solid #A7ABB4;");

        add(new TabHeader(title));

        HTML spacerRight= new HTML("&nbsp;");
        add(spacerRight);
        spacerRight.getElement().getParentElement().setAttribute("style", "width:100%;border-bottom:1px solid #A7ABB4;");

    }

}
