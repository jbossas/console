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
package org.jboss.mbui.client.aui.aim;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Harald Pehl
 * @date 10/31/2012
 */
public class Container extends InteractionUnit
{
    private final List<InteractionUnit> children;
    private final TemporalOperator temporalOperator;


    public Container(final String id, final TemporalOperator temporalOperator)
    {
        this(id, null, temporalOperator);
    }

    public Container(final String id, final String name, final TemporalOperator temporalOperator)
    {
        super(id, name);
        this.temporalOperator = temporalOperator;
        this.children = new LinkedList<InteractionUnit>();
    }

    @Override
    public String toString()
    {
        return "Container{" + getId() + ", " + temporalOperator + '}';
    }


    // ------------------------------------------------------ parent / child relationship

    public void add(InteractionUnit interactionUnit)
    {
        if (interactionUnit != null)
        {
            children.add(interactionUnit);
        }
    }

    public void remove(InteractionUnit interactionUnit)
    {
        if (interactionUnit != null)
        {
            children.remove(interactionUnit);
        }
    }

    public List<InteractionUnit> getChildren()
    {
        return children;
    }


    // ------------------------------------------------------ properties

    public TemporalOperator getTemporalOperator()
    {
        return temporalOperator;
    }
}
