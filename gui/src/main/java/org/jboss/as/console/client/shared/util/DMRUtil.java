package org.jboss.as.console.client.shared.util;

import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.ModelType;
import org.jboss.dmr.client.Property;

import java.util.List;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * @author Heiko Braun
 * @date 10/22/12
 */
public class DMRUtil {

    public static void copyResourceValues(ModelNode fromModel, ModelNode toResourceParent, List<ModelNode> steps) {

        for(String key : fromModel.keys())
        {
            ModelNode attributeValue = fromModel.get(key);

            if(attributeValue.isDefined())
            {
                if(ModelType.OBJECT.equals(attributeValue.getType()))
                {
                    // probably children
                    Property child = attributeValue.asProperty();

                    ModelNode childOp = new ModelNode();
                    childOp.get(OP).set(ADD);
                    childOp.get(ADDRESS).set(toResourceParent.get(ADDRESS));
                    childOp.get(ADDRESS).add(key, child.getName());

                    copyResourceValues(child.getValue(), childOp, steps);

                    steps.add(childOp);
                }
                else
                {
                    // should be simple attribute
                    toResourceParent.get(key).set(attributeValue);
                }
            }
        }
    }
}
