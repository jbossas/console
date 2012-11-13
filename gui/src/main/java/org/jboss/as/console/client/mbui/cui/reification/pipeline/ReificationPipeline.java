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

import com.allen_sauer.gwt.log.client.Log;
import com.google.inject.Inject;
import org.jboss.as.console.client.mbui.aui.aim.InteractionUnit;
import org.jboss.as.console.client.mbui.cui.Context;

import java.util.LinkedList;
import java.util.List;

import static java.lang.Boolean.TRUE;

/**
 * @author Harald Pehl
 * @date 11/12/2012
 */
public class ReificationPipeline
{
    private final List<ReificationStep> steps;
    private int index;

    @Inject
    public ReificationPipeline(ReadResourceDescriptionStep readResourceDescriptionStep, BuildUserInterfaceStep buildUserInterfaceStep)
    {
        // order is important!
        this.steps = new LinkedList<ReificationStep>();
        this.steps.add(readResourceDescriptionStep);
        this.steps.add(buildUserInterfaceStep);
    }

    public void execute(final InteractionUnit interactionUnit, final Context context, final ReificationCallback outcome)
    {
        this.index = 0;
        for (ReificationStep step : steps)
        {
            step.init(interactionUnit, context);
        }
        executeNext(outcome);
    }

    private void executeNext(final ReificationCallback outcome)
    {
        if (index < steps.size())
        {
            final ReificationStep nextStep = steps.get(index);
            index++;

            nextStep.execute(new ReificationCallback()
            {
                @Override
                public void onFailure(final Throwable caught)
                {
                    Log.error("Failed to execute reification step " + nextStep.getName(), caught);
                }

                @Override
                public void onSuccess(final Boolean result)
                {
                    if (result != null && result.booleanValue())
                    {
                        executeNext(outcome);
                    }
                    else
                    {
                        Log.error("Reification step " + nextStep.getName() + " returned false");
                    }
                }
            });
        }
        outcome.onSuccess(TRUE);
    }
}
