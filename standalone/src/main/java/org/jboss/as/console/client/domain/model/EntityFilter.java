/*
 * JBoss, Home of Professional Open Source
 * Copyright <YEAR> Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

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
