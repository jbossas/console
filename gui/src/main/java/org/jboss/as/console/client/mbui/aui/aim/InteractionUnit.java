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

import org.jboss.as.console.client.mbui.aui.aim.assets.EventConsumption;
import org.jboss.as.console.client.mbui.aui.mapping.EntityContext;
import org.jboss.as.console.client.mbui.aui.mapping.Mapping;
import org.jboss.as.console.client.mbui.aui.mapping.MappingType;
import org.jboss.as.console.client.mbui.aui.mapping.Predicate;

/**
 * @author Harald Pehl
 * @date 10/24/2012
 */
public abstract class InteractionUnit implements EventConsumer
{
    public final static String ENTITY_CONTEXT_SUFFIX = "_entityContext";

    private final QName id;
    private String name;
    private final EntityContext entityContext;
    private EventConsumption eventConsumption;
    private InteractionUnit parent;


    protected InteractionUnit(String namespace, final String id)
    {
        this(new QName(namespace, id), null);
    }


    protected InteractionUnit(String namespace, final String id, final String name)
    {
        this(new QName(namespace, id), name);
    }


    protected InteractionUnit(final QName id, final String name)
    {
        assert id != null : "Id must not be null";
        assert !id.getNamespaceURI().isEmpty() : "Units require qualified namespace";
        this.id = id;
        this.name = name;
        this.entityContext = new EntityContext(id + ENTITY_CONTEXT_SUFFIX);
        this.eventConsumption = new EventConsumption(EventType.System);
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


    // ------------------------------------------------------ mappings

    public <T extends Mapping> T findMapping(MappingType type)
    {
        return findMapping(type, null);
    }

    public <T extends Mapping> T findMapping(MappingType type, Predicate<T> predicate)
    {
        T mapping = entityContext.getMapping(type);
        if (mapping != null)
        {
            // check predicate
            if (predicate != null)
            {
                mapping = (predicate.appliesTo(mapping)) ? mapping : null;
            }
        }
        if (mapping == null && parent != null)
        {
            mapping = parent.findMapping(type);
        }
        return mapping;
    }

    // ------------------------------------------------------ event handling

    @Override
    public EventType[] getConsumedTypes()
    {
        return eventConsumption.getConsumedTypes();
    }

    @Override
    public boolean consumes(Event event)
    {
        return eventConsumption.consumes(event);
    }

    // ------------------------------------------------------ properties

    public InteractionUnit getParent()
    {
        return parent;
    }

    void setParent(InteractionUnit parent)
    {
        this.parent = parent;
    }

    public boolean hasParent()
    {
        return parent != null;
    }

    public QName getId()
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

    public EntityContext getEntityContext()
    {
        return entityContext;
    }
}
