package org.jboss.as.console.client.widgets.forms;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 3/28/11
 */
public class FormValidation {

    private List<String> fieldNames = new ArrayList<String>();

    public void addError(String name)
    {
        fieldNames.add(name);
    }

    public boolean hasErrors()
    {
        return fieldNames.size()>0;
    }

    public List<String> getErrors()
    {
        return fieldNames;
    }
}
