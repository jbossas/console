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
package org.jboss.as.console.client.mbui.aui.mapping;

/**
 * @author Harald Pehl
 * @date 10/25/2012
 */
public abstract class Mapping
{
    private static class EmptyMapping extends Mapping
    {
        private EmptyMapping()
        {
            super("__empty_id__");
        }
    }

    public final static Mapping EMPTY = new EmptyMapping();

    private final String id;

    protected Mapping(final String id)
    {
        assert id != null : "Id must not be null";
        this.id = id;
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o) { return true; }
        if (!(o instanceof Mapping)) { return false; }

        Mapping mapping = (Mapping) o;
        if (!id.equals(mapping.id)) { return false; }

        return true;
    }

    @Override
    public int hashCode()
    {
        return id.hashCode();
    }

    @Override
    public String toString()
    {
        return "Mapping{" + id + '}';
    }

    public String getId()
    {
        return id;
    }
}
