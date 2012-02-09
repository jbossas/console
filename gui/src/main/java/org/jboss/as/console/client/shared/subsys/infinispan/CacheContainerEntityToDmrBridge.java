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
 * Bridget for CacheContainer.
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2011 Red Hat Inc.
 */
public class CacheContainerEntityToDmrBridge extends EntityToDmrBridgeImpl<CacheContainer> {

    public CacheContainerEntityToDmrBridge(ApplicationMetaData propertyMetadata, Class<CacheContainer> type, FrameworkView view,
                                 DispatchAsync dispatcher) {
        super(propertyMetadata, type, view, dispatcher);
    }


    /**
     * Special logic to save transport attributes, which are kept in a special entity called "TRANSPORT".
     */
    @Override
    public void onSaveDetails(CacheContainer entity, Map<String, Object> changedValues, ModelNode... extraSteps) {
        String cacheContainer = entity.getName();
        List<ModelNode> stepsList = new ArrayList<ModelNode>();
        stepsList.addAll(Arrays.asList(extraSteps));

        List<String> removals = new ArrayList<String>();
        for (String javaName : changedValues.keySet()) {
            PropertyBinding binding = formMetaData.findAttribute(javaName);
            String detypedName = binding.getDetypedName();
            if (!detypedName.startsWith("transport")) continue;

            ModelNode writeTransportAttributeStep = new ModelNode();
            writeTransportAttributeStep.get(ADDRESS).add(Baseadress.get())
                                                    .add("subsystem", "infinispan")
                                                    .add("cache-container", cacheContainer)
                                                    .add("transport", "TRANSPORT");
            writeTransportAttributeStep.get(OP).set(WRITE_ATTRIBUTE_OPERATION);
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

        super.onSaveDetails(entity, changedValues, stepsList.toArray(new ModelNode[stepsList.size()]));
    }

}
