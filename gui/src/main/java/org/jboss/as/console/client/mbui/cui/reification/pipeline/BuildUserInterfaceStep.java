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
package org.jboss.as.console.client.mbui.cui.reification.pipeline;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.jboss.as.console.client.mbui.aui.aim.Container;
import org.jboss.as.console.client.mbui.aui.aim.InteractionUnit;
import org.jboss.as.console.client.mbui.cui.Context;
import org.jboss.as.console.client.mbui.cui.ReificationStrategy;
import org.jboss.as.console.client.mbui.cui.reification.ChoiceStrategy;
import org.jboss.as.console.client.mbui.cui.reification.FormStrategy;
import org.jboss.as.console.client.mbui.cui.reification.OrderIndependanceStrategy;
import org.jboss.as.console.client.mbui.cui.reification.ReificationWidget;
import org.jboss.as.console.client.mbui.cui.reification.SelectStrategy;

import java.util.HashSet;
import java.util.Set;

import static java.lang.Boolean.TRUE;

/**
 * @author Harald Pehl
 * @date 11/12/2012
 */
public class BuildUserInterfaceStep extends ReificationStep
{
    final Set<ReificationStrategy<ReificationWidget>> strategies;

    public BuildUserInterfaceStep()
    {
        super("build ui");
        this.strategies = new HashSet<ReificationStrategy<ReificationWidget>>();
        this.strategies.add(new OrderIndependanceStrategy());
        this.strategies.add(new ChoiceStrategy());
        this.strategies.add(new SelectStrategy());
        this.strategies.add(new FormStrategy());
    }

    @Override
    public void execute(final AsyncCallback<Boolean> callback)
    {
        ReificationWidget widget = null;
        if (isValid())
        {
            widget = startReification(interactionUnit, context);
        }
        context.setAttribute("reificationWidget", widget);
        callback.onSuccess(TRUE);
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
