package org.jboss.as.console.client.widgets;

import com.google.gwt.user.client.ui.Button;

/**
 * @author Heiko Braun
 * @date 3/1/11
 */
public class DefaultButton extends Button {
    public DefaultButton(String title) {
        super(title);
        setStyleName("default-button");
    }
}
