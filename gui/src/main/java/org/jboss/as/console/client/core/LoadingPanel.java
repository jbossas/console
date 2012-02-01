package org.jboss.as.console.client.core;

import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Heiko Braun
 * @date 12/7/11
 */
public class LoadingPanel {

    public Widget asWidget() {

        HTMLPanel html = new HTMLPanel("<center><div id='loading-panel'><img src='images/loading_lite.gif' style='padding-top:3px;vertical-align:middle'/> Loading ... </div></center>");

        return html.asWidget();

    }
}
