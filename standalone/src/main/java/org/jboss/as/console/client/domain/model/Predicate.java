package org.jboss.as.console.client.domain.model;

/**
 * Used with {@link EntityFilter}
 * If the predicate applies the entity T will be included
 * in the resulting subset.
 *
 * @author Heiko Braun
 * @date 3/9/11
 */
public interface Predicate<T>
{
    boolean appliesTo(T candidate);
}