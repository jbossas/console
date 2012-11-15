package org.jboss.as.console.client.core.bootstrap;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.jboss.as.console.client.core.BootstrapContext;
import org.jboss.as.console.client.shared.dispatch.AsyncCommand;
import org.jboss.as.console.client.shared.dispatch.ResponseProcessorFactory;

import java.util.Iterator;

/**
 * @author Heiko Braun
 * @date 1/17/12
 */
public class ChoseProcessor extends BoostrapStep {

    private BootstrapContext bootstrap;

    public ChoseProcessor(BootstrapContext bootstrapContext) {
        this.bootstrap = bootstrapContext;
    }

    @Override
    public void execute(Iterator<BoostrapStep> iterator, AsyncCallback<Boolean> outcome) {
        ResponseProcessorFactory.INSTANCE.bootstrap(bootstrap.isStandalone());
        outcome.onSuccess(Boolean.TRUE);
        next(iterator, outcome);
    }
}
