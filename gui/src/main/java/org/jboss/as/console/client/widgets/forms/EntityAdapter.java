package org.jboss.as.console.client.widgets.forms;

import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.ModelType;
import org.jboss.dmr.client.Property;

import java.util.LinkedList;
import java.util.List;

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
        for(PropertyBinding propBinding : beanMetaData.properties)
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
                Mutator mutator = metaData.getMutator(type);
                mutator.mutate(protoType, propBinding.getJavaName(), value);


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

    public ModelNode fromEntity(T entity)
    {
        return null;
    }

    public List<ModelNode> fromEntityList(List<T> entities)
    {
        return null;
    }

}
