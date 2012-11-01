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
    final Set<ReificationStrategy<ReificationWidget>> strategies;

    public Reificator()
    {
        this.strategies = new HashSet<ReificationStrategy<ReificationWidget>>();
        this.strategies.add(new OrderIndependanceStrategy());
        this.strategies.add(new ChoiceStrategy());
        this.strategies.add(new SelectStrategy());
        this.strategies.add(new InputStrategy());
    }

    public ReificationWidget reify(final InteractionUnit interactionUnit, final Context context)
    {
        ReificationWidget result = null;
        if (interactionUnit != null)
        {
            result = startReification(interactionUnit, context);
        }
        return result;
    }

    private ReificationWidget startReification(final InteractionUnit interactionUnit, final Context context)
    {
        ReificationWidget reificationWidget = null;
        ReificationStrategy<ReificationWidget> strategy = resolve(interactionUnit);
        if (strategy != null)
        {
            reificationWidget = strategy.reify(interactionUnit, context);
            if (reificationWidget != null)
            {
                if (interactionUnit instanceof Container)
                {
                    Container container = (Container) interactionUnit;
                    for (InteractionUnit child : container.getChildren())
                    {
                        ReificationWidget childReificationWidget = startReification(child, context);
                        if (childReificationWidget != null)
                        {
                            reificationWidget.add(childReificationWidget, child, container);
                        }
                    }
                }
            }
        }
        return reificationWidget;
    }

    private ReificationStrategy<ReificationWidget> resolve(InteractionUnit interactionUnit)
    {
        ReificationStrategy<ReificationWidget> match = null;
        for (ReificationStrategy<ReificationWidget> strategy : strategies)
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
