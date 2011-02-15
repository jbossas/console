package org.jboss.as.console.client.components.sgwt;

import com.smartgwt.client.widgets.Label;

/**
 * @author Heiko Braun
 * @date 2/15/11
 */
public class ContentGroupLabel extends Label {

    public ContentGroupLabel(String title) {
        super(title);
        setStyleName("content-group-label");
        setHeight(20);
    }
}
