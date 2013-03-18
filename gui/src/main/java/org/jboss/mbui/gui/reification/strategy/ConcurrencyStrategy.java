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
package org.jboss.mbui.gui.reification.strategy;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.layout.SimpleLayout;
import org.jboss.mbui.gui.reification.Context;
import org.jboss.mbui.model.structure.Container;
import org.jboss.mbui.model.structure.InteractionUnit;

import static org.jboss.mbui.model.structure.TemporalOperator.Concurrency;

/**
 * Strategy for a container with temporal operator Concurrency.
 *
 * @author Harald Pehl
 * @author Heiko Braun
 * @date 11/01/2012
 */
public class ConcurrencyStrategy implements ReificationStrategy<ReificationWidget>
{
    @Override
    public boolean prepare(InteractionUnit interactionUnit, Context context) {
        return true;
    }

    @Override
    public ReificationWidget reify(final InteractionUnit interactionUnit, final Context context)
    {
        SimpleLayoutAdapter adapter = null;
        if (interactionUnit != null)
        {
            adapter = new SimpleLayoutAdapter(interactionUnit, context);
        }
        return adapter;
    }

    @Override
    public boolean appliesTo(final InteractionUnit interactionUnit)
    {
        return (interactionUnit instanceof Container) && (((Container) interactionUnit)
                .getTemporalOperator() == Concurrency);
    }


    class SimpleLayoutAdapter implements ReificationWidget
    {
        final TabPanelContract delegate;
        final InteractionUnit interactionUnit;

        SimpleLayoutAdapter(final InteractionUnit interactionUnit, Context context)
        {
            this.interactionUnit = interactionUnit;

            if(interactionUnit.hasParent())
            {
                final VerticalPanel panel = new VerticalPanel();
                panel.setStyleName("fill-layout-width");
                this.delegate = new TabPanelContract() {
                    @Override
                    public void add(InteractionUnit unit, Widget widget) {
                        panel.add(widget);
                    }

                    @Override
                    public Widget as() {
                        return panel;
                    }
                };
            }
            else
            {
                final SimpleLayout builder = new SimpleLayout()
                        .setTitle(interactionUnit.getLabel()
                        );

                this.delegate = new TabPanelContract() {
                    @Override
                    public void add(InteractionUnit unit, Widget widget) {
                        builder.addContent("TODO: NAME", widget);
                    }

                    @Override
                    public Widget as() {
                        return builder.build();
                    }
                };
            }

        }

        @Override
        public InteractionUnit getInteractionUnit() {
            return interactionUnit;
        }

        @Override
        public void add(final ReificationWidget widget)
        {

            if (widget!= null)
            {
                delegate.add(widget.getInteractionUnit(), widget.asWidget());
            }
        }

        @Override
        public Widget asWidget()
        {
            return delegate.as();
        }
    }
}
