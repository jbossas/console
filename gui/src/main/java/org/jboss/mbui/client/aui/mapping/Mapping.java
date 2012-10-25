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
package org.jboss.mbui.client.aui.mapping;

import org.jboss.mbui.client.aui.domain.DomainModel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * @author Harald Pehl
 * @date 10/25/2012
 */
public class Mapping implements Iterable<String>
{
    public final static Mapping EMPTY = new Mapping(new DomainModel("__invalid_id__", "__invalid_address__"));

    private final DomainModel domainModel;
    private final List<String> attributes;

    public Mapping(final DomainModel domainModel)
    {
        assert domainModel != null : "DomainModel must not be null";
        this.domainModel = domainModel;
        this.attributes = new ArrayList<String>();
    }

    public void addAttributes(final String... attributes)
    {
        for (String attribute : attributes)
        {
            if (attribute != null && attribute.length() != 0)
            {
                this.attributes.add(attribute);
            }
        }
    }

    public DomainModel getDomainModel()
    {
        return domainModel;
    }

    @Override
    public Iterator<String> iterator()
    {
        return attributes.iterator();
    }

    @Override
    public String toString()
    {
        return domainModel.getId() + " -> " + attributes.toString();
    }


    // ------------------------------------------------------ delegate methods

    public int size() {return attributes.size();}

    public boolean isEmpty() {return attributes.isEmpty();}

    public String get(final int i) {return attributes.get(i);}

    public ListIterator<String> listIterator() {return attributes.listIterator();}
}
