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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.jboss.mbui.gui.reification.Context;
import org.jboss.mbui.model.Dialog;

/**
 * Entry point for reification of an abstract model to a concrete interface. The reification is split up into several
 * {@link ReificationStep}s which are executed synchronously in the given order.
 *
 * @author Harald Pehl
 * @date 11/12/2012
 */
public class ReificationPipeline
{
    private final List<ReificationStep> steps;

    public ReificationPipeline(ReificationStep... steps)
    {
        // order is important!
        this.steps = new LinkedList<ReificationStep>();
        this.steps.addAll(Arrays.asList(steps));
    }

    public void execute(final Dialog dialog, final Context context)
    {
        assert dialog != null : "Dialog must not be null";
        assert context != null : "Context unit must not be null";

        for (ReificationStep step : steps)
        {
            step.execute(dialog, context);
        }
    }
}
