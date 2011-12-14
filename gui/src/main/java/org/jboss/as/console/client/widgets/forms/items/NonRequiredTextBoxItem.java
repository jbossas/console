package org.jboss.as.console.client.widgets.forms.items;

import org.jboss.ballroom.client.widgets.forms.TextBoxItem;

/**
 * @author Heiko Braun
 * @date 12/14/11
 */
public class NonRequiredTextBoxItem extends TextBoxItem {

    public NonRequiredTextBoxItem(String name, String title) {
        super(name, title);
        setRequired(false);
    }
}
