package org.jboss.as.console.client.widgets;

import com.google.gwt.user.client.ui.HTMLPanel;

/**
 * @author Heiko Braun
 * @date 12/10/12
 */
public class Hint extends HTMLPanel {

    private static final String ICON = "<i class='icon-bullhorn'></i>&nbsp;";

    public Hint(String html) {
        super(ICON +html);
        setStyleName("hint-box");
    }
}
