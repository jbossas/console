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
package org.jboss.mbui.client.aui.mapping.as7;

import org.jboss.mbui.client.aui.mapping.Mapping;

import java.util.ArrayList;
import java.util.List;

/**
 * Mapping for a concrete resource in the DMA model.
 *
 * @author Harald Pehl
 * @date 10/25/2012
 */
public class ResourceMapping extends Mapping
{
    private final String address;
    private final List<ResourceAttribute> attributes;

    public ResourceMapping(final String id, final String address)
    {
        super(id);
        assert address != null : "Address must not be null";
        this.address = address;
        this.attributes = new ArrayList<ResourceAttribute>();
    }

    public ResourceMapping addAttributes(final String... attributes)
    {
        for (String attribute : attributes)
        {
            if (attribute != null && attribute.length() != 0)
            {
                this.attributes.add(new ResourceAttribute(attribute));
            }
        }
        return this;
    }

    public ResourceMapping addAttribute(final ResourceAttribute attribute)
    {
        if (attribute != null)
        {
            this.attributes.add(attribute);
        }
        return this;
    }

    public String getAddress()
    {
        return address;
    }

    public List<ResourceAttribute> getAttributes()
    {
        return attributes;
    }
}
