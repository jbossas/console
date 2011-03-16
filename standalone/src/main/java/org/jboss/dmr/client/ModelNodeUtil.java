package org.jboss.dmr.client;

import static org.jboss.dmr.client.ModelDescriptionConstants.ATTRIBUTES;
import static org.jboss.dmr.client.ModelDescriptionConstants.CHILDREN;
import static org.jboss.dmr.client.ModelDescriptionConstants.DESCRIPTION;
import static org.jboss.dmr.client.ModelDescriptionConstants.FAILED;
import static org.jboss.dmr.client.ModelDescriptionConstants.FAILURE_DESCRIPTION;
import static org.jboss.dmr.client.ModelDescriptionConstants.MIN_LENGTH;
import static org.jboss.dmr.client.ModelDescriptionConstants.MIN_OCCURS;
import static org.jboss.dmr.client.ModelDescriptionConstants.MODEL_DESCRIPTION;
import static org.jboss.dmr.client.ModelDescriptionConstants.NAME;
import static org.jboss.dmr.client.ModelDescriptionConstants.OP;
import static org.jboss.dmr.client.ModelDescriptionConstants.OPERATIONS;
import static org.jboss.dmr.client.ModelDescriptionConstants.OP_ADDR;
import static org.jboss.dmr.client.ModelDescriptionConstants.OUTCOME;
import static org.jboss.dmr.client.ModelDescriptionConstants.PROFILE;
import static org.jboss.dmr.client.ModelDescriptionConstants.READ_RESOURCE_DESCRIPTION_OPERATION;
import static org.jboss.dmr.client.ModelDescriptionConstants.RECURSIVE;
import static org.jboss.dmr.client.ModelDescriptionConstants.REQUIRED;
import static org.jboss.dmr.client.ModelDescriptionConstants.RESULT;
import static org.jboss.dmr.client.ModelDescriptionConstants.SUBSYSTEM;
import static org.jboss.dmr.client.ModelDescriptionConstants.TYPE;
import static org.jboss.dmr.client.ModelDescriptionConstants.VALUE_TYPE;

/**
 * @author Heiko Braun
 * @date 3/16/11
 */
public class ModelNodeUtil {

    public static ModelNode createOperation(String operationName, String...address) {
        ModelNode operation = new ModelNode();
        operation.get(OP).set(operationName);
        if (address.length > 0) {
            for (String addr : address) {
                operation.get(OP_ADDR).add(addr);
            }
        } else {
            operation.get(OP_ADDR).setEmptyList();
        }

        return operation;
    }
}
