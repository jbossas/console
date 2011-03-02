package org.jboss.as.console.client.widgets;

import com.google.gwt.user.client.ui.HTML;

/**
 * @author Heiko Braun
 * @date 2/21/11
 */
public class StackSectionHeader extends HTML {

    public StackSectionHeader(String title) {
        super(title);
        setStyleName("stack-section-header");
    }

    public StackSectionHeader(String title, boolean inline) {
        super(title);
        setStyleName("stack-section-header-inline");
    }
}
