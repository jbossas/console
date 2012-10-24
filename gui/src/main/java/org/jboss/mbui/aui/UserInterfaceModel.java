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
public class UserInterfaceModel extends Model
{
    private DomainModel domainModel;
    private InteractionUnit interactionUnit;

    public UserInterfaceModel(final String id)
    {
        super(id);
        this.domainModel = DomainModel.EMPTY;
    }

    /**
     * Binds the specified interaction unit to this UI model. The binding is bi-directional. Therefore this
     * method calls {@link InteractionUnit#setUserInterfaceModel(UserInterfaceModel)} with this instance as parameter.
     *
     * @param interactionUnit
     */
    public void bindInteractionUnit(final InteractionUnit interactionUnit)
    {
        this.interactionUnit = interactionUnit;
        if (this.interactionUnit != null)
        {
            this.interactionUnit.setUserInterfaceModel(this);
        }
    }

    public DomainModel getDomainModel()
    {
        return domainModel;
    }

    public void setDomainModel(final DomainModel domainModel)
    {
        assert domainModel != null : "DomainModel must not be null";
        this.domainModel = domainModel;
    }

    public InteractionUnit getInteractionUnit()
    {
        return interactionUnit;
    }
}
