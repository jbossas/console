/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
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
package org.jboss.mbui.model.structure.impl;

import org.jboss.mbui.model.mapping.Mapping;
import org.jboss.mbui.model.structure.Container;
import org.jboss.mbui.model.structure.InteractionUnit;

import java.util.Stack;

/**
 * @author Harald Pehl
 * @date 11/14/2012
 */
public final class Builder
{
    private InteractionUnit current;
    private final Stack<Container> container;

    public Builder()
    {
        this.container = new Stack<Container>();
    }

    public Builder start(Container container)
    {
        current = container;
        if (!this.container.isEmpty())
        {
            this.container.peek().add(current);
        }
        this.container.push(container);
        return this;
    }

    public Builder end()
    {
        current = container.pop();
        return this;
    }

    public Builder add(InteractionUnit interactionUnit)
    {
        if (container.isEmpty())
        {
            throw new IllegalStateException("No container");
        }
        container.peek().add(interactionUnit);
        current = interactionUnit;
        return this;
    }

    public Builder mappedBy(Mapping mapping)
    {
        if (current == null)
        {
            throw new IllegalStateException("No interaction unit");
        }

        // important ti distinguish the mapping and reference the model parts they belong to
        mapping.setCorrelationId(current.getId());

        current.addMapping(mapping);
        return this;
    }

    public InteractionUnit build()
    {
        if (current == null)
        {
            throw new IllegalStateException("Nothing added");
        }
        if (!container.isEmpty())
        {
            throw new IllegalStateException("Unmatched calls of start() and end()");
        }
        return current;
    }
}
