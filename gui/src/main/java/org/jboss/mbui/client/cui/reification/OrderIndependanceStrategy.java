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
package org.jboss.mbui.client.cui.reification;

import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.viewframework.builder.SimpleLayout;
import org.jboss.mbui.client.aui.aim.Container;
import org.jboss.mbui.client.aui.aim.InteractionUnit;
import org.jboss.mbui.client.cui.Context;
import org.jboss.mbui.client.cui.ReificationStrategy;

import static org.jboss.mbui.client.aui.aim.TemporalOperator.OrderIndependance;

/**
 * Strategy for a container with temporal operator == OrderIndependance.
 *
 * @author Harald Pehl
 * @date 11/01/2012
 */
public class OrderIndependanceStrategy implements ReificationStrategy<ReificationWidget>
{
    @Override
    public ReificationWidget reify(final InteractionUnit interactionUnit, final Context context)
    {
        SimpleLayoutAdapter adapter = null;
        if (interactionUnit != null)
        {
            adapter = new SimpleLayoutAdapter(interactionUnit);
        }
        return adapter;
    }

    @Override
    public boolean appliesTo(final InteractionUnit interactionUnit)
    {
        return (interactionUnit instanceof Container) && (((Container) interactionUnit)
                .getTemporalOperator() == OrderIndependance);
    }


    class SimpleLayoutAdapter implements ReificationWidget
    {
        final SimpleLayout layout;
        final InteractionUnit interactionUnit;

        SimpleLayoutAdapter(final  InteractionUnit interactionUnit)
        {
            this.interactionUnit = interactionUnit;
            this.layout = new SimpleLayout().setTitle(interactionUnit.getName()).setHeadline(interactionUnit.getName());
        }

        @Override
        public void add(final ReificationWidget widget, final InteractionUnit interactionUnit,
                final InteractionUnit parent)
        {
            if (widget != null)
            {
                layout.addContent(interactionUnit.getId(), widget.asWidget());
            }
        }

        @Override
        public Widget asWidget()
        {
            return layout.build();
        }
    }
}
