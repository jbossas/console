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

import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.viewframework.builder.SimpleLayout;
import org.jboss.mbui.client.aui.aim.Container;
import org.jboss.mbui.client.aui.aim.InteractionUnit;
import org.jboss.mbui.client.cui.Context;
import org.jboss.mbui.client.cui.ReificationStrategy;

import java.util.Collections;
import java.util.Iterator;

import static org.jboss.mbui.client.aui.aim.TemporalOperator.OrderIndependance;

/**
 * Strategy for a container with temporal operator == OrderIndependance.
 *
 * @author Harald Pehl
 * @date 11/01/2012
 */
public class OrderIndependanceStrategy implements ReificationStrategy<ContainerWidget>
{
    static int counter = 0;

    @Override
    public ContainerWidget reify(final InteractionUnit interactionUnit, final Context context)
    {
        SimpleLayoutAdapter adapter = null;
        if (interactionUnit != null)
        {
            adapter = new SimpleLayoutAdapter();
            adapter.layout.setTitle(interactionUnit.getName());
            adapter.layout.setHeadline(interactionUnit.getName());
        }
        return adapter;
    }

    @Override
    public boolean appliesTo(final InteractionUnit interactionUnit)
    {
        return (interactionUnit instanceof Container) && (((Container) interactionUnit)
                .getTemporalOperator() == OrderIndependance);
    }


    class SimpleLayoutAdapter implements ContainerWidget
    {
        final SimpleLayout layout;

        SimpleLayoutAdapter()
        {
            this.layout = new SimpleLayout();
        }

        @Override
        public void add(final Widget w)
        {
            if (w != null)
            {
                layout.addContent("child-" + counter, w);
                counter++;
            }
        }

        @Override
        public void clear()
        {
        }

        @Override
        public Iterator<Widget> iterator()
        {
            return Collections.<Widget>emptyList().iterator();
        }

        @Override
        public boolean remove(final Widget w)
        {
            return false;
        }

        @Override
        public Widget asWidget()
        {
            return layout.build();
        }
    }
}
