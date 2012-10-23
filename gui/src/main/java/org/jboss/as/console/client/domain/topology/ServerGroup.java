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
package org.jboss.as.console.client.domain.topology;

import org.jboss.as.console.client.domain.model.ServerInstance;

import java.util.*;

/**
 * Simple class for rendering a server group in {@link TopologyView}
 * @author Harald Pehl
 * @dat 10/11/12
 */
class ServerGroup implements Comparable<ServerGroup>
{
    final String name;
    final String profile;
    String cssClassname;
    int maxServersPerHost;
    SortedMap<HostInfo, List<ServerInstance>> serversPerHost;


    ServerGroup(final String name, final String profile)
    {
        this.name = name;
        this.profile = profile;
        this.cssClassname = "255,255,255";
        this.maxServersPerHost = 0;
        this.serversPerHost = new TreeMap<HostInfo, List<ServerInstance>>();
    }

    @Override
    public int compareTo(final ServerGroup serverGroup)
    {
        return name.compareTo(serverGroup.name);
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }
        ServerGroup that = (ServerGroup) o;
        if (!name.equals(that.name))
        {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        return name.hashCode();
    }

    @Override
    public String toString()
    {
        return name;
    }

    void fill(final List<HostInfo> hosts)
    {
        maxServersPerHost = 0;
        serversPerHost.clear();
        for (HostInfo host : hosts)
        {
            ArrayList<ServerInstance> servers = new ArrayList<ServerInstance>();
            serversPerHost.put(host, servers);
            for (ServerInstance server : host.getServerInstances())
            {
                if (name.equals(server.getGroup()))
                {
                    servers.add(server);
                }
            }
            maxServersPerHost = Math.max(maxServersPerHost, servers.size());
        }
    }

    Set<HostInfo> getHosts()
    {
        return serversPerHost.keySet();
    }
}
