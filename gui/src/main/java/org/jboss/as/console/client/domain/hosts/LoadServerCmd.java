package org.jboss.as.console.client.domain.hosts;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.jboss.as.console.client.domain.model.HostInformationStore;
import org.jboss.as.console.client.domain.model.ServerInstance;
import org.jboss.as.console.client.shared.dispatch.AsyncCommand;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 12/9/11
 */
public class LoadServerCmd implements AsyncCommand<List<ServerInstance>> {

    private HostInformationStore hostInfoStore;

    public LoadServerCmd(HostInformationStore hostInfoStore) {
        this.hostInfoStore = hostInfoStore;
    }

    @Override
    public void execute(final AsyncCallback<List<ServerInstance>> callback) {

        throw new IllegalArgumentException("Use the overridden method instead");

    }

    public void execute(final String hostName, final AsyncCallback<List<ServerInstance>> callback) {
        hostInfoStore.getServerInstances(hostName, callback);
    }
}
