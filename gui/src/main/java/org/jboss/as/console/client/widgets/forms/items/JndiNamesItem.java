package org.jboss.as.console.client.widgets.forms.items;

import org.jboss.ballroom.client.widgets.forms.ListItem;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 12/14/11
 */
public class JndiNamesItem extends ListItem
{
    public JndiNamesItem(String name, String title)
    {
        super(name, title);
    }

    @Override
    public boolean validate(final List values)
    {
        if (values == null || values.isEmpty())
        {
            return false;
        }
        for (Object value : values)
        {
            if (!validateJndi(String.valueOf(value)))
            {
                return false;
            }
        }
        return true;
    }

    public boolean validateJndi(String value)
    {
        boolean isSet = value != null && !value.isEmpty();
        boolean validPrefix = value.startsWith("java:/") || value.startsWith("java:jboss/");
        return (!isRequired() && !isSet) || (isSet && validPrefix);
    }

    @Override
    public String getErrMessage()
    {
        return "JNDI names have to start with 'java:/' or 'java:jboss/'";
    }
}
