package org.jboss.as.console.client.widgets.forms;

import org.jboss.ballroom.client.widgets.forms.FormItem;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.ModelType;
import org.jboss.dmr.client.Property;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * Adopts DMR to Entity T and vice versa.<p/>
 * Acts as a model bridge from JBoss AS 7 ModelNode representations
 * into a strongly typed model that's consumed by GWT.
 *
 * @author Heiko Braun
 * @date 9/23/11
 */
public class EntityAdapter<T> {

    private Class<?> type;
    private PropertyMetaData metaData;
    private KeyAssignment keyAssignment = null;

    public EntityAdapter(Class<?> type, PropertyMetaData metaData) {
        this.type = type;
        this.metaData = metaData;
    }

    public EntityAdapter<T> with(KeyAssignment keyAssignment)
    {
        this.keyAssignment = keyAssignment;
        return this;
    }

    /**
     * A ModelNode can be either of type <tt>ModelType.Object</tt> or <tt>ModelType.Property</tt>
     * @param dmr  a ModelNode
     * @return an entity representation of type T
     */
    public T fromDMR(ModelNode dmr) {

        String key = null;
        ModelNode actualPayload = null;
        T protoType = (T)metaData.getFactory(type).create();

        if(ModelType.OBJECT.equals(dmr.getType()))
        {
            actualPayload = dmr;
        }
        else if(ModelType.PROPERTY.equals(dmr.getType()))
        {
            final Property property = dmr.asProperty();
            this.keyAssignment = new KeyAssignment() {
                @Override
                public Object valueForKey(String key) {
                    return property.getName();
                }
            };
            actualPayload = property.getValue();
        }
        else
        {
            throw new IllegalArgumentException("Unknown ModelType "+dmr.getType());
        }

        BeanMetaData beanMetaData = metaData.getBeanMetaData(type);
        Mutator mutator = metaData.getMutator(type);

        for(PropertyBinding propBinding : beanMetaData.getProperties())
        {

            Object value = null;

            try
            {

                //System.out.println(propBinding);

                if(propBinding.isKey())
                {
                    if(keyAssignment!=null)
                    {
                        value = keyAssignment.valueForKey(propBinding.getJavaName());
                    }
                    else
                    {
                        throw new IllegalArgumentException("Key property declared, but no key assignment available: "+propBinding);
                    }
                }
                else if("java.lang.Boolean".equals(propBinding.getJavaTypeName()))
                {
                    if(actualPayload.hasDefined(propBinding.getDetypedName()))
                        value = actualPayload.get(propBinding.getDetypedName()).asBoolean();
                    else
                        value = false;
                }
                else if("java.lang.Long".equals(propBinding.getJavaTypeName()))
                {
                    if(actualPayload.hasDefined(propBinding.getDetypedName()))
                        value = actualPayload.get(propBinding.getDetypedName()).asLong();
                    else
                        value = -1;
                }
                else if("java.lang.Integer".equals(propBinding.getJavaTypeName()))
                {
                    if(actualPayload.hasDefined(propBinding.getDetypedName()))
                        value = actualPayload.get(propBinding.getDetypedName()).asInt();
                    else
                        value = -1;
                }
                else if("java.lang.Double".equals(propBinding.getJavaTypeName()))
                {
                    if(actualPayload.hasDefined(propBinding.getDetypedName()))
                        value = actualPayload.get(propBinding.getDetypedName()).asDouble();
                    else
                        value = -1;
                }
                else if("java.lang.Float".equals(propBinding.getJavaTypeName()))
                {
                    if(actualPayload.hasDefined(propBinding.getDetypedName()))
                        value = actualPayload.get(propBinding.getDetypedName()).asDouble();
                    else
                        value = -1;
                }
                else if("java.lang.String".equals(propBinding.getJavaTypeName()))
                {
                    // default
                    if(actualPayload.hasDefined(propBinding.getDetypedName()))
                        value = actualPayload.get(propBinding.getDetypedName()).asString();
                    else
                        value = "";
                }

                // invoke the mutator
                mutator.setValue(protoType, propBinding.getJavaName(), value);


            }
            catch (RuntimeException e)
            {

                System.out.println("Error on property binding: '"+propBinding.toString()+"'");
                System.out.println(dmr);

                throw e;
            }


        }

        return protoType;
    }

    /**
     * Parse a ModelNode of type ModelType.List<p/>
     * Basically calls {@link #fromDMR(org.jboss.dmr.client.ModelNode)} for each item.
     *
     * @param dmr a ModelNode
     * @return a list of entities of type T
     */
    public List<T> fromDMRList(List<ModelNode> dmr) {

        List<T> entities = new LinkedList<T>();

        for(ModelNode item : dmr)
        {
            entities.add(fromDMR(item));
        }

        return entities;
    }

    public ModelNode fromEntity(T entity, String... addressArgs)
    {
        AddressBinding address = metaData.getBeanMetaData(type).getAddress();
        ModelNode operation = address.asProtoType(addressArgs);

        List<PropertyBinding> properties = metaData.getBeanMetaData(type).getProperties();
        Mutator mutator = metaData.getMutator(type);

        for(PropertyBinding property : properties)
        {
            // TODO: How to deal with keys?
            if(property.isKey()) continue;

            Object value = mutator.getValue(entity, property.getJavaName());
            if(value!=null)
            {
                try {
                    operation.get(property.getDetypedName()).set(resolveModelType(property.getJavaTypeName()), value);
                } catch (RuntimeException e) {
                    throw new RuntimeException("Failed to get value "+property.getJavaName(), e);
                }
            }
        }

        return operation;
    }

    private ModelType resolveModelType(String javaTypeName) {

        ModelType type = null;

        if("java.lang.String".equals(javaTypeName))
            type = ModelType.STRING;
        else if("java.lang.Integer".equals(javaTypeName))
            type = ModelType.INT;
        else if("java.lang.Long".equals(javaTypeName))
            type = ModelType.LONG;
        else if("java.lang.Boolean".equals(javaTypeName))
            type = ModelType.BOOLEAN;
        else if("java.lang.Double".equals(javaTypeName))
            type = ModelType.DOUBLE;
        else
            throw new RuntimeException("Failed to resolve ModelType for '"+ javaTypeName+"'");

        return type;
    }

    public List<ModelNode> fromEntityList(List<T> entities)
    {
        return null;
    }


    /**
     * Turns a changeset into a composite write attribute operation.
     *
     * @param prototype a ModelNode that carries the target address
     * @param changeSet
     * @return composite operation
     */
    public ModelNode fromChangeset(ModelNode prototype, Map<String, Object> changeSet)
    {
        prototype.require(ADDRESS);
        prototype.require(OP);

        ModelNode operation = new ModelNode();
        operation.get(OP).set(COMPOSITE);
        operation.get(ADDRESS).setEmptyList();

        List<ModelNode> steps = new ArrayList<ModelNode>();

        List<PropertyBinding> propertyBindings = metaData.getBeanMetaData(type).getProperties();

        for(PropertyBinding binding : propertyBindings)
        {

            Object value = changeSet.get(binding.getJavaName());
            if(value!=null)
            {
                ModelNode step = prototype.clone();
                step.get(NAME).set(binding.getDetypedName());

                Class type = value.getClass();
                if(FormItem.VALUE.class == type) {
                    // if we don't provide a value, it will be persisted as UNDEFINED
                }
                else if(String.class == type)
                {
                    step.get(VALUE).set((String) value);
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

}
