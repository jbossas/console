package org.jboss.as.console.client.shared.dispatch;

import org.jboss.dmr.client.ModelNode;

/**
 * @author Heiko Braun
 * @date 1/17/12
 */
public class ResponseProcessorFactory {

    public static ResponseProcessorFactory INSTANCE = new ResponseProcessorFactory();

    // used before bootstrap completes
    public static ResponseProcessor NOOP = new ResponseProcessor() {

        @Override
        public void process(ModelNode response) {

        }
    };

    public static ResponseProcessor PROCESSOR = NOOP;

    public ResponseProcessor create()
    {
        return PROCESSOR;
    }

    public void bootstrap(boolean isStandalone)
    {
        if(isStandalone)
            PROCESSOR = new StandaloneResponseProcessor();
        else
            PROCESSOR = new DomainResponseProcessor();
    }
}
