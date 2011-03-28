package org.jboss.as.console.client.widgets.forms;

/**
 * Simple form item validation handler
 *
 * @author Heiko Braun
 * @date 3/28/11
 */
public interface ValidationHandler<T> {
    ValidationResult validate(T value);
}
