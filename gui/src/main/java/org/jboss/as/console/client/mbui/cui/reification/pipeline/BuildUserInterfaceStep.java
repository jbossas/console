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
import org.jboss.as.console.client.mbui.cui.reification.ContextKey;
import org.jboss.as.console.client.mbui.cui.reification.FormStrategy;
import org.jboss.as.console.client.mbui.cui.reification.OrderIndependanceStrategy;
import org.jboss.as.console.client.mbui.cui.reification.ReificationWidget;
import org.jboss.as.console.client.mbui.cui.reification.SelectStrategy;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

/**
 * @author Harald Pehl
 * @author Heiko Braun
 * @date 11/12/2012
 */
public class BuildUserInterfaceStep extends ReificationStep
{

    final LinkedList<ReificationStrategy<ReificationWidget>> strategies;
    private StringBuffer log = new StringBuffer();
    private int tabCount = 0;

    public BuildUserInterfaceStep()
    {
        super("BuildUserInterfaceStep");

        this.strategies = new LinkedList<ReificationStrategy<ReificationWidget>>();

        // setup order matters (precendence)

        this.strategies.add(new FormStrategy());
        this.strategies.add(new OrderIndependanceStrategy());
        this.strategies.add(new ChoiceStrategy());
        this.strategies.add(new SelectStrategy());
    }

    @Override
    public void execute(Iterator<ReificationStep> iterator, AsyncCallback<Boolean> outcome) {

        log = new StringBuffer();
        tabCount = 0;

        ReificationWidget widget = null;
        if (isValid())
        {
            assert !toplevelUnit.hasParent() : "Entry point interaction units are not expected to have parents";
            widget = startReification(toplevelUnit, context);
        }
        context.set(ContextKey.WIDGET, widget);

        System.out.println("Finished " + getName());
        System.out.println(log.toString());
        outcome.onSuccess(Boolean.TRUE);

        next(iterator, outcome);
    }

    private ReificationWidget startReification(final InteractionUnit parentUnit, final Context context)
    {
        start(parentUnit);

        ReificationStrategy<ReificationWidget> strategy = resolve(parentUnit);
        ReificationWidget parentWidget = strategy.reify(parentUnit, context);

        // process children
        if (parentUnit instanceof Container)
        {
            Container container = (Container) parentUnit;
            for (InteractionUnit childUnit : container.getChildren())
            {
                ReificationWidget childWidget = startReification(childUnit, context);
                parentWidget.add(childWidget, childUnit, container);
            }
        }

        end(parentUnit);

        return parentWidget;
    }



    private void start(InteractionUnit parentUnit) {
        tabCount++;
        for(int i=0; i<tabCount;i++)
            log.append("\t");
        log.append("<").append(parentUnit.getName()).append(">");
        log.append("\n");
    }

    private void end(InteractionUnit parentUnit) {
        for(int i=0; i<tabCount;i++)
            log.append("\t");
        log.append("</").append(parentUnit.getName()).append(">");
        log.append("\n");
        tabCount--;
    }

    private ReificationStrategy<ReificationWidget> resolve(InteractionUnit interactionUnit)
    {
        ReificationStrategy<ReificationWidget> match = null;
        for (ReificationStrategy<ReificationWidget> strategy : strategies)
        {
            if (strategy.appliesTo(interactionUnit))
            {
                match = strategy;
                break;
            }
        }
        return match;
    }
}
