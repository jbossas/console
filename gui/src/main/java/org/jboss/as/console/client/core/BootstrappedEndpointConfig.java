package org.jboss.as.console.client.core;

import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.dispatch.impl.DMREndpointConfig;

/**
 * @author Heiko Braun
 * @date 3/13/13
 */
public class BootstrappedEndpointConfig implements DMREndpointConfig {
    @Override
    public String getUrl() {
        return Console.MODULES.getBootstrapContext().getProperty(BootstrapContext.DOMAIN_API);
    }
}
