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
import org.jboss.mbui.gui.behaviour.StatementContext;
import org.jboss.mbui.gui.reification.Context;
import org.jboss.mbui.gui.reification.ContextKey;
import org.jboss.mbui.model.Dialog;
import org.jboss.mbui.model.behaviour.Resource;
import org.jboss.mbui.model.behaviour.ResourceType;
import org.jboss.mbui.model.mapping.as7.AddressMapping;
import org.jboss.mbui.model.mapping.as7.DMRMapping;
import org.jboss.mbui.model.structure.Container;
import org.jboss.mbui.model.structure.InteractionUnit;
import org.jboss.mbui.model.structure.QName;
import org.jboss.mbui.model.structure.Trigger;
import org.jboss.mbui.model.structure.as7.StereoTypes;
import org.jboss.mbui.model.structure.impl.InteractionUnitVisitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;
import static org.jboss.mbui.model.mapping.MappingType.DMR;

/**
 *
 * Reads the operation meta data associated to {@link Trigger} units.
 *
 * @author Heiko Braun
 * @date 11/12/2012
 */
public class ReadOperationDescriptions extends ReificationPreperation
{
    final DispatchAsync dispatcher;
    private static final QName RESOURCE_OP = QName.valueOf("org.jboss.as:resource-operation");

    public ReadOperationDescriptions(final DispatchAsync dispatcher)
    {
        super("read operation description");
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

                    if (!context.has(ContextKey.OPERATION_DESCRIPTIONS))
                    {
                        context.set(ContextKey.OPERATION_DESCRIPTIONS, new HashMap<QName, ModelNode>());
                    }

                    Resource<ResourceType> output = visitor.stepReference.get(step);
                    ModelNode operationMetaData = stepResponse.get(RESULT);
                    context.<Map>get(ContextKey.OPERATION_DESCRIPTIONS).put(output.getId(), operationMetaData);

                    //System.out.println(output.getId() + " > " + operationMetaData);
                }
                callback.onSuccess();
            }
        });

    }


    class CollectOperationsVisitor implements InteractionUnitVisitor<StereoTypes>
    {
        final Context context;
        List<ModelNode> steps = new ArrayList<ModelNode>();
        Set<QName> resolvedOperations = new HashSet<QName>();
        Map<String, Resource<ResourceType>> stepReference = new HashMap<String, Resource<ResourceType>>();

        public CollectOperationsVisitor(final Context context)
        {
            this.context = context;
        }

        @Override
        public void startVisit(final Container container)
        {
            // ignore
        }

        @Override
        public void visit(final InteractionUnit<StereoTypes> interactionUnit)
        {
            if(interactionUnit instanceof Trigger
                    && interactionUnit.doesProduce())
                addStep((Trigger) interactionUnit);
        }

        @Override
        public void endVisit(final Container container)
        {
            // noop
        }

        private void addStep(Trigger<StereoTypes> interactionUnit)
        {
            final StatementContext delegate = context.get(ContextKey.STATEMENTS);
            assert delegate != null : "StatementContext not provided";
            assert interactionUnit.doesProduce();

            Resource<ResourceType> output = interactionUnit.getOutputs().iterator().next();

            // skip unqualified trigger that don't point to a resource operation
            if(!output.getId().equalsIgnoreSuffix(RESOURCE_OP))
                return;

            String operationName = output.getId().getSuffix();
            if(operationName==null)
                throw new IllegalArgumentException("Illegal operation name mapping: "+ output.getId()+ " (suffix required)");

            DMRMapping mapping = interactionUnit.findMapping(DMR);
            String address = mapping.getAddress();

            if (!resolvedOperations.contains(output.getId()))
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
                op.get(OP).set(READ_OPERATION_DESCRIPTION_OPERATION);
                op.get(NAME).set(operationName);

                steps.add(op);

                resolvedOperations.add(output.getId());
                stepReference.put("step-" + steps.size(), output);
            }

        }
    }
}
