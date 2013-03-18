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
package org.jboss.mbui.gui.reification.preparation;

import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.dmr.client.dispatch.DispatchAsync;
import org.jboss.dmr.client.dispatch.impl.DMRAction;
import org.jboss.dmr.client.dispatch.impl.DMRResponse;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.ModelType;
import org.jboss.mbui.gui.behaviour.StatementContext;
import org.jboss.mbui.gui.reification.Context;
import org.jboss.mbui.gui.reification.ContextKey;
import org.jboss.mbui.model.Dialog;
import org.jboss.mbui.model.mapping.Predicate;
import org.jboss.mbui.model.mapping.as7.AddressMapping;
import org.jboss.mbui.model.mapping.as7.DMRMapping;
import org.jboss.mbui.model.structure.Container;
import org.jboss.mbui.model.structure.InteractionUnit;
import org.jboss.mbui.model.structure.impl.InteractionUnitVisitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;
import static org.jboss.mbui.gui.reification.ContextKey.MODEL_DESCRIPTIONS;
import static org.jboss.mbui.model.mapping.MappingType.DMR;

/**
 * TODO Implement caching for resource descriptions (memory, local storage, ...)
 *
 * @author Harald Pehl
 * @date 11/12/2012
 */
public class ReadResourceDescription extends ReificationPreperation
{
    final DispatchAsync dispatcher;

    public ReadResourceDescription(final DispatchAsync dispatcher)
    {
        super("read resource description");
        this.dispatcher = dispatcher;
    }

    @Override
    public void prepare(final Dialog dialog, final Context context)
    {
        throw new UnsupportedOperationException("Only async preparation is supported");
    }

    @Override
    public void prepareAsync(final Dialog dialog, final Context context, final Callback callback)
    {
        assert dialog != null && context != null && callback != null : "Interaction unit, context and callback must be present";

        final CollectOperationsVisitor visitor = new CollectOperationsVisitor(context);
        dialog.getInterfaceModel().accept(visitor);

        ModelNode compsite = new ModelNode();
        compsite.get(OP).set(COMPOSITE);
        compsite.get(ADDRESS).setEmptyList();
        compsite.get(STEPS).set(visitor.steps);

        dispatcher.execute(new DMRAction(compsite), new SimpleCallback<DMRResponse>()
        {
            @Override
            public void onFailure(final Throwable caught)
            {
                callback.onError(caught);
            }

            @Override
            public void onSuccess(final DMRResponse result)
            {
                ModelNode response = result.get();

                // evaluate step responses
                for (String step : visitor.stepReference.keySet())
                {
                    ModelNode stepResponse = response.get(RESULT).get(step);

                    // might be a LIST response type (resource=*:read-resource-description)
                    ModelNode description = ModelType.LIST == stepResponse.get(RESULT).getType() ?
                            stepResponse.get(RESULT).asList().get(0).get(RESULT).asObject() :
                            stepResponse.get(RESULT).asObject();


                    if (!context.has(MODEL_DESCRIPTIONS))
                    {
                        context.set(MODEL_DESCRIPTIONS, new HashMap<String, ModelNode>());
                    }
                    DMRMapping mapping = (DMRMapping) visitor.stepReference.get(step).findMapping(DMR);
                    context.<Map>get(MODEL_DESCRIPTIONS).put(mapping.getNamespace(), description);
                }
                callback.onSuccess();
            }
        });
    }


    class CollectOperationsVisitor implements InteractionUnitVisitor
    {
        final Context context;
        List<ModelNode> steps = new ArrayList<ModelNode>();
        Set<String> resolvedAdresses = new HashSet<String>();
        Map<String, InteractionUnit> stepReference = new HashMap<String, InteractionUnit>();

        public CollectOperationsVisitor(final Context context)
        {
            this.context = context;
        }

        @Override
        public void startVisit(final Container container)
        {
            addStep(container);
        }

        @Override
        public void visit(final InteractionUnit interactionUnit)
        {
            addStep(interactionUnit);
        }

        @Override
        public void endVisit(final Container container)
        {
            // noop
        }

        private void addStep(InteractionUnit interactionUnit)
        {
            final StatementContext delegate = context.get(ContextKey.STATEMENTS);
            assert delegate != null : "StatementContext not provided";

            DMRMapping mapping = (DMRMapping) interactionUnit.findMapping(DMR, new Predicate<DMRMapping>()
            {
                @Override
                public boolean appliesTo(final DMRMapping candidate)
                {
                    // the read-resource operation only needs the address of a resource
                    // hence we can skip mapping without address declarations (i.e. just attributes)
                    return candidate.getAddress() != null;
                }
            });
            if (mapping != null)
            {
                String address = mapping.getAddress();
                if (!resolvedAdresses.contains(address))
                {
                    AddressMapping addressMapping = AddressMapping.fromString(address);
                    ModelNode op = addressMapping.asResource(new StatementContext()
                    {
                        @Override
                        public String resolve(String key)
                        {
                            // fallback strategy for values that are created at runtime, i.e. datasource={selected.entity}
                            String resolved = delegate.resolve(key);
                            if (null == resolved) { resolved = "*"; }
                            return resolved;
                        }

                        @Override
                        public String[] resolveTuple(String key)
                        {
                            return delegate.resolveTuple(key);
                        }
                    });
                    op.get(OP).set(READ_RESOURCE_DESCRIPTION_OPERATION);
                    steps.add(op);

                    resolvedAdresses.add(address);
                    stepReference.put("step-" + steps.size(), interactionUnit);
                }
            }
        }
    }
}
