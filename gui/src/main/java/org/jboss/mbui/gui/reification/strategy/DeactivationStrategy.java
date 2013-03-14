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

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import org.jboss.mbui.gui.reification.Context;
import org.jboss.mbui.gui.reification.ContextKey;
import org.jboss.mbui.model.structure.Container;
import org.jboss.mbui.model.structure.InteractionUnit;

import static org.jboss.mbui.model.structure.TemporalOperator.Deactivation;

/**
 * Strategy for a container with temporal operator == Deactivation.
 *
 * @author Harald Pehl
 * @author Heiko Braun
 * @date 11/01/2012
 */
public class DeactivationStrategy implements ReificationStrategy<ReificationWidget>
{

    @Override
    public boolean prepare(InteractionUnit interactionUnit, Context context) {
        return false;
    }

    @Override
    public ReificationWidget reify(final InteractionUnit interactionUnit, final Context context)
    {

        EventBus eventBus = context.get(ContextKey.EVENTBUS);
        assert eventBus!=null : "Coordinator bus is required to execute FormStrategy";

        MyAdapter adapter = null;
        if (interactionUnit != null)
        {
            adapter = new MyAdapter(eventBus, interactionUnit);
        }
        return adapter;
    }

    @Override
    public boolean appliesTo(final InteractionUnit interactionUnit)
    {
        return (interactionUnit instanceof Container) && (((Container) interactionUnit)
                .getTemporalOperator() == Deactivation);
    }


    class MyAdapter  implements ReificationWidget
    {
        final InteractionUnit interactionUnit;

        MyAdapter(final EventBus eventBus, final InteractionUnit interactionUnit)
        {

            this.interactionUnit = interactionUnit;

        }

        @Override
        public InteractionUnit getInteractionUnit() {
            return interactionUnit;
        }

        @Override
        public void add(final ReificationWidget widget)
        {

        }

        @Override
        public Widget asWidget()
        {
            return new HTML("TBD");
        }
    }
}
