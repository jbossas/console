package org.jboss.as.console.client.domain.hosts;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.jboss.as.console.client.domain.model.HostInformationStore;
import org.jboss.as.console.client.domain.model.ServerInstance;
import org.jboss.as.console.client.shared.dispatch.AsyncCommand;
import org.jboss.as.console.client.shared.state.DomainEntityManager;
import org.jboss.as.console.client.shared.state.ServerInstanceList;

import java.util.List;

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
