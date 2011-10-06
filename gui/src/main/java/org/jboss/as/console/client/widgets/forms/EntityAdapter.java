package org.jboss.as.console.client.widgets.forms;

import org.jboss.as.console.client.shared.properties.PropertyRecord;
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
    private final  EntityFactory<PropertyRecord> propertyRecordFactory;
    private Class<?> type;
    private PropertyMetaData metaData;
    private KeyAssignment keyAssignment = null;

    public EntityAdapter(Class<?> type, PropertyMetaData metaData) {
        this.type = type;
        this.metaData = metaData;
        this.propertyRecordFactory = metaData.getFactory(PropertyRecord.class);
    }

    public EntityAdapter<T> with(KeyAssignment keyAssignment)
    {
        this.keyAssignment = keyAssignment;
        return this;
    }

    /**
     * A ModelNode can be either of type <tt>ModelType.Object</tt> or <tt>ModelType.Property</tt>.
     * Typically it's just the payload of a DMR response (ModelNode.get(RESULT))
     *
     * @param dmr  a ModelNode
     * @return an entity representation of type T
     */
    public T fromDMR(ModelNode dmr) {

        ModelNode actualPayload = null;
        EntityFactory<?> factory = metaData.getFactory(type);

        if(null==factory)
            throw new IllegalArgumentException("No factory method for " + type);

        T protoType = (T) factory.create();

        KeyAssignment keyDelegation = null;

        if(ModelType.OBJECT.equals(dmr.getType()))
        {
            actualPayload = dmr;
        }
        else if(ModelType.PROPERTY.equals(dmr.getType()))
        {
            final Property property = dmr.asProperty();

            keyDelegation = new KeyAssignment() {
                @Override
                public Object valueForKey(String key) {

                    Object resolvedValue = null;

                    // use delegate
                    if(keyAssignment!=null)
                        resolvedValue = keyAssignment.valueForKey(key);

                    // if delegate fails, fallback to property name
                    if(null==resolvedValue)
                        resolvedValue = property.getName();

                    return resolvedValue;
                }
            };

            actualPayload = property.getValue();
        }
        else
        {
            throw new IllegalArgumentException("Unknown ModelType "+dmr.getType()+": "+dmr);
        }

        BeanMetaData beanMetaData = metaData.getBeanMetaData(type);
        Mutator mutator = metaData.getMutator(type);

        for(PropertyBinding propBinding : beanMetaData.getProperties())
        {

            Object value = null;

            try
            {
                if(propBinding.isKey())
                {
                    // key resolution strategy:
                    // a, external KeyAssignment with fallback to property name (for property types)
                    // b, external KeyAssignment
                    // c, resolution of a matching property
                    // d, failure

                    if(keyDelegation!=null)
                    {
                        value = keyDelegation.valueForKey(propBinding.getJavaName());
                    }
                    else if(keyAssignment!=null)
                    {
                        // typically keys are
                        value = keyAssignment.valueForKey(propBinding.getJavaName());
                    }
                    else if(dmr.hasDefined(propBinding.getDetypedName()))
                    {
                        // keys are required to be strings (part of the address..)
                        value = actualPayload.get(propBinding.getDetypedName()).asString();
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
                else if ("java.util.List".equals(propBinding.getJavaTypeName()))
                {
                    ModelNode list = actualPayload.get(propBinding.getDetypedName());
                    if (actualPayload.hasDefined(propBinding.getDetypedName()) && !list.asList().isEmpty()) {
                        if (list.asList().get(0).getType().equals(ModelType.PROPERTY)) {
                            value = propBinding.getEntityAdapterForList().fromDMRPropertyList(list.asPropertyList());
                        } else {
                            value = propBinding.getEntityAdapterForList().fromDMRList(list.asList());
                        }
                    } 
                    else
                    {
                        value = new LinkedList();
                    }
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
    
    public List<PropertyRecord> fromDMRPropertyList(List<Property> dmr) {
        List<PropertyRecord> entities = new LinkedList<PropertyRecord>();

        for (Property prop : dmr) {
            PropertyRecord property = propertyRecordFactory.create();
            property.setKey(prop.getName());
            property.setValue(prop.getValue().asString());
            entities.add(property);
        }

        return entities;
    }

    /**
     * Create a plain DMR representation of an entity.
     * Plain means w/o the address and operation property.
     *
     * @param entity
     * @return
     */
    public ModelNode fromEntity(T entity)
    {
        ModelNode operation = new ModelNode();
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
                    ModelType modelType = resolveModelType(property.getJavaTypeName());
                    if ((modelType == ModelType.LIST) && (property.getListType() == PropertyBinding.class)) {
                        operation.get(property.getDetypedName()).set(modelType, property.getEntityAdapterForList().fromEntityPropertyList((List)value));
                    } else if (modelType == ModelType.LIST) {
                        operation.get(property.getDetypedName()).set(modelType, property.getEntityAdapterForList().fromEntityList((List)value));
                    } else {
                        operation.get(property.getDetypedName()).set(modelType, value);
                    }
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
        else if("java.util.List".equals(javaTypeName)) {
            type = ModelType.LIST;
        } else {
            throw new RuntimeException("Failed to resolve ModelType for '"+ javaTypeName+"'");
        }
        
        return type;
    }

    /**
     * Creates a composite operation to create entities.
     * Basically calls {@link #fromEntity(Object)}
     *
     * @param entities
     * @return a composite ModelNode structure
     */
    public ModelNode fromEntityList(List<T> entities)
    {
        ModelNode operation = new ModelNode();
        operation.get(OP).set(COMPOSITE);
        operation.get(ADDRESS).setEmptyList();

        List<ModelNode> steps = new ArrayList<ModelNode>();

        for(T entity : entities)
        {
            steps.add(fromEntity(entity));
        }

        operation.get(STEPS).set(steps);
        return operation;
    }
    
    public ModelNode fromEntityPropertyList(List<PropertyRecord> entities) 
    {
        ModelNode propList = new ModelNode();
        for (PropertyRecord prop : entities) {
            propList.add(prop.getKey(), prop.getValue());
        }
        return propList;
    }
    
    /**
     * Turns a changeset into a composite write attribute operation.
     *
     * @param changeSet
     * @param address the entity address
     * @return composite operation
     */
    public ModelNode fromChangeset(Map<String, Object> changeSet, ModelNode address)
    {

        ModelNode protoType = new ModelNode();
        protoType.get(ADDRESS).set(address.get(ADDRESS));
        protoType.get(OP).set(WRITE_ATTRIBUTE_OPERATION);

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
                ModelNode step = protoType.clone();
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
                else if (Long.class == type)
                {
                    step.get(VALUE).set((Long)value);
                }
                else if (Float.class == type)
                {
                    step.get(VALUE).set((Float)value);
                } 
                else if (binding.getListType() != null)
                {
                    if (binding.getListType() == PropertyRecord.class) {
                        step.get(VALUE).set(fromEntityPropertyList((List)value));
                    } else {
                        step.get(VALUE).set(fromEntityList((List)value));
                    }
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
