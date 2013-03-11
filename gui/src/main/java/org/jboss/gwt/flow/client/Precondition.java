package org.jboss.gwt.flow.client;

/**
 * A guard clause for certain flow semantics.
 *
 * @author Heiko Braun
 * @date 3/11/13
 */
public interface Precondition {
    boolean isMet();
}
