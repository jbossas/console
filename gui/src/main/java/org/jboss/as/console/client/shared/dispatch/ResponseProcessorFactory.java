package org.jboss.as.console.client.shared.dispatch;

import org.jboss.as.console.client.shared.state.ReloadState;
import org.jboss.dmr.client.ModelNode;

import javax.inject.Inject;

/**
 * @author Heiko Braun
 * @date 1/17/12
 */
public class ResponseProcessorFactory {

    @Inject
    public static ResponseProcessorFactory INSTANCE;

    // used before bootstrap completes
    public static ResponseProcessor NOOP = new ResponseProcessor() {

        @Override
        public void process(ModelNode response) {

        }
    };

    private ResponseProcessor delegate = NOOP;
    private ReloadState reloadState;

    @Inject
    public ResponseProcessorFactory(ReloadState reloadState) {

        this.reloadState = reloadState;
    }

    public ResponseProcessor get() {
        return INSTANCE.delegate;
    }

    public void bootstrap(boolean isStandalone)
    {
        if(isStandalone)
            delegate = new StandaloneResponseProcessor(reloadState);
        else
            delegate = new DomainResponseProcessor(reloadState);
    }
}
