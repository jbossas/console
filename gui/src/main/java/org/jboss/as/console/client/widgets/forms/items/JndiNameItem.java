package org.jboss.as.console.client.widgets.forms.items;

import org.jboss.ballroom.client.widgets.forms.TextBoxItem;

/**
 * @author Heiko Braun
 * @date 12/14/11
 */
public class JndiNameItem extends TextBoxItem {

    public JndiNameItem() {
        super("jndiName", "JNDI Name");
    }

    public JndiNameItem(String name, String title) {
        super(name, title);
    }

    @Override
    public boolean validate(String value) {

        boolean isSet = value!=null && !value.isEmpty();
        boolean validPrefix = value.startsWith("java:/") || value.startsWith("java:jboss/");
        return (!isRequired() && !isSet) || (isSet&&validPrefix);
    }

    @Override
    public String getErrMessage() {
        return "JNDI name has to start with 'java:/' or 'java:jboss/'";
    }
}
