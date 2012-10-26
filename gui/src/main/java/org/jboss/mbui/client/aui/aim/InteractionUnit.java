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

import org.jboss.mbui.client.aui.mapping.EntityContext;

import java.util.LinkedList;

/**
 * @author Harald Pehl
 * @date 10/24/2012
 */
public class InteractionUnit
{
    public final static String ENTITY_CONTEXT_STRING = "_entityContext";

    private final String id;
    private String name;
    private InteractionRole role;
    private EntityContext entityContext;
    private String mappingReference;
    private InteractionUnit parent;
    private LinkedList<InteractionUnit> children;

    public InteractionUnit(final String id)
    {
        this(id, null);
    }

    public InteractionUnit(final String id, final String name)
    {
        assert id != null : "Id must not be null";
        this.id = id;
        this.name = name;
        this.entityContext = new EntityContext(id + ENTITY_CONTEXT_STRING);
        this.children = new LinkedList<InteractionUnit>();
        this.role = InteractionRole.Overview;
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o) { return true; }
        if (!(o instanceof InteractionUnit)) { return false; }

        InteractionUnit that = (InteractionUnit) o;
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
        return "InteractionUnit{" + id + '}';
    }

    public void addComponent(InteractionUnit interactionUnit)
    {
        interactionUnit.setParent(this);
        this.children.add(interactionUnit);
    }

    public Iterable<InteractionUnit> getComponents()
    {
        return this.children;
    }

    public String getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    public InteractionRole getRole()
    {
        return role;
    }

    public void setRole(InteractionRole role)
    {
        this.role = role;
    }

    public EntityContext getEntityContext()
    {
        return entityContext;
    }

    public String getMappingReference()
    {
        return mappingReference;
    }

    public void setMappingReference(final String mappingReference)
    {
        this.mappingReference = mappingReference;
    }

    public boolean isComposite()
    {
        return !this.children.isEmpty();
    }

    private void setParent(final InteractionUnit parent)
    {
        this.parent = parent;
    }

    public InteractionUnit getParent()
    {
        return parent;
    }
}
