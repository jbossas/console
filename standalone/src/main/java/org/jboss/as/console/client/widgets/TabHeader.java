package org.jboss.as.console.client.widgets;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * @author Heiko Braun
 * @date 2/25/11
 */
public class TabHeader extends HorizontalPanel {

    public TabHeader(String title) {
        super();
        getElement().setAttribute("style", "border-bottom:1px solid #A7ABB4;");

        HTML tabLeft = new HTML("");
        tabLeft.setStyleName("tab-left");
        HTML tabTitle = new HTML("<div style='padding-top:6px;'>"+title+"</div>");
        tabTitle.setStyleName("tab-title");
        HTML tabRight = new HTML("");
        tabRight.setStyleName("tab-right");

        add(tabLeft);
        add(tabTitle);
        add(tabRight);


    }
}
