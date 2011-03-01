package org.jboss.as.console.client.domain.model;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import org.jboss.as.console.client.domain.events.StaleModelEvent;
import org.jboss.as.console.client.shared.BeanFactory;

import java.util.*;

/**
 * @author Heiko Braun
 * @date 2/15/11
 */
public class MockServerGroupStore implements ServerGroupStore {

    BeanFactory factory = GWT.create(BeanFactory.class);

    static Map props = new LinkedHashMap();
    static {
        props.put("-XX:-UseParallelGC", "true");
        props.put("-XX:-DisableExplicitGC", "true");
        props.put("DEFAULT_LOG_LEVEL", "INFO");
    }

    private EventBus bus;

    private List<ServerGroupRecord> results = new ArrayList<ServerGroupRecord>();

    @Inject
    public MockServerGroupStore(EventBus bus) {

        this.bus = bus;

        ServerGroupRecord eeServer = factory.serverGroup().as();
        eeServer.setGroupName("EE6 Server");
        eeServer.setProfileName("EE6 Web");
        eeServer.setProperties(props);
        eeServer.setJvm("jdk_16_default");
        eeServer.setSocketBinding("default");
        results.add(eeServer);

        ServerGroupRecord webServer = factory.serverGroup().as();
        webServer.setGroupName("Web Server");
        webServer.setProfileName("EE6 Web");
        webServer.setProperties(Collections.EMPTY_MAP);
        webServer.setJvm("jdk_16_default");
        webServer.setSocketBinding("DMZ");
        results.add(webServer);

        ServerGroupRecord standby = factory.serverGroup().as();
        standby.setGroupName("Hot Standby");
        standby.setProfileName("Messaging");
        standby.setProperties(Collections.EMPTY_MAP);
        standby.setJvm("jrockit");
        standby.setSocketBinding("default_no_http");
        results.add(standby);
    }

    @Override
    public List<ServerGroupRecord> loadServerGroups() {

        Log.debug("Loaded " + results.size() + " server groups");
        return results;
    }

    @Override
    public List<ServerGroupRecord> loadServerGroups(String profileName) {
        List<ServerGroupRecord> all = loadServerGroups();
        List<ServerGroupRecord> results = new ArrayList<ServerGroupRecord>();

        for(ServerGroupRecord group : all)
        {
            if(group.getProfileName().equals(profileName))
                results.add(group);
        }
        return results;
    }

    @Override
    public void persist(ServerGroupRecord updatedEntity) {

        deleteGroup(updatedEntity, false);
        results.add(updatedEntity);
        bus.fireEvent(new StaleModelEvent(StaleModelEvent.SERVER_GROUPS));
    }

    @Override
    public boolean deleteGroup(ServerGroupRecord record) {
        return deleteGroup(record, true);
    }

    private boolean deleteGroup(ServerGroupRecord record, boolean fire) {
        ServerGroupRecord removal = null;
        for(ServerGroupRecord rec : results)
        {
            if(rec.getGroupName().equals(record.getGroupName()))
            {
                removal = rec;
                break;
            }
        }

        // replace
        if(removal!=null)
        {
            results.remove(removal);
        }

        if(fire) bus.fireEvent(new StaleModelEvent(StaleModelEvent.SERVER_GROUPS));

        return removal!=null;
    }
}
