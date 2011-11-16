package org.jboss.as.console.client.shared.general;

import java.util.HashMap;
import java.util.Set;

/**
 * @author Heiko Braun
 * @date 11/15/11
 */
public class ValidationResult {

    private HashMap<String, String> messages = new HashMap<String, String>();

    void addMessage(String javaName, String message) {
        messages.put(javaName, message);
    }

    public boolean isValid() {
        return messages.isEmpty();
    }

    public String asMessageString() {

        Set<String> keys = messages.keySet();
        StringBuilder builder = new StringBuilder();
        for(String key : keys)
        {
            builder.append(key).append(":");
            builder.append(messages.get(key));
        }

        return builder.toString();
    }
}
