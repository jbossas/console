package org.jboss.gwt.flow.client;

/**
 * An execution delegate able to control the outcome.
 *
 * @author Heiko Braun
 * @date 3/8/13
 */
public interface Function<C> {
    void execute(Control<C> control);
}
