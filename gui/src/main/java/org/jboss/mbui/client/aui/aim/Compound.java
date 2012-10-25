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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Harald Pehl
 * @date 10/24/2012
 */
public class Compound extends Output implements Iterable<InteractionUnit>
{
    private final List<InteractionUnit> children;

    public Compound(final String id)
    {
        super(id);
        this.children = new ArrayList<InteractionUnit>();
    }

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

    @Override
    public Iterator<InteractionUnit> iterator()
    {
        return children.iterator();
    }


    // ------------------------------------------------------ delegate method

    public int size() {return children.size();}

    public boolean isEmpty() {return children.isEmpty();}

    public void clear() {children.clear();}

    public InteractionUnit get(final int i) {return children.get(i);}
}
