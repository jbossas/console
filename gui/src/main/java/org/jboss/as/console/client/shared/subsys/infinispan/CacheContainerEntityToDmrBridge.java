/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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
package org.jboss.as.console.client.shared.subsys.infinispan;

import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.infinispan.model.CacheContainer;
import org.jboss.as.console.client.shared.viewframework.EntityToDmrBridgeImpl;
import org.jboss.as.console.client.shared.viewframework.FrameworkView;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.as.console.client.widgets.forms.PropertyBinding;
import org.jboss.dmr.client.ModelNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * Bridge for CacheContainer.
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2011 Red Hat Inc.
 */
public class CacheContainerEntityToDmrBridge extends EntityToDmrBridgeImpl<CacheContainer> {

    public CacheContainerEntityToDmrBridge(ApplicationMetaData propertyMetadata, Class<CacheContainer> type, FrameworkView view,
                                 DispatchAsync dispatcher) {
        super(propertyMetadata, type, view, dispatcher);
    }

    @Override
    // set transport flag before view is refreshed
    protected void refreshView(ModelNode response) {
        for (CacheContainer cacheContainer : this.entityList) {
            cacheContainer.setHasTransport(response.get("result", cacheContainer.getName(), "transport").isDefined());
        }

        super.refreshView(response);
    }


    /**
     * Special logic to save transport attributes, which are kept in a special entity called "TRANSPORT".
     */
    @Override
    public void onSaveDetails(CacheContainer entity, Map<String, Object> changedValues, ModelNode... extraSteps) {
        if (!isStackSetOnAdd(changedValues)) {
            super.onSaveDetails(entity, changedValues, extraSteps);
            return;
        }

        String cacheContainer = entity.getName();
        List<ModelNode> stepsList = new ArrayList<ModelNode>();
        stepsList.addAll(Arrays.asList(extraSteps));

        handleAddRemoveTransport(cacheContainer, changedValues, stepsList);

        Boolean hasTransport = (Boolean)changedValues.remove("hasTransport");
        if ((hasTransport != null) && (hasTransport)) {
            handleWriteTransportAttributes(cacheContainer, changedValues, stepsList);
        }

        super.onSaveDetails(entity, changedValues, stepsList.toArray(new ModelNode[stepsList.size()]));
    }

    // if adding transport, see if required stack param was set
    private boolean isStackSetOnAdd(Map<String, Object> changedValues) {
        Boolean addTransport = (Boolean)changedValues.get("hasTransport");
        if (addTransport == null) return true;
        if (!addTransport) return true;

        if (!changedValues.containsKey("stack")) {
            Console.error("Stack is required when defining the transport.");
            changedValues.remove("hasTransport");
            return false;
        }

        return true;
    }

    private ModelNode makeTransportOperation(String operation, String cacheContainer) {
        ModelNode transportOperation = new ModelNode();
        transportOperation.get(ADDRESS).set(Baseadress.get())
                                       .add("subsystem", "infinispan")
                                       .add("cache-container", cacheContainer)
                                       .add("transport", "TRANSPORT");
        transportOperation.get(OP).set(operation);
        return transportOperation;
    }

    private void handleWriteTransportAttributes(String cacheContainer, Map<String, Object> changedValues, List<ModelNode> stepsList) {
        List<String> removals = new ArrayList<String>();
        for (String javaName : changedValues.keySet()) {
            PropertyBinding binding = formMetaData.findAttribute(javaName);
            String detypedName = binding.getDetypedName();
            if (!detypedName.startsWith("transport")) continue;

            ModelNode writeTransportAttributeStep = makeTransportOperation(WRITE_ATTRIBUTE_OPERATION, cacheContainer);
            writeTransportAttributeStep.get(NAME).set(detypedName.substring(detypedName.lastIndexOf("/") + 1));
            writeTransportAttributeStep.get(VALUE).set(changedValues.get(javaName).toString());
            stepsList.add(writeTransportAttributeStep);
            removals.add(javaName);
        }

        // Need to remove transport attributes from normal processing.
        // Remove outside main loop to avoid ConcurrentModificationException
        for (String javaName: removals) {
            changedValues.remove(javaName);
        }
    }

    private void handleAddRemoveTransport(String cacheContainer, Map<String, Object> changedValues, List<ModelNode> stepsList) {
        if (!changedValues.containsKey("hasTransport")) return;

        // if hasTransport was toggled then we must be adding or removing it
        ModelNode addOrRemoveTransport = null;
        Boolean addTransport = (Boolean)changedValues.get("hasTransport");
        if (addTransport) {
            addOrRemoveTransport = makeTransportOperation(ADD, cacheContainer);
            addOrRemoveTransport.get("stack").set(changedValues.remove("stack").toString());
        } else {
            addOrRemoveTransport = makeTransportOperation(REMOVE, cacheContainer);
        }

        stepsList.add(addOrRemoveTransport);
    }

}
