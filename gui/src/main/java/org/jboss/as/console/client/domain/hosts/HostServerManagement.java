package org.jboss.as.console.client.domain.hosts;

import com.google.gwt.user.client.Command;
import org.jboss.as.console.client.domain.model.Host;
import org.jboss.as.console.client.domain.model.ServerInstance;

/**
 * @author Heiko Braun
 * @date 12/9/11
 */
public interface HostServerManagement  {
    void loadServer(String selectedHost, final Command... commands);
    void onServerSelected(Host selectedHost, ServerInstance server);
}
