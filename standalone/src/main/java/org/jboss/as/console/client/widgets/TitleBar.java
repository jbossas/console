package org.jboss.as.console.client.widgets;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * A title bar to be displayed at the top of a content view -
 * contains a label and/or an icon.
 *
 * @author Heiko Braun
 */
public class TitleBar extends HorizontalPanel {


    public TitleBar(String title) {
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

    public void setIcon(ImageResource icon) {

    }

}
