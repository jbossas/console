package org.jboss.as.console.client.components.sgwt;

import com.smartgwt.client.widgets.Label;

/**
 * @author Heiko Braun
 * @date 2/15/11
 */
public class ContentHeaderLabel extends Label {

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
        setHeight(35);
    }
}
