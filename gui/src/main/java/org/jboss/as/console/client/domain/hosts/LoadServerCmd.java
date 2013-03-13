package org.jboss.as.console.client.domain.hosts;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.jboss.dmr.client.dispatch.AsyncCommand;
import org.jboss.as.console.client.shared.state.DomainEntityManager;
import org.jboss.as.console.client.shared.state.ServerInstanceList;

/**
 * @author Heiko Braun
 * @date 12/9/11
 */
public class LoadServerCmd implements AsyncCommand<ServerInstanceList> {


    private final DomainEntityManager domainManager;

    public LoadServerCmd(DomainEntityManager domainManager) {
        this.domainManager = domainManager;
    }

    public void execute(final AsyncCallback<ServerInstanceList> callback) {
        domainManager.getServerInstances(domainManager.getSelectedHost(), callback);
    }
}
