package org.jboss.as.console.client.widgets;

import com.google.gwt.user.client.ui.HTML;

/**
 * @author Heiko Braun
 * @date 10/27/11
 */
public class ContentDescription extends HTML {
    public ContentDescription(String html) {
        super(html);

        setStyleName("content-description");
    }
}
