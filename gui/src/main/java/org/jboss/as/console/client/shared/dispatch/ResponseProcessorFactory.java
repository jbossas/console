package org.jboss.as.console.client.shared.dispatch;

import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.BootstrapContext;

/**
 * @author Heiko Braun
 * @date 1/17/12
 */
public class ResponseProcessorFactory {

    public static ResponseProcessorFactory INSTANCE = new ResponseProcessorFactory();

    public static ResponseProcessor PROCESSOR;

    public ResponseProcessor create() {
        BootstrapContext bootstrapContext = Console.MODULES.getBootstrapContext();

        if(null==PROCESSOR) // run a single instance
        {
            if(bootstrapContext.isStandalone())
                PROCESSOR = new StandaloneResponseProcessor();
            else
                PROCESSOR = new DomainResponseProcessor();
        }

        return PROCESSOR;
    }
}
