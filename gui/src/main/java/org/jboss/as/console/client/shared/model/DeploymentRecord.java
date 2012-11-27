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

package org.jboss.as.console.client.shared.model;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 1/31/11
 */
public interface DeploymentRecord  {

    public String getName();
    public void setName(String name);

    public String getStatus();
    public void setStatus(String status);

    public String getPath();
    public void setPath(String path);

    public DeploymentRecord getParent();
    public void setParent(DeploymentRecord parent);

    public String getRelativeTo();
    public void setRelativeTo(String relativeTo);

    public String getRuntimeName();
    public void setRuntimeName(String runtimeName);

    public String getSha();
    public void setSha(String sha);

    public String getServerGroup();
    public void setServerGroup(String groupName);

    public boolean isEnabled();
    public void setEnabled(boolean enabaled);

    public boolean isPersistent();
    public void setPersistent(boolean isPersistent);

    public boolean isArchive();
    public void setArchive(boolean isArchive);

    public boolean isSubdeployment();
    public void setSubdeployment(boolean subdeployment);

    public boolean isHasSubsystems();
    public void setHasSubsystems(boolean hasSubsystems);

    public boolean isHasSubdeployments();
    public void setHasSubdeployments(boolean hasSubdeployments);


    interface Subsystem
    {
        enum Type {ejb3, jpa, web, webservices}

        public String getName();
        public void setName(String name);

        public Type getType();
        public void setType(Type type);

        public DeploymentRecord getDeployment();
        public void setDeployment(DeploymentRecord deployment);
    }

    interface SubsystemElement
    {
        public String getName();
        public void setName(String name);

        public Subsystem getSubsystem();
        public void setSubsystem(Subsystem subsystem);
    }

    interface EjbSubsystem extends Subsystem
    {
    }

    interface Ejb extends SubsystemElement
    {
        public String getComponentClassname();
        public void setComponentClassname(String componentClassname);

        public List<String> getDeclaredRoles();
        public void setDeclaredRoles(List<String> declaredRoles);

        public String getRunAsRole();
        public void setRunAsRole(String runAsRole);

        public String getSecurityDomain();
        public void setSecurityDomain(String securityDomain);
    }

    interface JpaSubsystem extends Subsystem
    {
        enum PersistenceInheritance {DEEP, SHALLOW}

        public String getDefaultDatasource();
        public void setDefaultDataSource(String getDefaultDatasource);

        public PersistenceInheritance getDefaultExtendedPersistenceInheritance();
        public void setDefaultExtendedPersistenceInheritance(PersistenceInheritance persistenceInheritance);

        public boolean isDefaultVfs();
        public void setDefaultVfs(boolean defaultVfs);
    }

    interface PersistenceUnit extends SubsystemElement
    {
        public boolean isEnabled();
        public void setEnabled(boolean enabled);

        public List<String> getEntities();
        public void setEntities(List<String> entities);
    }

    interface WebSubsystemn extends Subsystem
    {
        public String getContextRoot();
        public void setContextRoot(String contextRoot);

        public int getMaxActiveSessions();
        public void setMaxActiveSessions(int maxActiveSessions);

        public String getVirtualHost();
        public void setVirtualHost(String virtualHost);
    }

    interface Servlet extends SubsystemElement
    {
        public String getServletClass();
        public void setServletClass(String servletClass);
    }

    interface WebserviceSubsystem extends Subsystem
    {
    }

    interface Endpoint extends SubsystemElement
    {
        public String getClassname();
        public void setClassname(String classname);

        public String getContext();
        public void setContext(String context);

        public String getType();
        public void setType(String type);

        public String getWsdlUrl();
        public void setWsdlUrl(String wsdlUrl);
    }
}

