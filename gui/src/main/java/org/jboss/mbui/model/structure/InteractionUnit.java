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
package org.jboss.mbui.model.structure;

import org.jboss.mbui.model.behaviour.Consumer;
import org.jboss.mbui.model.behaviour.Producer;
import org.jboss.mbui.model.behaviour.Resource;
import org.jboss.mbui.model.behaviour.ResourceType;
import org.jboss.mbui.model.structure.impl.ResourceConsumption;
import org.jboss.mbui.model.structure.impl.ResourceProduction;
import org.jboss.mbui.model.mapping.Mapping;
import org.jboss.mbui.model.mapping.MappingType;
import org.jboss.mbui.model.mapping.Predicate;
import org.jboss.mbui.model.structure.impl.InteractionUnitVisitor;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Harald Pehl
 * @date 10/24/2012
 */
public abstract class InteractionUnit implements Consumer, Producer
{
    private final QName id;
    private InteractionUnit parent;
    private String name;

    private final Map<MappingType, Mapping> mappings;

    private ResourceConsumption resourceConsumption;
    private ResourceProduction resourceProduction;

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
        this.mappings = new EnumMap<MappingType, Mapping>(MappingType.class);
        this.resourceConsumption = new ResourceConsumption();
        this.resourceProduction = new ResourceProduction();
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

    /**
     * Finds a specific mapping over a hirarchy of interaction units following a bottom up approach.
     *
     * @param type
     * @param <T>
     *
     * @return
     */
    public <T extends Mapping> T findMapping(MappingType type)
    {
        return findMapping(type, null);
    }

    /**
     * Finds a specific mapping over a hirarchy of interaction units following a bottom up approach.
     * <p/>
     * The mapping is only returned if the predicate matches.
     *
     * @param type
     * @param predicate Use {@code null} to ignore
     * @param <T>
     *
     * @return
     */
    public <T extends Mapping> T findMapping(MappingType type, Predicate<T> predicate)
    {
        T mapping = getMapping(type);
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
    public Set<Resource<ResourceType>> getInputs()
    {
        return resourceConsumption.getInputs();
    }

    @Override
    public boolean doesConsume(Resource<ResourceType> event)
    {
        return resourceConsumption.doesConsume(event);
    }


    // ------------------------------------------------------ visitor related

    public void accept(InteractionUnitVisitor visitor)
    {
        visitor.visit(this);
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

    public boolean doesProduce()
    {
        return resourceProduction.doesProduce();
    }

    @Override
    public void setOutputs(Resource<ResourceType>... resource)
    {
        for(Resource<ResourceType> event : resource)
            event.setSource(getId());

        resourceProduction.setOutputs(resource);
    }

    @Override
    public void setInputs(Resource<ResourceType>... resource) {

        for(Resource<ResourceType> event : resource)
            event.setSource(getId());

        for(Resource<ResourceType> e : resource)
            resourceConsumption.setInputs(e);

    }

    public Set<Resource<ResourceType>> getOutputs() {
        return resourceProduction.getOutputs();
    }
}
