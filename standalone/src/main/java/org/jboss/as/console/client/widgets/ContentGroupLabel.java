package org.jboss.as.console.client.widgets;

import com.google.gwt.user.client.ui.HTML;

/**
 * @author Heiko Braun
 * @date 2/15/11
 */
public class ContentGroupLabel extends HTML {

    private String icon;

    public ContentGroupLabel(String title) {
        super(title);
        setStyleName("content-group-label");
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
