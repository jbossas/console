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
package org.jboss.mbui.gui.reification;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.jboss.mbui.gui.reification.strategy.ConcurrencyStrategy;
import org.jboss.mbui.model.structure.Container;
import org.jboss.mbui.model.structure.InteractionUnit;
import org.jboss.mbui.model.structure.impl.InteractionUnitVisitor;
import org.jboss.mbui.gui.reification.strategy.ChoiceStrategy;
import org.jboss.mbui.gui.reification.strategy.FormStrategy;
import org.jboss.mbui.gui.reification.strategy.ReificationWidget;
import org.jboss.mbui.gui.reification.strategy.SelectStrategy;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import static org.jboss.mbui.gui.reification.strategy.ContextKey.WIDGET;

/**
 * @author Harald Pehl
 * @author Heiko Braun
 * @date 11/12/2012
 */
public class BuildUserInterfaceStep extends ReificationStep
{
    final List<ReificationStrategy<ReificationWidget>> strategies;
    private StructureLogger logger = new StructureLogger();

    public BuildUserInterfaceStep()
    {
        super("build ui");
        this.strategies = new LinkedList<ReificationStrategy<ReificationWidget>>();
        // order is important! add specific strategies first!
        this.strategies.add(new FormStrategy());
        this.strategies.add(new SelectStrategy());
        this.strategies.add(new ConcurrencyStrategy());
        this.strategies.add(new ChoiceStrategy());

    }

    @Override
    public void execute(Iterator<ReificationStep> iterator, AsyncCallback<Boolean> outcome)
    {
        if (isValid())
        {
            assert !toplevelUnit.hasParent() : "Entry point interaction units are not expected to have parents";
            BuildUserInterfaceVisitor visitor = new BuildUserInterfaceVisitor();
            toplevelUnit.accept(visitor);
            System.out.println(logger.flush());
            context.set(WIDGET, visitor.root);
            System.out.println("Finished " + getName());
        }
        outcome.onSuccess(Boolean.TRUE);
        next(iterator, outcome);
    }


    class BuildUserInterfaceVisitor implements InteractionUnitVisitor
    {
        ReificationWidget root;
        Stack<ReificationWidget> container = new Stack<ReificationWidget>();

        @Override
        public void startVisit(final Container container)
        {
            logger.start(container);
            ReificationStrategy<ReificationWidget> strategy = resolve(container);
            if (strategy != null)
            {
                ReificationWidget widget = strategy.reify(container, context);
                if (widget != null)
                {
                    if (root == null)
                    {
                        root = widget;
                    }
                    if (!this.container.isEmpty())
                    {
                        this.container.peek().add(widget);
                    }
                    this.container.push(widget);
                }
            }
        }

        @Override
        public void visit(final InteractionUnit interactionUnit)
        {
            logger.start(interactionUnit);
            ReificationStrategy<ReificationWidget> strategy = resolve(interactionUnit);
            if (strategy != null)
            {
                ReificationWidget widget = strategy.reify(interactionUnit, context);
                if (widget != null && this.container.peek() != null)
                {
                    this.container.peek().add(widget);
                }
            }
            logger.end(interactionUnit);
        }

        @Override
        public void endVisit(final Container container)
        {
            logger.end(container);
            this.container.pop();
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
}
