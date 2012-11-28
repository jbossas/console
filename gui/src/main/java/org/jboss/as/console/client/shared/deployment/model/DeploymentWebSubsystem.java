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

import org.jboss.as.console.client.widgets.forms.Binding;

/**
* @author Harald Pehl
* @date 11/28/2012
*/
public interface DeploymentWebSubsystem extends DeploymentSubsystem
{
    @Binding(detypedName = "context-root")
    String getContextRoot();
    void setContextRoot(String contextRoot);

    @Binding(detypedName = "max-active-sessions")
    int getMaxActiveSessions();
    void setMaxActiveSessions(int maxActiveSessions);

    @Binding(detypedName = "virtual-host")
    String getVirtualHost();
    void setVirtualHost(String virtualHost);
}
