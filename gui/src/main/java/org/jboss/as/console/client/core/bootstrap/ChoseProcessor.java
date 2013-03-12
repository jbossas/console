package org.jboss.as.console.client.core.bootstrap;

import org.jboss.as.console.client.core.BootstrapContext;
import org.jboss.as.console.client.shared.dispatch.ResponseProcessorFactory;
import org.jboss.gwt.flow.client.Control;
import org.jboss.gwt.flow.client.Function;

/**
 * @author Heiko Braun
 * @date 1/17/12
 */
public class ChoseProcessor implements Function<BootstrapContext> {

    @Override
    public void execute(Control<BootstrapContext> control) {

        BootstrapContext bootstrap = control.getContext();
        ResponseProcessorFactory.INSTANCE.bootstrap(bootstrap.isStandalone());
        control.proceed();
    }
}
