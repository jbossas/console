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

import java.util.LinkedHashMap;
import java.util.Map;

import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.NameTokens;

/**
 * @author Heiko Braun
 * @date 3/29/11
 */
public class SubsystemMetaData {

    static Map<String, SubsystemGroup> groups = new LinkedHashMap<String, SubsystemGroup>();

    private static final String CONNECTOR = "Connector";

    private static final String MESSAGING = "Messaging";

    private static final String CORE = "Core";

    private static final String CONTAINER = "Container";

    private static final String EJB = "EJB";

    private static final String OSGI = "OSGi";

    private static final String INFINISPAN = "Infinispan";

    private static final String SECURITY = "Security";

    private static final String WEB = "Web";

    private static final String OTHER = "Other";

    static {

        // specify groups and the order they appear
        groups.put(CORE, new SubsystemGroup(CORE));
        groups.put(CONNECTOR, new SubsystemGroup(CONNECTOR));
        groups.put(MESSAGING, new SubsystemGroup(MESSAGING));
        groups.put(SECURITY, new SubsystemGroup(SECURITY));
        groups.put(CONTAINER, new SubsystemGroup(CONTAINER));
        groups.put(WEB, new SubsystemGroup(WEB));
        groups.put(OSGI, new SubsystemGroup(OSGI));
        groups.put(OTHER, new SubsystemGroup(OTHER));

        // assign actual subsystems
        groups.get(CONNECTOR).getItems().add(new SubsystemGroupItem("JCA", "jca", Boolean.TRUE));
        groups.get(CONNECTOR).getItems().add(new SubsystemGroupItem("Datasources", "datasources"));
        groups.get(CONNECTOR).getItems().add(new SubsystemGroupItem("Resource Adapter", "resource-adapters"));
        groups.get(CONNECTOR).getItems().add(new SubsystemGroupItem("Connector", "connector",Boolean.TRUE));

        groups.get(WEB).getItems().add(new SubsystemGroupItem("Servlet/HTTP", "web"));
        groups.get(WEB).getItems().add(new SubsystemGroupItem("Web Services", "webservices"));
        groups.get(WEB).getItems().add(new SubsystemGroupItem("JAXRS", "jaxrs",Boolean.TRUE));

        groups.get(MESSAGING).getItems().add(new SubsystemGroupItem("JMS", "messaging"));

        groups.get(CORE).getItems().add(new SubsystemGroupItem("Threads", "threads", Boolean.TRUE));
        groups.get(CORE).getItems().add(new SubsystemGroupItem("Logging", "logging"));
        groups.get(CORE).getItems().add(new SubsystemGroupItem("Deployment Scanners", "deployment-scanner"));
        groups.get(CORE).getItems().add(new SubsystemGroupItem("Remoting", "remoting",Boolean.TRUE));
        groups.get(CORE).getItems().add(new SubsystemGroupItem("Threads", NameTokens.BoundedQueueThreadPoolPresenter));

        groups.get(CONTAINER).getItems().add(new SubsystemGroupItem("Naming", "naming", !Console.MODULES.getBootstrapContext().isStandalone()));
        groups.get(CONTAINER).getItems().add(new SubsystemGroupItem("EJB 3", "ejb3"));
        groups.get(CONTAINER).getItems().add(new SubsystemGroupItem("EE", "ee",Boolean.TRUE));
        groups.get(CONTAINER).getItems().add(new SubsystemGroupItem("Security", "security"));
        groups.get(CONTAINER).getItems().add(new SubsystemGroupItem("Transactions", "transactions"));
        groups.get(CONTAINER).getItems().add(new SubsystemGroupItem("Weld", "weld",Boolean.TRUE));
        groups.get(CONTAINER).getItems().add(new SubsystemGroupItem("JPA", "jpa",Boolean.TRUE));

        groups.get(OSGI).getItems().add(new SubsystemGroupItem("Configuration", "osgi", NameTokens.OSGiConfigurationPresenter));
        groups.get(OSGI).getItems().add(new SubsystemGroupItem("Runtime", "osgi", NameTokens.OSGiRuntimePresenter));

        groups.get(OTHER).getItems().add(new SubsystemGroupItem("Infinispan", NameTokens.CacheContainerPresenter, Boolean.TRUE));

        groups.get(OTHER).getItems().add(new SubsystemGroupItem("SAR", "sar",Boolean.TRUE));
        groups.get(OTHER).getItems().add(new SubsystemGroupItem("JMX", "jmx",Boolean.TRUE));
        groups.get(OTHER).getItems().add(new SubsystemGroupItem("Arquillian", "arquillian",Boolean.TRUE));

        groups.get(SECURITY).getItems().add(new SubsystemGroupItem("Security Provider", "security",Boolean.TRUE));
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
