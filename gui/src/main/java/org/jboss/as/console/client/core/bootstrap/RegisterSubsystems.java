package org.jboss.as.console.client.core.bootstrap;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.jboss.as.console.client.plugins.SubsystemRegistry;
import org.jboss.as.console.client.shared.SubsystemMetaData;
import org.jboss.as.console.client.shared.dispatch.AsyncCommand;

import java.util.Iterator;

/**
 * @author Heiko Braun
 * @date 3/27/12
 */
public class RegisterSubsystems extends BoostrapStep {

    private SubsystemRegistry registry;

    public RegisterSubsystems(SubsystemRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void execute(Iterator<BoostrapStep> iterator, AsyncCallback<Boolean> outcome) {

        SubsystemMetaData.bootstrap(registry);

        outcome.onSuccess(Boolean.TRUE);

        next(iterator, outcome);
    }
}
