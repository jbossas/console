package org.jboss.as.console.client.tools;

import org.jboss.dmr.client.ModelType;

/**
 * @author Heiko Braun
 * @date 7/23/12
 */
public class Types {

    public static ModelType toDMR(Object o) {
        return resolveModelType(o.getClass().getName());
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

}
