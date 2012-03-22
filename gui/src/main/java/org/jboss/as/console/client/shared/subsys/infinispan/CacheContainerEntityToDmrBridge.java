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
        Boolean hasTransport = (Boolean)changedValues.remove("hasTransport");
        boolean removingTransport = (hasTransport != null) && !hasTransport;
        boolean addingTransport = (hasTransport != null) && hasTransport;
        boolean modifyingTransport = (hasTransport == null) && entity.isHasTransport();

        if (addingTransport && !isStackSetOnAdd(changedValues)) {
            return;
        }

        String cacheContainer = entity.getName();
        List<ModelNode> stepsList = new ArrayList<ModelNode>();
        stepsList.addAll(Arrays.asList(extraSteps));

        if (addingTransport) {
            addTransport(cacheContainer, changedValues, stepsList);
        }

        if (removingTransport) {
            removeTransport(cacheContainer, stepsList);
        }

        if (!removingTransport && (addingTransport || modifyingTransport)) {
            handleWriteTransportAttributes(cacheContainer, changedValues, stepsList);
        }

        removeTransportAttribs(changedValues);

        super.onSaveDetails(entity, changedValues, stepsList.toArray(new ModelNode[stepsList.size()]));
    }

    // remove transport attributes from normal processing
    private void removeTransportAttribs(Map<String, Object> changedValues) {
        List<String> removals = new ArrayList<String>();
        for (String javaName : changedValues.keySet()) {
            if (isTransportAttribute(javaName)) removals.add(javaName);
        }

        // avoid ConcurrentModificationException
        for (String javaName : removals) {
            changedValues.remove(javaName);
        }
    }

    private boolean isTransportAttribute(String javaName) {
        PropertyBinding binding = formMetaData.findAttribute(javaName);
        String detypedName = binding.getDetypedName();
        return detypedName.startsWith("transport");
    }

    // if adding transport, see if required stack param was set
    private boolean isStackSetOnAdd(Map<String, Object> changedValues) {
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
        for (String javaName : changedValues.keySet()) {
            if (!isTransportAttribute(javaName)) continue;

            PropertyBinding binding = formMetaData.findAttribute(javaName);
            String detypedName = binding.getDetypedName();
            ModelNode writeTransportAttributeStep = makeTransportOperation(WRITE_ATTRIBUTE_OPERATION, cacheContainer);
            writeTransportAttributeStep.get(NAME).set(detypedName.substring(detypedName.lastIndexOf("/") + 1));
            writeTransportAttributeStep.get(VALUE).set(changedValues.get(javaName).toString());
            stepsList.add(writeTransportAttributeStep);
        }
    }

    private void addTransport(String cacheContainer, Map<String, Object> changedValues, List<ModelNode> stepsList) {
        ModelNode addTransport = makeTransportOperation(ADD, cacheContainer);
        addTransport.get("stack").set(changedValues.remove("stack").toString());
        stepsList.add(addTransport);
    }

    private void removeTransport(String cacheContainer, List<ModelNode> stepsList) {
        ModelNode removeTransport = makeTransportOperation(REMOVE, cacheContainer);
        stepsList.add(removeTransport);
    }

}
