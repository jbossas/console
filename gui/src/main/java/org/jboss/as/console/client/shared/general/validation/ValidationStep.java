package org.jboss.as.console.client.shared.general.validation;

import java.util.Map;

/**
 * @author Heiko Braun
 * @date 11/16/11
 */
public interface ValidationStep<T> {
    ValidationResult validate(T entity, Map<String, Object> changedValues);
    boolean doesApplyTo(T entity, Map<String, Object> changedValues);
}
