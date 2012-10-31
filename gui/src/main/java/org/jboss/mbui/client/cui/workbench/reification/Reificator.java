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
package org.jboss.mbui.client.cui.workbench.reification;

import org.jboss.mbui.client.aui.aim.Container;
import org.jboss.mbui.client.aui.aim.InteractionUnit;
import org.jboss.mbui.client.cui.Context;
import org.jboss.mbui.client.cui.ReificationStrategy;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Harald Pehl
 * @date 10/25/2012
 */
public class Reificator
{
    final Set<ReificationStrategy<ContainerWidget>> strategies;

    public Reificator()
    {
        this.strategies = new HashSet<ReificationStrategy<ContainerWidget>>();
    }

    public ContainerWidget reify(final InteractionUnit interactionUnit, final Context context)
    {
        ContainerWidget result = null;
        if (interactionUnit != null)
        {
            result = startReification(interactionUnit, context);
        }
        return result;
    }

    private ContainerWidget startReification(final InteractionUnit interactionUnit, final Context context)
    {
        ContainerWidget containerWidget = null;
        ReificationStrategy<ContainerWidget> strategy = resolve(interactionUnit);
        if (strategy != null)
        {
            containerWidget = strategy.reify(interactionUnit, context);
            if (containerWidget != null)
            {
                if (interactionUnit instanceof Container)
                {
                    Container container = (Container) interactionUnit;
                    for (InteractionUnit child : container.getChildren())
                    {
                        ContainerWidget childContainerWidget = startReification(child, context);
                        if (childContainerWidget != null)
                        {
                            containerWidget.add(childContainerWidget.asWidget());
                        }
                    }
                }
            }
        }
        return containerWidget;
    }

    private ReificationStrategy<ContainerWidget> resolve(InteractionUnit interactionUnit)
    {
        ReificationStrategy<ContainerWidget> match = null;
        for (ReificationStrategy<ContainerWidget> strategy : strategies)
        {
            if(strategy.appliesTo(interactionUnit))
            {
                match = strategy;
                break;
            }
        }
        return match;
    }
}
