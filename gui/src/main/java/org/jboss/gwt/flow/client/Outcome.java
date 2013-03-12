package org.jboss.gwt.flow.client;

/**
 * The final outcome of the controlled flow.
 *
 * @author Heiko Braun
 * @date 3/8/13
 */
public interface Outcome<C> {

    void onFailure();

    void onSuccess(C context);

}
