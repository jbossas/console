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
    private Map<String, List<Server>> servers = new HashMap<String, List<Server>>();
    private Map<String, List<ServerInstance>> instances = new HashMap<String, List<ServerInstance>>();

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
            instances.put(host.getName(), new ArrayList<ServerInstance>());
            hosts.add(host);
        }


        Server s1 = beanFactory.server().as();
        s1.setGroup(MockServerGroupStore.PRODUCTION_SERVERS);
        s1.setName("EE6 Server (default)");
        s1.setStarted(true);
        s1.setSocketBinding(MockServerGroupStore.SOCKET_DEFAULT);
        s1.setJvm(MockServerGroupStore.JVM_DEFAULT);

        Server s2 = beanFactory.server().as();
        s2.setGroup(MockServerGroupStore.DEVELOPMENT_ENVIRONMENT);
        s2.setName("Internal Dev");
        s2.setStarted(false);
        s2.setSocketBinding(MockServerGroupStore.SOCKET_DMZ);
        s2.setJvm(MockServerGroupStore.JVM_15);

        servers.get(addresses[0]).add(s1);
        servers.get(addresses[0]).add(s2);

        Server s3 = beanFactory.server().as();
        s3.setGroup(MockServerGroupStore.DEVELOPMENT_ENVIRONMENT);
        s3.setName("Message Broker (dev)");
        s3.setStarted(false);
        s3.setSocketBinding(MockServerGroupStore.SOCKET_NO_HTTP);
        s3.setJvm(MockServerGroupStore.JVM_DEFAULT);

        Server s4 = beanFactory.server().as();
        s4.setGroup(MockServerGroupStore.B2B_SERVICES);
        s4.setName("Message Broker");
        s4.setStarted(true);
        s4.setSocketBinding(MockServerGroupStore.SOCKET_DEFAULT);
        s2.setJvm(MockServerGroupStore.JVM_15);

        servers.get(addresses[1]).add(s3);
        servers.get(addresses[1]).add(s4);

        // -----


        ServerInstance i1 = beanFactory.serverInstance().as();
        i1.setServer(s1.getName());
        i1.setName(s1.getName()+"_1");
        i1.setRunning(true);

        ServerInstance i2 = beanFactory.serverInstance().as();
        i2.setServer(s1.getName());
        i2.setName(s1.getName()+"_2");
        i2.setRunning(false);

        instances.get(addresses[0]).add(i1);
        instances.get(addresses[0]).add(i2);

        ServerInstance i3 = beanFactory.serverInstance().as();
        i3.setServer(s4.getName());
        i3.setName(s4.getName()+"_1");
        i3.setRunning(true);

        ServerInstance i4 = beanFactory.serverInstance().as();
        i4.setServer(s4.getName());
        i4.setName(s4.getName()+"_2");
        i4.setRunning(true);

        instances.get(addresses[1]).add(i3);
        instances.get(addresses[1]).add(i4);

        ServerInstance i5 = beanFactory.serverInstance().as();
        i5.setServer(s2.getName());
        i5.setName(s2.getName()+"_1");
        i5.setRunning(false);

        instances.get(addresses[0]).add(i5);

    }

    @Override
    public List<Host> getHosts() {
        return hosts;
    }

    @Override
    public List<Server> getServers(String hostName) {
        return servers.get(hostName);
    }

    @Override
    public List<ServerInstance> getInstances(String host) {

        return instances.get(host);
    }
}
