package org.jboss.as.console.client.domain.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple util to filter entities.
 * Commonly used to filter {@link com.google.gwt.user.cellview.client.CellTable} contents.
 *
 * @author Heiko Braun
 * @date 3/9/11
 */
public class EntityFilter<T> {

    /**
     * Filter a list of entities through a predicate.
     * If the the predicate applies the entity will be included.
     *
     * @param predicate
     * @param candidates
     * @return a subset of the actual list of candidates
     */
    public List<T> apply(Predicate<T> predicate, List<T> candidates)
    {
        List<T> filtered = new ArrayList<T>(candidates.size());

        for(T entity : candidates)
        {
            if(predicate.appliesTo(entity))
                filtered.add(entity);
        }

        return filtered;
    }
}
