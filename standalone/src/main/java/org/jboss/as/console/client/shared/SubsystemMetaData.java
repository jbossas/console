/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
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

package org.jboss.as.console.client.shared;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Heiko Braun
 * @date 3/29/11
 */
public class SubsystemMetaData {

    static Map<String, SubsystemGroup> groups = new HashMap<String, SubsystemGroup>();

    private static final String INTEGRATION = "Integration";

    private static final String MESSAGING = "Messaging";

    private static final String CORE = "Core";

    private static final String CONTAINER = "Container";

    private static final String SECURITY = "Security";

    private static final String WEB = "Web";

    private static final String OTHER = "Other";

    static {

        // specify groups
        groups.put(INTEGRATION, new SubsystemGroup(INTEGRATION));
        groups.put(MESSAGING, new SubsystemGroup(MESSAGING));
        groups.put(CORE, new SubsystemGroup(CORE));
        groups.put(CONTAINER, new SubsystemGroup(CONTAINER));
        groups.put(SECURITY, new SubsystemGroup(SECURITY));
        groups.put(WEB, new SubsystemGroup(WEB));
        groups.put(OTHER, new SubsystemGroup(OTHER));

        // assign actual subsystems
        groups.get(INTEGRATION).getItems().add(new SubsystemGroupItem("JCA", "jca"));
        groups.get(INTEGRATION).getItems().add(new SubsystemGroupItem("Datasources", "datasources"));
        groups.get(INTEGRATION).getItems().add(new SubsystemGroupItem("Resource Adapter", "resource-adapters"));
        groups.get(INTEGRATION).getItems().add(new SubsystemGroupItem("Connector", "connector", true));

        groups.get(WEB).getItems().add(new SubsystemGroupItem("HTTP", "web"));
        groups.get(WEB).getItems().add(new SubsystemGroupItem("Web Services", "webservices"));
        groups.get(WEB).getItems().add(new SubsystemGroupItem("JAXRS", "jaxrs"));

        groups.get(MESSAGING).getItems().add(new SubsystemGroupItem("JMS", "jms"));
        groups.get(MESSAGING).getItems().add(new SubsystemGroupItem("Messaging", "messaging"));

        groups.get(CORE).getItems().add(new SubsystemGroupItem("Threads", "threads"));
        groups.get(CORE).getItems().add(new SubsystemGroupItem("Logging", "logging"));
        groups.get(CORE).getItems().add(new SubsystemGroupItem("Remoting", "remoting"));

        groups.get(CONTAINER).getItems().add(new SubsystemGroupItem("EE", "ee"));
        groups.get(CONTAINER).getItems().add(new SubsystemGroupItem("EJB3", "ejb3"));
        groups.get(CONTAINER).getItems().add(new SubsystemGroupItem("Transactions", "transactions"));
        groups.get(CONTAINER).getItems().add(new SubsystemGroupItem("Naming", "naming"));
        groups.get(CONTAINER).getItems().add(new SubsystemGroupItem("Weld", "weld"));
        groups.get(CONTAINER).getItems().add(new SubsystemGroupItem("JPA", "jpa"));

        groups.get(OTHER).getItems().add(new SubsystemGroupItem("OSGI", "osgi"));
        groups.get(OTHER).getItems().add(new SubsystemGroupItem("SAR", "sar"));
        groups.get(OTHER).getItems().add(new SubsystemGroupItem("JMX", "jmx"));
        groups.get(OTHER).getItems().add(new SubsystemGroupItem("Arquillian", "arquillian"));

        groups.get(SECURITY).getItems().add(new SubsystemGroupItem("Security", "security"));
    }

    public static Map<String, SubsystemGroup> getGroups() {
        return groups;
    }

    public static SubsystemGroup getGroupForKey(String subsysKey)
    {
        SubsystemGroup matchingGroup = null;

        for(String groupName : groups.keySet())
        {
            SubsystemGroup group = groups.get(groupName);
            for(SubsystemGroupItem item : group.getItems())
            {
                if(item.getKey().equals(subsysKey)
                        && item.isDisabled() == false)
                {
                    matchingGroup =  group;
                    break;
                }
            }

            if(matchingGroup!=null)
                break;
        }

        // found one?
        if(null==matchingGroup)
            matchingGroup = groups.get(OTHER);

        return matchingGroup;
    }
}
