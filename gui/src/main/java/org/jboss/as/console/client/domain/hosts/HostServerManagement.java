package org.jboss.as.console.client.domain.hosts;

import org.jboss.as.console.client.domain.model.Host;

/**
 * @author Heiko Braun
 * @date 12/9/11
 */
public interface HostServerManagement  {
    void loadServer(Host selectedHost);
}
