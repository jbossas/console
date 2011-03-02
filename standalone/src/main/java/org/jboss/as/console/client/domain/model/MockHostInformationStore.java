package org.jboss.as.console.client.domain.model;

import com.google.gwt.core.client.GWT;
import org.jboss.as.console.client.shared.BeanFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Heiko Braun
 * @date 3/2/11
 */
public class MockHostInformationStore implements HostInformationStore {

    BeanFactory beanFactory = GWT.create(BeanFactory.class);

    private List<Host> hosts = new ArrayList<Host>();
    private Map<String, List<Server>> servers =
            new HashMap<String, List<Server>>();

    String[] addresses = new String[] {
            "192.168.10.5",
            "192.168.10.17",
            "192.168.10.11"
    };

    public MockHostInformationStore() {

        for(String address : addresses)
        {
            Host host = beanFactory.host().as();
            host.setName(address);

            servers.put(host.getName(), new ArrayList<Server>());
            hosts.add(host);
        }


        Server s1 = beanFactory.serverInstance().as();
        s1.setGroup(MockServerGroupStore.PRODUCTION_SERVERS);
        s1.setName("EE6 Server (default)");
        s1.setStarted(true);

        Server s2 = beanFactory.serverInstance().as();
        s2.setGroup(MockServerGroupStore.DEVELOPMENT_ENVIRONMENT);
        s2.setName("Internal Dev");
        s2.setStarted(false);

        servers.get(addresses[0]).add(s1);
        servers.get(addresses[0]).add(s2);

        Server s3 = beanFactory.serverInstance().as();
        s3.setGroup(MockServerGroupStore.DEVELOPMENT_ENVIRONMENT);
        s3.setName("Message Broker (dev)");
        s3.setStarted(false);

        Server s4 = beanFactory.serverInstance().as();
        s4.setGroup(MockServerGroupStore.B2B_SERVICES);
        s4.setName("Message Broker");
        s4.setStarted(true);

        servers.get(addresses[1]).add(s3);
        servers.get(addresses[1]).add(s4);
    }

    @Override
    public List<Host> getHosts() {
        return hosts;
    }

    @Override
    public List<Server> getServers(String hostName) {
        return servers.get(hostName);
    }

}
