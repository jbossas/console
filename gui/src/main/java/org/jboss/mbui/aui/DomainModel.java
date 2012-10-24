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
package org.jboss.mbui.aui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

/**
 * @author Harald Pehl
 * @date 10/24/2012
 */
public class DomainModel extends Model
{
    public final static DomainModel EMPTY = new DomainModel("__empty_domain_model__", "__no_address__");

    private final String address;
    private final Attributes defaultAttributes;
    private final Map<Group, Attributes> attributesByGroup;

    public DomainModel(final String id, final String address)
    {
        super(id);
        assert address != null : "Address must not be null";
        this.address = address;
        this.defaultAttributes = new Attributes();
        this.attributesByGroup = new HashMap<Group, Attributes>();
        this.attributesByGroup.put(Group.DEFAULT, defaultAttributes);
        setAuthor(Author.JBOSS);
    }

    public void addAttributes(final String... attributes)
    {
        addAttributes(Group.DEFAULT, attributes);
    }

    public void addAttributes(final Group name, final String... attributes)
    {
        assert name != null : "Name must not be null";
        if (attributes == null || attributes.length == 0)
        {
            return;
        }

        if (Group.DEFAULT.equals(name))
        {
            defaultAttributes.add(attributes);
        }
        else
        {
            Attributes attr = this.attributesByGroup.get(name);
            if (attr == null)
            {
                attr = new Attributes();
                this.attributesByGroup.put(name, attr);
            }
            attr.add(attributes);
        }
    }

    public Attributes getAttributes()
    {
        return getAttributes(Group.DEFAULT);
    }

    /**
     * In case the attributes for the specified group does not exist, {@link Attributes#EMPTY} is returned.
     * @param name
     * @return never null
     */
    public Attributes getAttributes(final Group name)
    {
        Attributes attributes = attributesByGroup.get(name);
        return attributes == null ? Attributes.EMPTY : attributes;
    }

    @Override
    public String toString()
    {
        return "DomainModel{" + getId() + ", " + address + '}';
    }

    public String getAddress()
    {
        return address;
    }


    // ------------------------------------------------------ inner clases

    /**
     * Value object for a group of attributes.
     */
    public static class Group
    {
        public final static Group DEFAULT = new Group("__default__");

        private final String name;

        public Group(final String name)
        {
            assert name != null : "Name must not be null";
            this.name = name;
        }

        @Override
        public boolean equals(final Object o)
        {
            if (this == o) { return true; }
            if (!(o instanceof Group)) { return false; }

            Group that = (Group) o;
            if (!name.equals(that.name)) { return false; }

            return true;
        }

        @Override
        public int hashCode()
        {
            return name.hashCode();
        }

        @Override
        public String toString()
        {
            return name;
        }
    }


    /**
     * Value object for the attributes of a domain model
     */
    public static class Attributes implements Iterable<String>
    {
        public final static Attributes EMPTY = new Attributes();

        private final List<String> attributes;

        public Attributes()
        {
            this.attributes = new ArrayList<String>();
        }

        private void add(final String... attributes)
        {
            for (String attribute : attributes)
            {
                if (attribute != null && attribute.length() != 0)
                {
                    this.attributes.add(attribute);
                }
            }
        }

        @Override
        public Iterator<String> iterator()
        {
            return attributes.iterator();
        }

        @Override
        public String toString()
        {
            return attributes.toString();
        }


        // ------------------------------------------------------ delegate methods

        public int size() {return attributes.size();}

        public boolean isEmpty() {return attributes.isEmpty();}

        public String get(final int i) {return attributes.get(i);}

        public ListIterator<String> listIterator() {return attributes.listIterator();}
    }
}
