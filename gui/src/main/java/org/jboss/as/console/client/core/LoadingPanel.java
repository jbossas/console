package org.jboss.as.console.client.core;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Heiko Braun
 * @date 12/7/11
 */
public class LoadingPanel {

    public Widget asWidget() {

        final LayoutPanel loadingPanel = new LayoutPanel();
        loadingPanel.setStyleName("loading-panel");

        Image loadingImage = new Image("images/loading_lite.gif");
        Label label = new Label("Loading ...");
        label.getElement().setAttribute("style", "padding-right:5px;");

        loadingPanel.add(loadingImage);
        loadingPanel.add(label);

        loadingPanel.setWidgetLeftRight(loadingImage, 5, Style.Unit.PX, 25, Style.Unit.PX);
        loadingPanel.setWidgetLeftRight(label, 38, Style.Unit.PX, 0, Style.Unit.PX);

        loadingPanel.setWidgetTopHeight(loadingImage, 10, Style.Unit.PX, 25, Style.Unit.PX);
        loadingPanel.setWidgetTopHeight(label, 15, Style.Unit.PX, 25, Style.Unit.PX);

        return loadingPanel;

    }
}
