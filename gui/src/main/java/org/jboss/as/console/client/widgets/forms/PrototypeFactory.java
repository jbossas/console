package org.jboss.as.console.client.widgets.forms;

/**
 * @author Heiko Braun
 * @date 9/23/11
 */
public interface PrototypeFactory<T> {
    T create();
}
