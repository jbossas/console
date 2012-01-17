package org.jboss.as.console.client.core.bootstrap;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.jboss.as.console.client.core.BootstrapContext;
import org.jboss.as.console.client.shared.dispatch.AsyncCommand;
import org.jboss.as.console.client.shared.dispatch.ResponseProcessorFactory;

/**
 * @author Heiko Braun
 * @date 1/17/12
 */
public class ChoseProcessor implements AsyncCommand<Boolean>{

    private BootstrapContext bootstrap;

    public ChoseProcessor(BootstrapContext bootstrapContext) {
        this.bootstrap = bootstrapContext;
    }

    @Override
    public void execute(AsyncCallback<Boolean> callback) {

        ResponseProcessorFactory.INSTANCE.bootstrap(bootstrap.isStandalone());
        callback.onSuccess(Boolean.TRUE);
    }
}
