/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General  License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General  License for more details.
 * You should have received a copy of the GNU Lesser General  License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jboss.as.console.client.shared.deployment.model;

import org.jboss.as.console.client.domain.model.ServerInstance;
import org.jboss.as.console.client.widgets.forms.Binding;

/**
 * TODO Split up into different classes: DeploymentContent, GroupDeployment, ServerDeployment
 * TODO Consolidate the different 'context' data like server, group and address
 *
 * @author Heiko Braun
 * @date 1/31/11
 */
public interface DeploymentRecord extends DeploymentData
{
    @Binding(key = true)
    String getName();
    void setName(String name);

    String getStatus();
    void setStatus(String status);

    @Binding(skip = true)
    String getPath();
    void setPath(String path);

    @Binding(skip = true)
    boolean isArchive();
    void setArchive(boolean isArchive);

    @Binding(skip = true)
    String getRelativeTo();
    void setRelativeTo(String relativeTo);

    @Binding(detypedName = "runtime-name")
    String getRuntimeName();
    void setRuntimeName(String runtimeName);

    @Binding(skip = true)
    String getSha();
    void setSha(String sha);

    @Binding(skip = true)
    String getServerGroup();
    void setServerGroup(String groupName);

    boolean isEnabled();
    void setEnabled(boolean enabaled);

    boolean isPersistent();
    void setPersistent(boolean isPersistent);

    @Binding(skip = true)
    DeploymentRecord getParent();
    void setParent(DeploymentRecord parent);

    @Binding(skip = true)
    boolean isSubdeployment();
    void setSubdeployment(boolean subdeployment);

    @Binding(skip = true)
    boolean isHasSubsystems();
    void setHasSubsystems(boolean hasSubsystems);

    @Binding(skip = true)
    boolean isHasSubdeployments();
    void setHasSubdeployments(boolean hasSubdeployments);

    @Binding(skip = true)
    ServerInstance getServer();
    void setServer(ServerInstance server);
}
