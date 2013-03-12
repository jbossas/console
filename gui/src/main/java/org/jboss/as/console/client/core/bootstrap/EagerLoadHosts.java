package org.jboss.as.console.client.core.bootstrap;

import com.allen_sauer.gwt.log.client.Log;
import org.jboss.as.console.client.core.BootstrapContext;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.state.DomainEntityManager;
import org.jboss.as.console.client.shared.state.HostList;
import org.jboss.gwt.flow.client.Control;
import org.jboss.gwt.flow.client.Function;

public class EagerLoadHosts implements Function<BootstrapContext> {

    private final DomainEntityManager domainManager;

    public EagerLoadHosts(DomainEntityManager domainManager) {
        this.domainManager = domainManager;
    }

    @Override
    public void execute(final Control<BootstrapContext> control) {
        final BootstrapContext context = control.getContext();

        if(!context.isStandalone())
        {
            domainManager.getHosts(new SimpleCallback<HostList>() {

                @Override
                public void onFailure(Throwable caught) {
                    context.setlastError(caught);
                    control.abort();
                }

                @Override
                public void onSuccess(HostList hostList) {
                    Log.info("Identified " + hostList.getHosts().size() + " hosts in this domain");
                    control.proceed();
                }
            });
        }
        else
        {
            // standalone
            control.proceed();
        }
    }

}
