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
import org.jboss.mbui.client.aui.aim.Container;
import org.jboss.mbui.client.aui.aim.InteractionUnit;
import org.jboss.mbui.client.cui.Context;
import org.jboss.mbui.client.cui.ReificationStrategy;

/**
 * @author Harald Pehl
 * @date 10/30/2012
 */
public class TestableReificationStrategy implements ReificationStrategy<ContainerWidget>
{
    public TestableReificationStrategy()
    {
    }

    @Override
    public ContainerWidget reify(final InteractionUnit interactionUnit, final Context context)
    {
        Widget w = interactionUnit instanceof Container ? new TestablePanel() : new Widget();
        // layout data is the only property which does not require the DOM to be present
        w.setLayoutData(interactionUnit.getId());
        return new TestableContainerWidget(w);
    }

    @Override
    public boolean appliesTo(final InteractionUnit interactionUnit)
    {
        return true;
    }
}
