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
 * @date 10/26/2012
 */
public class Composition implements Iterable<InteractionUnit>
{
    private final CompositionRole role;
    private final List<InteractionUnit> targets;

    public Composition(final CompositionRole role)
    {
        this.role = role;
        this.targets = new ArrayList<InteractionUnit>();
    }

    public void addTarget(InteractionUnit interactionUnit)
    {
        if (interactionUnit != null)
        {
            targets.add(interactionUnit);
        }
    }

    public void removeTarget(InteractionUnit interactionUnit)
    {
        if (interactionUnit != null)
        {
            targets.remove(interactionUnit);
        }
    }

    @Override
    public Iterator<InteractionUnit> iterator()
    {
        return targets.iterator();
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o) { return true; }
        if (!(o instanceof Composition)) { return false; }

        Composition that = (Composition) o;
        if (role != that.role) { return false; }

        return true;
    }

    @Override
    public int hashCode()
    {
        return role.hashCode();
    }

    @Override
    public String toString()
    {
        return "Composition{" + role + '}';
    }

    public CompositionRole getRole()
    {
        return role;
    }

    // ------------------------------------------------------ delegate method

    public int size() {return targets.size();}

    public boolean isEmpty() {return targets.isEmpty();}

    public void clear() {targets.clear();}

    public InteractionUnit get(final int i) {return targets.get(i);}
}
