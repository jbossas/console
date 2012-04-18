package org.jboss.as.console.client.widgets.forms;

import com.allen_sauer.gwt.log.client.Log;
import org.jboss.as.console.client.shared.expr.ExpressionAdapter;
import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.jboss.ballroom.client.widgets.forms.FormItem;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.ModelType;
import org.jboss.dmr.client.Property;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
 * @author Stan Silvert
 * @date 9/23/11
 */
public class EntityAdapter<T> {


    private final  EntityFactory<PropertyRecord> propertyRecordFactory;
    private Class<?> type;
    private ApplicationMetaData metaData;
    private KeyAssignment keyAssignment = null;

    public EntityAdapter(Class<?> type, ApplicationMetaData metaData) {
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
     * Determine if this is an EntityAdapter for a one of the supported ModelNode
     * base classes (String, Long, BigDecimal, etc).
     *
     * @return <code>true</code> if this is a base class EntityAdapter, <code>false</code> otherwise.
     */
    public boolean isBaseTypeAdapter() {
        return isBaseType(this.type);
    }

    /**
     * Is the given class one of the supported ModelNode base classes (String, Long, BigDecimal, etc.)
     *
     * @param clazz The class to be tested.
     * @return <code>true</code> if the given class is a base class, <code>false</code> otherwise.
     */
    public boolean isBaseType(Class<?> clazz) {
        return (clazz == String.class) ||
                (clazz == Long.class) ||
                (clazz == Integer.class) ||
                (clazz == Boolean.class) ||
                (clazz == Double.class) ||
                (clazz == BigDecimal.class) ||
                (clazz == byte[].class);
    }

    private T convertToBaseType(ModelNode dmr) {
        if (type == String.class) return (T)dmr.asString();
        if (type == Long.class) return (T)Long.valueOf(dmr.asLong());
        if (type == Integer.class) return (T)Integer.valueOf(dmr.asInt());
        if (type == Boolean.class) return (T)Boolean.valueOf(dmr.asBoolean());
        if (type == Double.class) return (T)Double.valueOf(dmr.asDouble());
        if (type == BigDecimal.class) return (T)BigDecimal.valueOf(dmr.asDouble());
        if (type == byte[].class) return (T)dmr.asBytes();

        throw new IllegalArgumentException("Can not convert. This node is not of a base type. Actual type is " + type.getName());
    }

    /**
     * The ModelNode can be of any type supported by ModelType except BigInteger.
     * Typically it's just the payload of a DMR response (ModelNode.get(RESULT))
     *
     * @param dmr  a ModelNode
     * @return an entity representation of type T
     */
    public T fromDMR(ModelNode dmr) {
        dmr = dmr.clone(); // don't want our dmr.get() calls to have side effects TODO: necessary?

        if (isBaseTypeAdapter()) return convertToBaseType(dmr);

        ModelNode actualPayload = null;
        EntityFactory<?> factory = metaData.getFactory(type);

        if(null==factory)
            throw new IllegalArgumentException("No factory method for " + type);

        T entity = (T) factory.create();

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

            String[] splitDetypedName = propBinding.getDetypedName().split("/");
            ModelNode propValue = actualPayload.get(splitDetypedName);
            Object value = null;

            try
            {


                /**
                 * EXPRESSIONS
                 */

                if(propBinding.doesSupportExpression())
                {
                    if(propValue.isDefined()
                            && propValue.getType() == ModelType.EXPRESSION)
                    {
                        String exprValue = actualPayload.get(propBinding.getDetypedName()).asString();

                        ExpressionAdapter.setExpressionValue(entity, propBinding.getJavaName(), exprValue);

                        continue; // expression have precedence over real values

                    }
                }

                /**
                 * KEYS
                 */

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
                        Log.warn("Key property declared, but no key assignment available: " + propBinding);
                    }
                }

                /**
                 * VALUES
                 */

                else if("java.lang.Boolean".equals(propBinding.getJavaTypeName()))
                {
                    if(propValue.isDefined())
                        value = propValue.asBoolean();
                    else
                        value = false;
                }
                else if("java.lang.Long".equals(propBinding.getJavaTypeName()))
                {
                    if(propValue.isDefined())
                        value = propValue.asLong();
                    else
                        // need to make sure to use the proper type otherwise ClassCastExceptions occur down the line (after boxing)
                        value = -1L;
                }
                else if("java.lang.Integer".equals(propBinding.getJavaTypeName()))
                {
                    if(propValue.isDefined())
                        value = propValue.asInt();
                    else
                        value = -1;
                }
                else if("java.lang.Double".equals(propBinding.getJavaTypeName()))
                {
                    if(propValue.isDefined())
                        value = propValue.asDouble();
                    else
                        value = -1.0;
                }
                else if("java.lang.Float".equals(propBinding.getJavaTypeName()))
                {
                    if(propValue.isDefined())
                        value = propValue.asDouble();
                    else
                        value = -1.0;
                }
                else if("java.lang.String".equals(propBinding.getJavaTypeName()))
                {
                    // default
                    if(propValue.isDefined())
                        value = propValue.asString();
                    else
                        value = "";
                }
                else if ("java.util.List".equals(propBinding.getJavaTypeName()))
                {
                    ModelNode list = actualPayload.get(splitDetypedName);
                    if (list.isDefined() && propValue.isDefined() && !list.asList().isEmpty()) {
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
                if(value!=null)
                    mutator.setValue(entity, propBinding.getJavaName(), value);

            }
            catch (RuntimeException e)
            {
                //  System.out.println("Error on property binding: '"+propBinding.toString()+"'");
                //  System.out.println(dmr);
                throw e;
            }


        }

        return entity;
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
            String[] splitDetypedName = property.getDetypedName().split("/");

            /**
             * KEYS
             */
            //      if(property.isKey()) continue;

            Object propertyValue = mutator.getValue(entity, property.getJavaName());

            /**
             * EXPRESSIONS
             */
            if(property.doesSupportExpression())
            {
                String exprValue = ExpressionAdapter.getExpressionValue(
                        entity, property.getJavaName()
                );

                if(exprValue!=null)
                {
                    operation.get(splitDetypedName).setExpression(exprValue);
                    continue; // expression have precedence over real values
                }
            }

            /**
             * VALUES
             */
            if(propertyValue!=null)
            {
                try {
                    ModelType modelType = resolveModelType(property.getJavaTypeName());
                    if ((modelType == ModelType.LIST) && (property.getListType() == PropertyBinding.class)) {
                        operation.get(splitDetypedName).set(modelType, property.getEntityAdapterForList().fromEntityPropertyList((List) propertyValue));
                    } else if (modelType == ModelType.LIST) {
                        operation.get(splitDetypedName).set(modelType, property.getEntityAdapterForList().fromEntityList((List) propertyValue));
                    } else {
                        operation.get(splitDetypedName).set(modelType, propertyValue);
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

    public ModelNode fromBaseTypeList(List<?> baseTypeValues, Class<?> baseType) {
        ModelNode node = new ModelNode();
        for (Object obj : baseTypeValues) {
            if (baseType == String.class) {
                node.add((String)obj);
            } else if (baseType == Long.class) {
                node.add((Long)obj);
            } else if (baseType == Integer.class) {
                node.add((Integer)obj);
            } else if (baseType == Boolean.class) {
                node.add((Boolean)obj);
            } else if (baseType == Double.class) {
                node.add((Double)obj);
            } else if (baseType == BigDecimal.class) {
                node.add((BigDecimal)obj);
            } else if (baseType == byte[].class) {
                node.add((byte[])obj);
            } else {
                throw new IllegalArgumentException("Can not convert. This value is not of a recognized base type. Value =" + obj.toString());
            }
        }
        return node;
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
    public ModelNode fromChangeset(Map<String, Object> changeSet, ModelNode address, ModelNode... extraSteps)
    {

        ModelNode protoType = new ModelNode();
        protoType.get(ADDRESS).set(address.get(ADDRESS));
        protoType.get(OP).set(WRITE_ATTRIBUTE_OPERATION);

        ModelNode operation = new ModelNode();
        operation.get(OP).set(COMPOSITE);
        operation.get(ADDRESS).setEmptyList();

        List<ModelNode> steps = new ArrayList<ModelNode>();

        List<PropertyBinding> propertyBindings = metaData.getBeanMetaData(type).getProperties();

        Map<String, ModelNode> flattenedSteps = new HashMap<String, ModelNode>();
        for(PropertyBinding binding : propertyBindings)
        {
            Object value = changeSet.get(binding.getJavaName());
            if (value == null) continue;

            ModelNode step = protoType.clone();

            // account for flattened sub-attribute paths
            String detypedName = binding.getDetypedName();
            String[] splitDetypedName = detypedName.split("/");

            step.get(NAME).set(splitDetypedName[0]);
            splitDetypedName[0] = VALUE;
            ModelNode nodeToSetValueUpon = step.get(splitDetypedName);

            if (binding.isFlattened()) { // unflatten
                String attributePath = detypedName.substring(0, detypedName.lastIndexOf("/"));
                ModelNode savedStep = flattenedSteps.get(attributePath);
                if (savedStep == null) {
                    setValue(binding, nodeToSetValueUpon, value);
                    flattenedSteps.put(attributePath, step);
                } else {
                    setValue(binding, savedStep.get(splitDetypedName), value);
                }

            } else {
                setValue(binding, nodeToSetValueUpon, value);
                steps.add(step);
            }
        }

        // add steps for flattened attributes
        for (ModelNode step : flattenedSteps.values()) steps.add(step);

        // add extra steps
        steps.addAll(Arrays.asList(extraSteps));

        operation.get(STEPS).set(steps);
        return operation;
    }

    private void setValue(PropertyBinding binding, ModelNode nodeToSetValueUpon, Object value) {
        Class type = value.getClass();

        if(FormItem.VALUE_SEMANTICS.class == type) {

            // skip undefined form item values (FormItem.UNDEFINED.Value)
            // or persist as UNDEFINED
            if(value.equals(FormItem.VALUE_SEMANTICS.UNDEFINED)
                    && binding.isWriteUndefined())
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
        else if (binding.getListType() != null)
        {
            if (binding.getListType() == PropertyRecord.class) {
                nodeToSetValueUpon.set(fromEntityPropertyList((List)value));
            }  else if (isBaseType(binding.getListType())) {
                nodeToSetValueUpon.set(fromBaseTypeList((List)value, binding.getListType()));
            } else {
                nodeToSetValueUpon.set(fromEntityList((List)value));
            }
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
