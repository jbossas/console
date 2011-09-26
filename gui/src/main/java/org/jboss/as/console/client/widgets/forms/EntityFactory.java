package org.jboss.as.console.client.widgets.forms;

/**
 * @author Heiko Braun
 * @date 9/26/11
 */
public interface EntityFactory<T> {
    T create();
}
