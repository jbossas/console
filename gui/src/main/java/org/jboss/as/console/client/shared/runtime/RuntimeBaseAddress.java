package org.jboss.as.console.client.shared.runtime;

import org.jboss.as.console.client.shared.state.CurrentServerSelection;
import org.jboss.dmr.client.ModelNode;

import javax.inject.Inject;

/**
 * @author Heiko Braun
 * @date 12/9/11
 */
public class RuntimeBaseAddress {

    @Inject private static CurrentServerSelection serverSelection;
    @Inject private static RuntimeBaseAddress instance;

    @Inject
    public RuntimeBaseAddress(
            CurrentServerSelection serverSelection) {
        this.serverSelection = serverSelection;
    }

    public static ModelNode get() {
        return instance.getAddress();
    }

    public ModelNode getAddress() {
        ModelNode baseAddress = new ModelNode();
        baseAddress.setEmptyList();

        if(serverSelection.isSet())
        {
            baseAddress.add("host", serverSelection.getHost());
            baseAddress.add("server", serverSelection.getServer().getName());
        }

        return baseAddress;
    }
}
