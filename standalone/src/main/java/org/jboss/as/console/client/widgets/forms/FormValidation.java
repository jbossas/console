package org.jboss.as.console.client.widgets.forms;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 3/28/11
 */
public class FormValidation {

    private List<ValidationResult> errors = new ArrayList<ValidationResult>();

    public void addError(ValidationResult fieldResult)
    {
        errors.add(fieldResult);
    }

    public boolean hasErrors()
    {
        return errors.size()>0;
    }

    public List<ValidationResult> getErrors()
    {
        return errors;
    }
}
