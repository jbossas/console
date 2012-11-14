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

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

/**
 * @author Harald Pehl
 * @date 10/25/2012
 */
public class EntityContext
{
    private final String id;
    private final Map<MappingType, Mapping> mappings;

    public EntityContext(final String id)
    {
        assert id != null : "Id must not be null";
        this.id = id;
        this.mappings = new EnumMap<MappingType,Mapping>(MappingType.class);
    }

    public void addMapping(Mapping mapping)
    {
        if (mapping != null)
        {
            mappings.put(mapping.getType(), mapping);
        }
    }

    public boolean hasMapping(MappingType type)
    {
        return mappings.get(type) != null;
    }

    public <T extends Mapping> T getMapping(final MappingType type)
    {
        return (T) mappings.get(type);
    }

    public Collection<Mapping> getMappings()
    {
        return mappings.values();
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o) { return true; }
        if (!(o instanceof EntityContext)) { return false; }

        EntityContext that = (EntityContext) o;
        if (!id.equals(that.id)) { return false; }

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
        return "EntityContext{" + id + '}';
    }

    public String getId()
    {
        return id;
    }
}
