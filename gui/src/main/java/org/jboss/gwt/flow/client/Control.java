package org.jboss.gwt.flow.client;

/**
 * Execution control handle passed into functions
 *
 * @author Heiko Braun
 * @date 3/8/13
 */
public interface Control<C> {

    void proceed();
    void abort();
    C getContext();
}

