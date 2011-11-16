package org.jboss.as.console.client.shared.general.validation;

/**
* @author Heiko Braun
* @date 11/16/11
*/
public interface Decision<T> {
    boolean evaluate(T entity);
}
