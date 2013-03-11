package org.jboss.gwt.flow.client;

/**
 * Shared context.
 *
 * @author Heiko Braun
 * @date 3/11/13
 */
public class Ctx<T> {

    private T delegate;

    public Ctx(T delegate) {
        this.delegate = delegate;
    }

    public T get() {
        return delegate;
    }
}
