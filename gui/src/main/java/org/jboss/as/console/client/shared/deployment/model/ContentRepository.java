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
package org.jboss.as.console.client.shared.deployment.model;

import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.shared.model.HasNameComparator;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Represents the deployments and the server group assignments in the domain mode. Does not contain runtime, host or
 * server information.
 *
 * @author Harald Pehl
 * @date 12/12/2012
 */
public class ContentRepository
{
    // Keys have to be strings rather then the autobean instances to have proper hash codes
    private final SortedMap<String, SortedSet<String>> groupsOfDeployment;
    private final Map<String, DeploymentRecord> nameToDeployment;
    private final SortedSet<ServerGroupRecord> serverGroups;
    private final Map<String, ServerGroupRecord> nameToServerGroup;
    private final Map<String, SortedSet<DeploymentRecord>> deploymentsOfGroup;


    public ContentRepository()
    {
        this.groupsOfDeployment = new TreeMap<String, SortedSet<String>>();
        this.nameToDeployment = new HashMap<String, DeploymentRecord>();
        this.serverGroups = new TreeSet<ServerGroupRecord>(new HasNameComparator<ServerGroupRecord>());
        this.nameToServerGroup = new HashMap<String, ServerGroupRecord>();
        this.deploymentsOfGroup = new HashMap<String, SortedSet<DeploymentRecord>>();
    }

    public void addDeployment(DeploymentRecord deployment)
    {
        groupsOfDeployment.put(deployment.getName(), new TreeSet<String>());
        nameToDeployment.put(deployment.getName(), deployment);
    }

    public void addServerGroup(ServerGroupRecord serverGroup)
    {
        serverGroups.add(serverGroup);
        nameToServerGroup.put(serverGroup.getName(), serverGroup);
        deploymentsOfGroup
                .put(serverGroup.getName(), new TreeSet<DeploymentRecord>(new HasNameComparator<DeploymentRecord>()));
    }

    public void assignDeploymentToServerGroup(String depoymentName, String serverGroupName)
    {
        DeploymentRecord deployment = nameToDeployment.get(depoymentName);
        ServerGroupRecord serverGroup = nameToServerGroup.get(serverGroupName);
        if (deployment != null && serverGroup != null)
        {
            SortedSet<String> groups = groupsOfDeployment.get(deployment.getName());
            groups.add(serverGroup.getName());
            SortedSet<DeploymentRecord> deployments = deploymentsOfGroup.get(serverGroup.getName());
            deployments.add(deployment);
        }
    }

    public List<DeploymentRecord> getDeployments()
    {
        Set<String> deploymentNames = groupsOfDeployment.keySet();
        SortedSet<DeploymentRecord> deployments = new TreeSet<DeploymentRecord>(
                new HasNameComparator<DeploymentRecord>());
        for (String deploymentName : deploymentNames)
        {
            deployments.add(nameToDeployment.get(deploymentName));
        }
        return new LinkedList<DeploymentRecord>(deployments);
    }

    public List<DeploymentRecord> getDeployments(ServerGroupRecord serverGroup)
    {
        SortedSet<DeploymentRecord> deployments = deploymentsOfGroup.get(serverGroup.getName());
        if (deployments != null)
        {
            return new LinkedList<DeploymentRecord>(deployments);
        }
        return Collections.emptyList();
    }

    public DeploymentRecord getDeployment(String name)
    {
        return nameToDeployment.get(name);
    }

    public List<ServerGroupRecord> getServerGroups()
    {
        return new LinkedList<ServerGroupRecord>(serverGroups);
    }

    public List<String> getServerGroups(DeploymentRecord deployment)
    {
        SortedSet<String> serverGroups = groupsOfDeployment.get(deployment.getName());
        if (serverGroups != null)
        {
            return new LinkedList<String>(serverGroups);
        }
        return Collections.emptyList();
    }

    public int getNumberOfAssignments(DeploymentRecord deployment)
    {
        int result = 0;
        SortedSet<String> serverGroups = groupsOfDeployment.get(deployment.getName());
        if (serverGroups != null)
        {
            result = serverGroups.size();
        }
        return result;
    }

    public List<ServerGroupRecord> getPossibleServerGroupAssignments(DeploymentRecord deployment)
    {
        SortedSet<ServerGroupRecord> allServerGroups = new TreeSet<ServerGroupRecord>(
                new HasNameComparator<ServerGroupRecord>());
        allServerGroups.addAll(serverGroups);
        List<String> currentServerGroupsNames = getServerGroups(deployment);
        for (String serverGroupsName : currentServerGroupsNames)
        {
            ServerGroupRecord serverGroup = nameToServerGroup.get(serverGroupsName);
            allServerGroups.remove(serverGroup);
        }
        return new LinkedList<ServerGroupRecord>(allServerGroups);
    }

    public List<DeploymentRecord> getPossibleServerGroupAssignments(ServerGroupRecord serverGroup)
    {
        SortedSet<DeploymentRecord> allDeployments = new TreeSet<DeploymentRecord>(
                new HasNameComparator<DeploymentRecord>());
        allDeployments.addAll(getDeployments());

        List<DeploymentRecord> currentDeployments = getDeployments(serverGroup);
        allDeployments.removeAll(currentDeployments);
        return new LinkedList<DeploymentRecord>(allDeployments);
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder("{");
        for (Iterator<Map.Entry<String, SortedSet<String>>> iterator = groupsOfDeployment.entrySet().iterator();
                iterator.hasNext(); )
        {
            Map.Entry<String, SortedSet<String>> entry = iterator.next();
            builder.append(entry.getKey()).append(" -> ").append(entry.getValue());
            if (iterator.hasNext())
            {
                builder.append(", ");
            }
        }
        builder.append("}");
        return builder.toString();
    }
}
