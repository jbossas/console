/*
 * JBoss, Home of Professional Open Source
 * Copyright <YEAR> Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jboss.as.console.client.domain.model.impl;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import org.jboss.as.console.client.domain.events.StaleModelEvent;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.shared.BeanFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Heiko Braun
 * @date 2/15/11
 */
public class MockServerGroupStore {

    public static final String PRODUCTION_SERVERS = "Production Servers";
    public static final String DEVELOPMENT_ENVIRONMENT = "Development Environment";
    public static final String B2B_SERVICES = "B2B Services";

    public static final String SOCKET_DMZ = "DMZ";
    public static final String SOCKET_DEFAULT = "default";
    public static final String SOCKET_NO_HTTP = "default_no_http";

    public static final String JVM_DEFAULT = "jdk_1.6";
    public static final String JVM_15 = "jdk_1.5";


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
        eeServer.setGroupName(PRODUCTION_SERVERS);
        eeServer.setProfileName("EE6 Web");
        eeServer.setProperties(props);
        eeServer.setJvm(JVM_DEFAULT);
        eeServer.setSocketBinding(SOCKET_DEFAULT);
        results.add(eeServer);

        ServerGroupRecord webServer = factory.serverGroup().as();
        webServer.setGroupName(DEVELOPMENT_ENVIRONMENT);
        webServer.setProfileName("EE6 Web");
        webServer.setProperties(Collections.EMPTY_MAP);
        webServer.setJvm(JVM_DEFAULT);
        webServer.setSocketBinding(SOCKET_DMZ);
        results.add(webServer);

        ServerGroupRecord standby = factory.serverGroup().as();
        standby.setGroupName(B2B_SERVICES);
        standby.setProfileName("Messaging");
        standby.setProperties(Collections.EMPTY_MAP);
        standby.setJvm(JVM_15);
        standby.setSocketBinding(SOCKET_NO_HTTP);
        results.add(standby);
    }


    public List<ServerGroupRecord> loadServerGroups() {

        Log.debug("Loaded " + results.size() + " server groups");
        return results;
    }


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


    public void persist(ServerGroupRecord updatedEntity) {

        deleteGroup(updatedEntity, false);
        results.add(updatedEntity);
        bus.fireEvent(new StaleModelEvent(StaleModelEvent.SERVER_GROUPS));
    }


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
