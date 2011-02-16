package org.jboss.as.console.client.components;

import com.smartgwt.client.widgets.Label;

/**
 * @author Heiko Braun
 * @date 2/16/11
 */
public class SpacerLabel extends Label {
    public SpacerLabel() {
        super("&nbsp;");
        setStyleName("lhs-spacer");
        setHeight(20);
    }
}
