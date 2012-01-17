package org.jboss.as.console.client.shared.runtime;

import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.state.CurrentServerSelection;
import org.jboss.dmr.client.ModelNode;

import javax.inject.Inject;

/**
 * @author Heiko Braun
 * @date 12/9/11
 */
public class RuntimeBaseAddress {

    private CurrentServerSelection serverSelection;

    @Inject
    public RuntimeBaseAddress(
            CurrentServerSelection serverSelection) {
        this.serverSelection = serverSelection;
    }

    public static ModelNode get() {
        return Console.MODULES.getRuntimeBaseAddress().getAddress();
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
