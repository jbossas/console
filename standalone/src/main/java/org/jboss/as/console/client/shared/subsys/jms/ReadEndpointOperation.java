package org.jboss.as.console.client.shared.subsys.jms;

import org.jboss.dmr.client.ModelNode;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * @author Heiko Braun
 * @date 3/29/11
 */
public class ReadEndpointOperation extends ModelNode{

    public ReadEndpointOperation(String profile, String type) {
        super();
        get(OP).set(READ_CHILDREN_RESOURCES_OPERATION);
        get(ADDRESS).add("profile", profile);
        get(ADDRESS).add("subsystem", "jms");
        get(CHILD_TYPE).set(type);
    }
}
