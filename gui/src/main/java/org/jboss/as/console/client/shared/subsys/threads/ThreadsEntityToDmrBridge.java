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
package org.jboss.as.console.client.shared.subsys.threads;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.viewframework.EntityToDmrBridgeImpl;
import org.jboss.as.console.client.shared.viewframework.FrameworkView;
import org.jboss.as.console.client.shared.viewframework.NamedEntity;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.as.console.client.widgets.forms.PropertyBinding;
import org.jboss.dmr.client.ModelNode;

import static org.jboss.dmr.client.ModelDescriptionConstants.ADDRESS;
import static org.jboss.dmr.client.ModelDescriptionConstants.NAME;
import static org.jboss.dmr.client.ModelDescriptionConstants.OP;
import static org.jboss.dmr.client.ModelDescriptionConstants.VALUE;
import static org.jboss.dmr.client.ModelDescriptionConstants.WRITE_ATTRIBUTE_OPERATION;

/**
 * There are certain thread attributes that must be saved as pairs.  This class overrides
 * onSaveDetails to remove those attributes from the changedValues and then create
 * custom steps for saving the attributes as pairs.
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2011 Red Hat Inc.
 */
public class ThreadsEntityToDmrBridge<T extends NamedEntity> extends EntityToDmrBridgeImpl<T> {

    private String[][] attributePairs = {
        {"keepaliveTimeout", "keepaliveTimeoutUnit"},
        {"maxThreadsCount", "maxThreadsPerCPU"},
        {"queueLengthCount", "queueLengthPerCPU"},
        {"coreThreadsCount", "coreThreadsPerCPU"}
    };
    
    public ThreadsEntityToDmrBridge(ApplicationMetaData propertyMetaData, Class type, FrameworkView view, DispatchAsync dispatcher) {
        super(propertyMetaData, type, view, dispatcher);
    }

    @Override
    public void onSaveDetails(T entity, Map<String, Object> changedValues, ModelNode... extraSteps) {
        Set<ModelNode> extraStepSet = new HashSet<ModelNode>();
        
        for (String[] attribPair : attributePairs) {
            ModelNode extraStep = createStep(entity, attribPair[0], changedValues.remove(attribPair[0]), 
                                                     attribPair[1], changedValues.remove(attribPair[1]));
            if (extraStep != null) extraStepSet.add(extraStep);
        }
        
        super.onSaveDetails(entity, changedValues, extraStepSet.toArray(new ModelNode[extraStepSet.size()]));
    }
    
    private ModelNode createStep(T entity, String name1, Object value1, String name2, Object value2) {
        if ((value1 == null) && (value2 == null)) return null;
        
        PropertyBinding propBinding1 = formMetaData.findAttribute(name1);
        String[] splitDetypedName1 = propBinding1.getDetypedName().split("/");
        value1 = editedOrCurrentValue(entity, name1, value1);
        
        PropertyBinding propBinding2 = formMetaData.findAttribute(name2);
        String[] splitDetypedName2 = propBinding2.getDetypedName().split("/");
        value2 = editedOrCurrentValue(entity, name2, value2);
        
        ModelNode valueNode = new ModelNode();
        setValueOnNode(valueNode.get(splitDetypedName1[1]), value1);
        setValueOnNode(valueNode.get(splitDetypedName2[1]), value2);

        ModelNode step = protoType(entity, splitDetypedName1[0]);
        step.get(VALUE).set(valueNode);
        
        return step;
    }
    
    private void setValueOnNode(ModelNode node, Object value) {
        Class type = value.getClass();
        if (type == String.class) {
            node.set((String)value);
        } else if (type == Long.class) {
            node.set((Long)value);
        } else if (type == Integer.class) {
            node.set((Integer)value);
        } else {
            throw new IllegalArgumentException("Unable to resolve type=" + type);
        }
    }
    
    private Object editedOrCurrentValue(NamedEntity entity, String name, Object value) {
        // if value was changed it will not be null, so return the newly edited value
        if (value != null) return value;
        
        // if value was not changed, return its current value
        T instance = findEntity(entity.getName());
        Object returnValue = this.propertyMetadata.getMutator(type).getter(name).invoke(instance);

        // if there is no current value, use the default
        if ((returnValue == null) || returnValue.equals("") ||
           ((returnValue instanceof Number) && (((Number)returnValue).longValue() < 0)) ) {
            returnValue = this.formMetaData.findAttribute(name).getDefaultValue();
        }
        
        return returnValue;
    }
    
    private ModelNode protoType(NamedEntity entity, String attributeName) {
       String entityName = entity.getName();
       ModelNode protoType = new ModelNode();
       protoType.get(ADDRESS).set(getResourceAddress(entityName).get(ADDRESS));
       protoType.get(OP).set(WRITE_ATTRIBUTE_OPERATION);
       protoType.get(NAME).set(attributeName);
       return protoType;
    }
}
