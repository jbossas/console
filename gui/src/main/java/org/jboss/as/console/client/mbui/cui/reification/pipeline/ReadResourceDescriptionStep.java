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

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.mbui.aui.aim.Container;
import org.jboss.as.console.client.mbui.aui.aim.InteractionUnit;
import org.jboss.as.console.client.mbui.aui.mapping.EntityContext;
import org.jboss.as.console.client.mbui.aui.mapping.MappingType;
import org.jboss.as.console.client.mbui.aui.mapping.as7.ResourceMapping;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.widgets.forms.AddressBinding;
import org.jboss.dmr.client.ModelNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.lang.Boolean.TRUE;
import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * @author Harald Pehl
 * @date 11/12/2012
 */
public class ReadResourceDescriptionStep extends ReificationStep
{
    final DispatchAsync dispatcher;
    private Map<String, InteractionUnit> stepReference = new HashMap<String, InteractionUnit>();
    private Set<String> resolvedOperations = new HashSet<String>();

    @Inject
    public ReadResourceDescriptionStep(final DispatchAsync dispatcher)
    {
        super("read resource descriptions");
        this.dispatcher = dispatcher;
    }


    @Override
    public void execute(final AsyncCallback<Boolean> callback)
    {
        ModelNode compsite = new ModelNode();
        compsite.get(OP).set(COMPOSITE);
        compsite.get(ADDRESS).setEmptyList();

        List<ModelNode> steps = new ArrayList<ModelNode>();
        collectOperations(interactionUnit, steps);

        compsite.get(STEPS).set(steps);

        System.out.println(compsite);
        dispatcher.execute(new DMRAction(compsite), new SimpleCallback<DMRResponse>()
        {
            @Override
            public void onSuccess(final DMRResponse result)
            {
                ModelNode modelNode = result.get();
                System.out.println(modelNode);
                callback.onSuccess(TRUE);
            }
        });
    }

    private void collectOperations(final InteractionUnit interactionUnit,
            final List<ModelNode> steps)
    {
        EntityContext entityContext = interactionUnit.getEntityContext();
        if (entityContext.hasMapping(MappingType.RESOURCE))
        {
            ResourceMapping mapping = entityContext.getMapping(MappingType.RESOURCE);
            String resolvedAddress = resolveAddress(interactionUnit, mapping);

            if(!resolvedOperations.contains(resolvedAddress))
            {
                List<String[]> addressTokens = AddressBinding.parseAddressString(resolvedAddress);
                AddressBinding addressBinding = new AddressBinding(addressTokens);

                String[] args = new String[addressBinding.getNumWildCards()];
                for(int i=0; i<addressBinding.getNumWildCards(); i++)
                    args[i] = "*";

                ModelNode descOp = addressBinding.asResource(args);
                descOp.get(OP).set(READ_RESOURCE_DESCRIPTION_OPERATION);
                steps.add(descOp);

                resolvedOperations.add(resolvedAddress);
                stepReference.put("step-"+steps.size(), interactionUnit);
            }
        }

        if(interactionUnit instanceof Container)
        {
            Container container = (Container)interactionUnit;
            for(InteractionUnit child : container.getChildren())
                collectOperations(child, steps);
        }

    }

    private String resolveAddress(final InteractionUnit interactionUnit, final ResourceMapping mapping)
    {
        String address = mapping.getAddress();
        if(null==address)
        {
            address = resolveFromParent(interactionUnit, mapping.getNamespace());
        }
        return address;
    }

    private String resolveFromParent(final InteractionUnit interactionUnit, final String namespace)
    {
        String parentAddress = null;

        InteractionUnit parent = interactionUnit.getParent();
        if(parent!=null)
        {
            if(parent.getEntityContext().hasMapping(MappingType.RESOURCE))
            {
                ResourceMapping parentMapping = parent.getEntityContext().getMapping(MappingType.RESOURCE);
                parentAddress = parentMapping.getAddress();
            }

            if(null==parentAddress)
                parentAddress = resolveFromParent(parent, namespace);
        }

        return parentAddress;
    }
}
