package org.jboss.as.console.client.core.bootstrap;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.jboss.as.console.client.plugins.SubsystemRegistry;
import org.jboss.as.console.client.shared.SubsystemMetaData;
import org.jboss.as.console.client.shared.dispatch.AsyncCommand;

/**
 * @author Heiko Braun
 * @date 3/27/12
 */
public class RegisterSubsystems implements AsyncCommand<Boolean> {

    private SubsystemRegistry registry;

    public RegisterSubsystems(SubsystemRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void execute(AsyncCallback<Boolean> callback) {

        SubsystemMetaData.bootstrap(registry);

        callback.onSuccess(Boolean.TRUE);
    }
}
