package org.jboss.as.console.client.widgets.forms;

import org.jboss.dmr.client.ModelNode;
import static org.jboss.dmr.client.ModelDescriptionConstants.*;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 9/23/11
 */
public class ModelAdapter<T> {

    private Class<?> type;
    private PropertyMetaData metaData;

    public ModelAdapter(Class<?> type, PropertyMetaData metaData) {
        this.type = type;
        this.metaData = metaData;
    }

    T fromDMR(ModelNode dmr, T protoType) {

        assert dmr.hasDefined(RESULT) == false : "Provide the payload only!";

        BeanMetaData beanMetaData = metaData.getBeanMetaData(type);
        for(PropertyBinding propBinding : beanMetaData.properties)
        {
            // TODO: alternate types
            dmr.get(propBinding.getDetypedName()).asString();

            // TODO: setter needs to be generated, no introspection!
        }

        return null;
    }

    List<T> fromDMRList(List<ModelNode> dmr) {
        return null;
    }

    ModelNode fromEntity(T entity)
    {
        return null;
    }

    List<ModelNode> fromEntityList(List<T> entities)
    {
        return null;
    }

}
