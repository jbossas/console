package org.jboss.mbui.gui.behaviour.as7;

import org.jboss.as.console.client.widgets.forms.KeyAssignment;
import org.jboss.ballroom.client.widgets.forms.FormItem;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.ModelType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

public class ModelNodeAdapter {

    private KeyAssignment keyAssignment = null;

    public ModelNodeAdapter with(KeyAssignment keyAssignment)
    {
        this.keyAssignment = keyAssignment;
        return this;
    }

    /**
     * Turns a changeset into a composite write attribute operation.
     *
     * @param changeSet
     * @param address the entity address
     * @return composite operation
     */
    public ModelNode fromChangeset(Map<String, Object> changeSet, ModelNode address, ModelNode... extraSteps)
    {

        ModelNode protoType = new ModelNode();
        protoType.get(ADDRESS).set(address.get(ADDRESS));
        protoType.get(OP).set(WRITE_ATTRIBUTE_OPERATION);

        ModelNode operation = new ModelNode();
        operation.get(OP).set(COMPOSITE);
        operation.get(ADDRESS).setEmptyList();

        List<ModelNode> steps = new ArrayList<ModelNode>();

        for(String key : changeSet.keySet())
        {
            Object value = changeSet.get(key);

            ModelNode step = protoType.clone();
            step.get(NAME).set(key);

            // set value, including type conversion
            ModelNode valueNode = step.get(VALUE);
            setValue(valueNode, value);

            steps.add(step);
        }

        // add extra steps
        steps.addAll(Arrays.asList(extraSteps));

        operation.get(STEPS).set(steps);

        return operation;
    }

    private void setValue(ModelNode nodeToSetValueUpon, Object value) {
        Class type = value.getClass();

        if(FormItem.VALUE_SEMANTICS.class == type) {

            // skip undefined form item values (FormItem.UNDEFINED.Value)
            // or persist as UNDEFINED
            if(value.equals(FormItem.VALUE_SEMANTICS.UNDEFINED))
            {
                nodeToSetValueUpon.set(ModelType.UNDEFINED);
            }

        }
        else if(String.class == type)
        {

            String stringValue = (String) value;
            if(stringValue.startsWith("$"))     // TODO: further constraints
                nodeToSetValueUpon.setExpression(stringValue);
            else
                nodeToSetValueUpon.set(stringValue);
        }
        else if(Boolean.class == type)
        {
            nodeToSetValueUpon.set((Boolean)value);
        }
        else if(Integer.class == type)
        {
            nodeToSetValueUpon.set((Integer)value);
        }
        else if(Double.class == type)
        {
            nodeToSetValueUpon.set((Double)value);
        }
        else if (Long.class == type)
        {
            nodeToSetValueUpon.set((Long)value);
        }
        else if (Float.class == type)
        {
            nodeToSetValueUpon.set((Float)value);
        }
        else
        {
            throw new RuntimeException("Unsupported type: "+type);
        }
    }

    public static List<String> modelToList(ModelNode model, String elementName)
    {
        List<String> strings = new ArrayList<String>();

        if(model.hasDefined(elementName))
        {
            List<ModelNode> items = model.get(elementName).asList();
            for(ModelNode item : items)
                strings.add(item.asString());

        }

        return strings;
    }

}
