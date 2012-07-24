package org.jboss.as.console.client.tools;

import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.ModelType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 7/23/12
 */
public class Types {

    public static ModelNode toDMR(Object o) {
        ModelNode node = new ModelNode();
        final ModelType attributeType = resolveModelType(o.getClass().getName());

        if(ModelType.STRING == attributeType)
        {
            node.set((String)o);
        }
        else if(ModelType.INT == attributeType)
        {
            node.set((Integer)o);
        }
        else if(ModelType.LONG == attributeType)
        {
            node.set((Long)o);
        }
        else if(ModelType.BOOLEAN == attributeType)
        {
            node.set((Boolean)o);
        }
        else if(ModelType.DOUBLE == attributeType)
        {
            node.set((Double)o);
        }
        else if(ModelType.LIST == attributeType)
        {
            final List<ModelNode> nodeList = node.asList();
            node.setEmptyList();
            for(ModelNode item : nodeList)
                node.add(item.toString());

        }
        else if (ModelType.OBJECT == attributeType)
        {
            node.set(o.toString());
        }
        else {
            throw new RuntimeException("Unsupported type "+attributeType);
        }

        return node;
    }

    private static ModelType resolveModelType(String javaTypeName) {

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
        }
        else if("java.util.ArrayList".equals(javaTypeName)) {
            type = ModelType.LIST;
        }
        else {
            throw new RuntimeException("Failed to resolve ModelType for '"+ javaTypeName+"'");
        }

        return type;
    }

    public static Object fromDmr(ModelNode attributeNode) {

        Object result = null;
        final ModelType attributeType = attributeNode.getType();

        if(ModelType.STRING == attributeType)
        {
            result = attributeNode.asString();
        }
        else if(ModelType.INT == attributeType)
        {
            result = attributeNode.asInt();
        }
        else if(ModelType.LONG == attributeType)
        {
            result = attributeNode.asLong();
        }
        else if(ModelType.BOOLEAN == attributeType)
        {
            result = attributeNode.asBoolean();
        }
        else if(ModelType.DOUBLE == attributeType)
        {
            result = attributeNode.asDouble();
        }
        else if(ModelType.LIST == attributeType)
        {
            final List<ModelNode> nodeList = attributeNode.asList();
            List list = new ArrayList(nodeList.size());
            for(ModelNode item : nodeList)
                list.add(item.toString());

            result = list;
        }
        else if(ModelType.OBJECT == attributeType)
        {
            result = attributeNode.asString();
        }
        else {
            throw new RuntimeException("Unsupported type "+attributeType);
        }

        return result;
    }

}
