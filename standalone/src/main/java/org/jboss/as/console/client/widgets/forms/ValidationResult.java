package org.jboss.as.console.client.widgets.forms;

/**
 * @author Heiko Braun
 * @date 3/28/11
 */
public class ValidationResult {
    boolean isValid;
    String message;

    public ValidationResult(boolean valid, String message) {
        isValid = valid;
        this.message = message;
    }

    public boolean isValid() {
        return isValid;
    }

    public String getMessage() {
        return message;
    }
}
