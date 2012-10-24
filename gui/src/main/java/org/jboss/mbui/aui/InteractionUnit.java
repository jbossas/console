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

/**
 * @author Harald Pehl
 * @date 10/24/2012
 */
public abstract class InteractionUnit
{
    private final String id;
    private UserInterfaceModel userInterfaceModel;
    private String name;
    private String role;
    private DomainModel.Group domainReference;

    protected InteractionUnit(final String id)
    {
        assert id != null : "Id must not be null";
        this.id = id;
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

    DomainModel safeDomainModel()
    {
        return userInterfaceModel != null ? userInterfaceModel.getDomainModel() : DomainModel.EMPTY;
    }

    public String getId()
    {
        return id;
    }

    public UserInterfaceModel getUserInterfaceModel()
    {
        return userInterfaceModel;
    }

    void setUserInterfaceModel(final UserInterfaceModel userInterfaceModel)
    {
        this.userInterfaceModel = userInterfaceModel;
    }

    public String getName()
    {
        return name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    public String getRole()
    {
        return role;
    }

    public void setRole(final String role)
    {
        this.role = role;
    }

    public DomainModel.Group getDomainReference()
    {
        return domainReference;
    }

    public void setDomainReference(final DomainModel.Group domainReference)
    {
        this.domainReference = domainReference;
    }
}
