package org.jboss.as.console.client.core.bootstrap;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.BootstrapContext;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.state.DomainEntityManager;
import org.jboss.as.console.client.shared.state.HostList;

import java.util.Iterator;

public class EagerLoadHosts extends BoostrapStep {

    private final DomainEntityManager domainManager;

    public EagerLoadHosts(DomainEntityManager domainManager) {
        this.domainManager = domainManager;
    }

    @Override
    public void execute(final Iterator<BoostrapStep> iterator, final AsyncCallback<Boolean> outcome) {

        BootstrapContext bootstrapContext = Console.getBootstrapContext();

        if(!bootstrapContext.isStandalone())
        {
            domainManager.getHosts(new SimpleCallback<HostList>() {
                @Override
                public void onSuccess(HostList hostList) {
                    Log.info("Identified " + hostList.getHosts().size() + " hosts in this domain");
                    outcome.onSuccess(Boolean.TRUE);
                    next(iterator, outcome);
                }
            });
        }
        else
        {
            // standalone
            outcome.onSuccess(Boolean.TRUE);
            next(iterator, outcome);
        }

    }

}
