package org.jboss.as.console.client.shared.general.validation;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 11/15/11
 */
public class ValidationResult {

    private List<String> messages = new LinkedList<String> ();

    private boolean success;

    public ValidationResult(boolean success) {
        this.success = success;
    }

    public void addMessage(String message) {
        messages.add(message);
    }

    public boolean isValid() {
        return success;
    }

    public List<String> getMessages() {
        return messages;
    }
}
