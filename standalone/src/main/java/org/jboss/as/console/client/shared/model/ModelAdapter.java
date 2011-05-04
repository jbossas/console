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

package org.jboss.as.console.client.shared.model;

import org.jboss.as.console.client.domain.groups.PropertyRecord;
import org.jboss.as.console.client.domain.model.Jvm;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.widgets.forms.PropertyBinding;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.Property;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * @author Heiko Braun
 * @date 4/27/11
 */
public class ModelAdapter {

    /**
     * Turns a changeset into a composite write attribute operation.
     *
     * @param changeSet
     * @param bindings
     * @return composite operation
     */
    public static ModelNode detypedFromChangeset(ModelNode prototype, Map<String, Object> changeSet, List<PropertyBinding> bindings)
    {
        // pre requesites
        prototype.require(ADDRESS);
        prototype.require(OP);

        ModelNode operation = new ModelNode();
        operation.get(OP).set(COMPOSITE);
        operation.get(ADDRESS).setEmptyList();

        List<ModelNode> steps = new ArrayList<ModelNode>();

        for(PropertyBinding binding : bindings)
        {

            Object value = changeSet.get(binding.getJavaName());
            if(value!=null)
            {
                ModelNode step = prototype.clone();
                step.get(NAME).set(binding.getDetypedName());

                Class type = value.getClass();
                if(String.class == type)
                {
                    step.get(VALUE).set((String)value);
                }
                else if(Boolean.class == type)
                {
                    step.get(VALUE).set((Boolean)value);
                }
                else if(Integer.class == type)
                {
                    step.get(VALUE).set((Integer)value);
                }
                else if(Double.class == type)
                {
                    step.get(VALUE).set((Double)value);
                }
                else
                {
                    throw new RuntimeException("Unsupported type: "+type);
                }

                steps.add(step);
            }
        }


        operation.get(STEPS).set(steps);
        return operation;
    }


    public static List<PropertyRecord> model2Property(BeanFactory factory, ModelNode model) {

        List<PropertyRecord> records = Collections.EMPTY_LIST;

        // System properties
        if(model.hasDefined("system-properties"))
        {
            List<ModelNode> properties = model.get("system-properties").asList();
            records = new ArrayList<PropertyRecord>(properties.size());
            for(ModelNode item : properties)
            {
                Property property = item.asProperty();
                PropertyRecord propRecord = factory.property().as();
                propRecord.setKey(property.getName());
                ModelNode value = property.getValue();
                propRecord.setValue(value.get("value").asString());
                propRecord.setBootTime(value.get("boot-time").asBoolean());
                records.add(propRecord);
            }
        }

        return records;

    }

    public static Jvm model2JVM(BeanFactory factory, ModelNode model) {

        Jvm jvm = null;

        try {
            if(model.has("jvm") && model.get("jvm").isDefined())
            {
                jvm = factory.jvm().as();
                Property jvmProp = model.get("jvm").asProperty();
                jvm.setName(jvmProp.getName());

                ModelNode jvmPropValue = jvmProp.getValue();

                if(jvmPropValue.hasDefined("heap-size"))
                    jvm.setHeapSize(jvmPropValue.get("heap-size").asString());

                if(jvmPropValue.hasDefined("max-heap-size"))
                    jvm.setMaxHeapSize(jvmPropValue.get("max-heap-size").asString());

                if(jvmPropValue.hasDefined("debug-enabled"))
                    jvm.setDebugEnabled(jvmPropValue.get("debug-enabled").asBoolean());


            }
        } catch (IllegalArgumentException e) {
            // TODO: properly deal with the different representations
        }

        return jvm;
    }
}
