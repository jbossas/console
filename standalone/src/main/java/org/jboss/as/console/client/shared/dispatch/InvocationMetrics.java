package org.jboss.as.console.client.shared.dispatch;

import org.jboss.dmr.client.ModelDescriptionConstants;
import org.jboss.dmr.client.ModelNode;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Heiko Braun
 * @date 3/22/11
 */
public class InvocationMetrics {

    private Map<String, Double> numInvocations = new HashMap<String, Double>();

    public void addInvocation(ModelNode operation)
    {

        String key = deriveKey(operation);

        Double value = numInvocations.get(key);
        if(null==value)
            numInvocations.put(key, 1.0);
        else
        {
            numInvocations.put(key, ++value);
        }
    }

    private String deriveKey(ModelNode operation) {
        String key = operation.get(ModelDescriptionConstants.OP_ADDR) + "::" +
                operation.get(ModelDescriptionConstants.OP);

        String childType = operation.get(ModelDescriptionConstants.CHILD_TYPE) == null ?
                "" : " (child-type="+operation.get(ModelDescriptionConstants.CHILD_TYPE).asString() +")";
        key+=childType;

        return key;
    }

    public Map<String, Double> getNumInvocations() {
        return numInvocations;
    }
}
