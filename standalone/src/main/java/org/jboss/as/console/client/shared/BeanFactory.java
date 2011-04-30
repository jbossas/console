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

import com.google.gwt.autobean.shared.AutoBean;
import com.google.gwt.autobean.shared.AutoBeanFactory;
import org.jboss.as.console.client.domain.groups.PropertyRecord;
import org.jboss.as.console.client.shared.deployment.DeploymentReference;
import org.jboss.as.console.client.domain.model.Host;
import org.jboss.as.console.client.domain.model.Jvm;
import org.jboss.as.console.client.domain.model.ProfileRecord;
import org.jboss.as.console.client.domain.model.Server;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.domain.model.ServerInstance;
import org.jboss.as.console.client.server.subsys.threads.ThreadFactoryRecord;
import org.jboss.as.console.client.shared.model.DeploymentRecord;
import org.jboss.as.console.client.shared.model.SubsystemRecord;
import org.jboss.as.console.client.shared.sockets.SocketBinding;
import org.jboss.as.console.client.shared.subsys.jca.model.DataSource;
import org.jboss.as.console.client.shared.subsys.jms.model.JMSEndpoint;
import org.jboss.as.console.client.shared.subsys.logging.model.LoggingHandler;


/**
 * @author Heiko Braun
 * @date 2/22/11
 */
public interface BeanFactory extends AutoBeanFactory {

    AutoBean<ProfileRecord> profile();
    AutoBean<SubsystemRecord> subsystem();
    AutoBean<ServerGroupRecord> serverGroup();
    AutoBean<PropertyRecord> property();
    AutoBean<DeploymentRecord> deployment();
    AutoBean<Host> host();
    AutoBean<Server> server();
    AutoBean<Jvm> jvm();
    AutoBean<ServerInstance> serverInstance();
    AutoBean<ThreadFactoryRecord> threadFactory();

    AutoBean<DataSource> dataSource();
    AutoBean<JMSEndpoint> jmsEndpoint();
    AutoBean<LoggingHandler> loggingHandler();
    AutoBean<SocketBinding> socketBinding();
    AutoBean<DeploymentReference> deploymentReference();
}
