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
package org.jboss.as.console.client.mbui.aui.aim;

import org.jboss.as.console.client.mbui.aui.mapping.Mapping;

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

    public Builder start(final String namespace, final String id, final String name,
            final TemporalOperator temporalOperator)
    {
        current = new Container(namespace, id, name, temporalOperator);
        if (!container.isEmpty())
        {
            container.peek().add(current);
        }
        container.push((Container) current);
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

    public Builder addMapping(Mapping mapping)
    {
        if (current == null)
        {
            throw new IllegalStateException("No interaction unit");
        }
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
