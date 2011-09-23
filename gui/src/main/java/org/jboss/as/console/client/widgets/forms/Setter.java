package org.jboss.as.console.client.widgets.forms;

/**
 * @author Heiko Braun
 * @date 9/23/11
 */
public interface Setter<T> {
    void invoke(T entity, Object value);
}
