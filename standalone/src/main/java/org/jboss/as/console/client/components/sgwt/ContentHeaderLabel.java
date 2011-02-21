package org.jboss.as.console.client.components.sgwt;

import com.google.gwt.user.client.ui.HTML;

/**
 * @author Heiko Braun
 * @date 2/15/11
 */
public class ContentHeaderLabel extends HTML {

    public ContentHeaderLabel() {
        super();
        setup();
    }

    public ContentHeaderLabel(String title) {
        super(title);
        setup();
    }

    private void setup()
    {
        setStyleName("content-header-label");
    }

    public void setIcon(String icon)
    {

    }
}
