package org.jboss.as.console.client.domain.hosts;

import org.jboss.as.console.client.domain.model.Server;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 10/7/11
 */
@Singleton
public class CurrentServerConfigurations {

    List<Server> serverConfigs = new ArrayList<Server>();

    public List<Server> getServerConfigs() {
        return serverConfigs;
    }

    public void setServerConfigs(List<Server> serverConfigs) {
        this.serverConfigs = serverConfigs;
    }
}
