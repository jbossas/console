package org.jboss.as.console.client.shared.runtime;

import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.BootstrapContext;
import org.jboss.as.console.client.shared.state.CurrentServerSelection;
import org.jboss.as.console.client.shared.state.DomainEntityManager;
import org.jboss.dmr.client.ModelNode;

import javax.inject.Inject;

/**
 * @author Heiko Braun
 * @date 12/9/11
 */
public class RuntimeBaseAddress {

    @Inject private static RuntimeBaseAddress instance;

    @Inject private static BootstrapContext bootstrap;

    public static ModelNode get() {
        return instance.getAddress();
    }



    public ModelNode getAddress() {

        final DomainEntityManager domainManager = Console.MODULES.getDomainEntityManager();

        ModelNode baseAddress = new ModelNode();
        baseAddress.setEmptyList();

        if(!bootstrap.isStandalone())
        {
            baseAddress.add("host", domainManager.getSelectedHost());
            baseAddress.add("server", domainManager.getSelectedServer());
        }

        return baseAddress;
    }
}
