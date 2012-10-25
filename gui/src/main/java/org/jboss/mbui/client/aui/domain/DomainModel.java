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
package org.jboss.mbui.client.aui.domain;

/**
 * @author Harald Pehl
 * @date 10/24/2012
 */
public class DomainModel
{
    private final String id;
    private final String address;

    public DomainModel(final String id, final String address)
    {
        assert id != null : "Id must not be null";
        assert address != null : "Address must not be null";
        this.id = id;
        this.address = address;
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o) { return true; }
        if (!(o instanceof DomainModel)) { return false; }

        DomainModel model = (DomainModel) o;
        if (!id.equals(model.id)) { return false; }

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
        return "DomainModel{" + id + ", " + address + '}';
    }

    public String getId()
    {
        return id;
    }

    public String getAddress()
    {
        return address;
    }
}
