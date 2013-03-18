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
package org.jboss.mbui.model.mapping;

/**
 * @author Harald Pehl
 * @author Heiko Braun
 *
 * @date 10/25/2012
 */
public abstract class Mapping
{
    protected MappingType type;
    protected String namespace;

    protected Mapping(MappingType type, String namespace)
    {
        this.type = type;
        this.namespace = namespace;
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o) { return true; }
        if (!(o instanceof Mapping)) { return false; }

        Mapping mapping = (Mapping) o;
        if (!type.equals(mapping.type)) { return false; }

        return true;
    }

    @Override
    public String toString()
    {
        return "Mapping{type=" + type + '}';
    }

    public String getNamespace()
    {
        return namespace;
    }

    public MappingType getType()
    {
        return type;
    }

    /**
     * Mapping exist in a hierarchical structure.
     * A nested (child) mapping can complement it's declarations with the parent mapping available to it.
     *
     * @param parent
     */
    public abstract void complementFrom(Mapping parent);

}
