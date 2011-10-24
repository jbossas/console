package org.jboss.as.console.client.shared.general;

import org.jboss.ballroom.client.widgets.forms.TextBoxItem;

/**
 * @author Heiko Braun
 * @date 10/24/11
 */
public class NonRequiredTextBoxItem extends TextBoxItem {
    public NonRequiredTextBoxItem(String name, String title) {
        super(name, title);
    }

    @Override
    public boolean isRequired() {
        return false;
    }
}
