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
package org.jboss.mbui.gui.reification.pipeline;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.mbui.gui.reification.Context;
import org.jboss.mbui.gui.reification.ReificationException;
import org.jboss.mbui.gui.reification.StructureLogger;
import org.jboss.mbui.gui.reification.strategy.ChoiceStrategy;
import org.jboss.mbui.gui.reification.strategy.ConcurrencyStrategy;
import org.jboss.mbui.gui.reification.strategy.DeactivationStrategy;
import org.jboss.mbui.gui.reification.strategy.FormStrategy;
import org.jboss.mbui.gui.reification.strategy.LinkStrategy;
import org.jboss.mbui.gui.reification.strategy.PullDownStrategy;
import org.jboss.mbui.gui.reification.strategy.ReificationStrategy;
import org.jboss.mbui.gui.reification.strategy.ReificationWidget;
import org.jboss.mbui.gui.reification.strategy.SelectStrategy;
import org.jboss.mbui.gui.reification.strategy.ToolStripStrategy;
import org.jboss.mbui.gui.reification.strategy.TriggerStrategy;
import org.jboss.mbui.model.Dialog;
import org.jboss.mbui.model.structure.Container;
import org.jboss.mbui.model.structure.InteractionUnit;
import org.jboss.mbui.model.structure.as7.StereoTypes;
import org.jboss.mbui.model.structure.impl.InteractionUnitVisitor;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import static org.jboss.mbui.gui.reification.ContextKey.WIDGET;

/**
 * TODO: Belongs to AS7 package
 *
 * @author Harald Pehl
 * @author Heiko Braun
 * @date 11/12/2012
 */
public class BuildUserInterfaceStep extends ReificationStep
{
    final List<ReificationStrategy<ReificationWidget, StereoTypes>> strategies;
    private StructureLogger logger = new StructureLogger();

    class BlankWidget implements ReificationWidget{
        private final InteractionUnit unit;

        BlankWidget(InteractionUnit unit) {
            this.unit = unit;
        }

        @Override
        public InteractionUnit getInteractionUnit() {
            return unit;
        }

        @Override
        public void add(ReificationWidget widget) {

        }

        @Override
        public Widget asWidget() {
            return new HTML("<div style='width:100%; padding:20px; background-color:yellow'>Placeholder</div>");
        }
    };

    public BuildUserInterfaceStep()
    {
        super("build ui");
        this.strategies = new LinkedList<ReificationStrategy<ReificationWidget, StereoTypes>>();
        // order is important! add specific strategies first!

        this.strategies.add(new ToolStripStrategy());
        this.strategies.add(new TriggerStrategy());
        this.strategies.add(new LinkStrategy());
        this.strategies.add(new FormStrategy());
        this.strategies.add(new SelectStrategy());
        this.strategies.add(new PullDownStrategy());

        // containerStack
        this.strategies.add(new ConcurrencyStrategy());
        this.strategies.add(new ChoiceStrategy());
        this.strategies.add(new DeactivationStrategy());

    }

    @Override
    public void execute(final Dialog dialog, final Context context) throws ReificationException
    {
        BuildUserInterfaceVisitor visitor = new BuildUserInterfaceVisitor(context);
        dialog.getInterfaceModel().accept(visitor);
        System.out.println(logger.flush());
        context.set(WIDGET, visitor.root);
        System.out.println("Finished " + getName());
    }


    class BuildUserInterfaceVisitor implements InteractionUnitVisitor
    {
        final Context context;

        BuildUserInterfaceVisitor(final Context context)
        {
            this.context = context;
        }

        ReificationWidget root;
        Stack<ReificationWidget> containerStack = new Stack<ReificationWidget>();

        @Override
        public void startVisit(final Container container)
        {
            logger.start(container);
            ReificationStrategy<ReificationWidget, StereoTypes> strategy = resolve(container);

            if (strategy != null)
            {
                boolean isPrepared = strategy.prepare(container, context);

                ReificationWidget widget = isPrepared ?
                        strategy.reify(container, context) : new BlankWidget(container);

                // stack up myself
                this.containerStack.push(widget);

            }
        }

        @Override
        public void visit(final InteractionUnit interactionUnit)
        {
            logger.start(interactionUnit);

            ReificationStrategy<ReificationWidget, StereoTypes> strategy = resolve(interactionUnit);

            if (strategy != null)
            {
                boolean isPrepared = strategy.prepare(interactionUnit, context);

                ReificationWidget widget = isPrepared ?
                        strategy.reify(interactionUnit, context) : new BlankWidget(interactionUnit);

                assert !this.containerStack.isEmpty() : "Atomic units needs to reside within container";
                this.containerStack.peek().add(widget);

            }

            logger.end(interactionUnit);
        }

        @Override
        public void endVisit(final Container container)
        {
            assert !this.containerStack.isEmpty() : "wrong order of startVisit() / endVisit()";
            logger.end(container);

            ReificationWidget currentContainer = this.containerStack.pop();
            if (containerStack.isEmpty())
            {
                //memorize the final root widget
                root = currentContainer;
            }
            else
            {
                // add to parent
                this.containerStack.peek().add(currentContainer);
            }
        }

        private ReificationStrategy<ReificationWidget, StereoTypes> resolve(InteractionUnit interactionUnit)
        {
            ReificationStrategy<ReificationWidget, StereoTypes> match = null;
            for (ReificationStrategy<ReificationWidget, StereoTypes> strategy : strategies)
            {
                if (strategy.appliesTo(interactionUnit))
                {
                    //System.out.println("Matching strategy "+strategy.getClass()+ " to " + interactionUnit);
                    match = strategy;
                    break;
                }
            }
            return match;
        }
    }
}
