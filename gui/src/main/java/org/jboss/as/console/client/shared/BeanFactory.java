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

import org.jboss.as.console.client.core.settings.CommonSettings;
import org.jboss.as.console.client.domain.model.Host;
import org.jboss.as.console.client.domain.model.ProfileRecord;
import org.jboss.as.console.client.domain.model.Server;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.domain.model.ServerInstance;
import org.jboss.as.console.client.shared.deployment.DeploymentReference;
import org.jboss.as.console.client.shared.general.model.Interface;
import org.jboss.as.console.client.shared.general.model.SocketBinding;
import org.jboss.as.console.client.shared.jvm.Jvm;
import org.jboss.as.console.client.shared.model.DeploymentRecord;
import org.jboss.as.console.client.shared.model.SubsystemRecord;
import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.jboss.as.console.client.shared.subsys.ejb.mdb.model.MessageDrivenBeans;
import org.jboss.as.console.client.shared.subsys.ejb.pool.model.EJBPool;
import org.jboss.as.console.client.shared.subsys.ejb.session.model.SessionBeans;
import org.jboss.as.console.client.shared.subsys.jca.model.DataSource;
import org.jboss.as.console.client.shared.subsys.jca.model.JDBCDriver;
import org.jboss.as.console.client.shared.subsys.jca.model.PoolConfig;
import org.jboss.as.console.client.shared.subsys.jca.model.ResourceAdapter;
import org.jboss.as.console.client.shared.subsys.jca.model.XADataSource;
import org.jboss.as.console.client.shared.subsys.logging.model.LoggerConfig;
import org.jboss.as.console.client.shared.subsys.logging.model.LoggingHandler;
import org.jboss.as.console.client.shared.subsys.messaging.model.AddressingPattern;
import org.jboss.as.console.client.shared.subsys.messaging.model.ConnectionFactory;
import org.jboss.as.console.client.shared.subsys.messaging.model.JMSEndpoint;
import org.jboss.as.console.client.shared.subsys.messaging.model.MessagingProvider;
import org.jboss.as.console.client.shared.subsys.messaging.model.Queue;
import org.jboss.as.console.client.shared.subsys.messaging.model.SecurityPattern;
import org.jboss.as.console.client.shared.subsys.osgi.model.OSGiConfigAdminData;
import org.jboss.as.console.client.shared.subsys.osgi.model.OSGiPreloadedModule;
import org.jboss.as.console.client.shared.subsys.osgi.model.OSGiSubsystem;
import org.jboss.as.console.client.shared.subsys.web.model.HttpConnector;
import org.jboss.as.console.client.shared.subsys.web.model.JSPContainerConfiguration;
import org.jboss.as.console.client.shared.subsys.web.model.VirtualServer;
import org.jboss.as.console.client.shared.subsys.ws.model.WebServiceEndpoint;
import org.jboss.as.console.client.standalone.StandaloneServer;


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

    AutoBean<DataSource> dataSource();
    AutoBean<XADataSource> xaDataSource();
    AutoBean<ResourceAdapter> resourceAdapter();
    AutoBean<PoolConfig> poolConfig();

    AutoBean<Queue> queue();
    AutoBean<JMSEndpoint> topic();
    AutoBean<ConnectionFactory> connectionFactory();

    AutoBean<SessionBeans> sessionBeans();
    AutoBean<MessageDrivenBeans> messageDrivenBeans();
    AutoBean<EJBPool> ejbPool();

    AutoBean<LoggingHandler> loggingHandler();
    AutoBean<LoggerConfig> loggerConfig();
    AutoBean<SocketBinding> socketBinding();
    AutoBean<DeploymentReference> deploymentReference();

    AutoBean<CommonSettings> settings();
    AutoBean<MessagingProvider> messagingProvider();
    AutoBean<SecurityPattern> messagingSecurity();
    AutoBean<AddressingPattern> messagingAddress();

    AutoBean<HttpConnector> httpConnector();
    AutoBean<JSPContainerConfiguration> jspConfig();
    AutoBean<VirtualServer> virtualServer();

    AutoBean<Interface> interfaceDeclaration();
    AutoBean<JDBCDriver> jdbcDriver();

    AutoBean<StandaloneServer> standaloneServer();
    AutoBean<WebServiceEndpoint> webServiceEndpoint();

    AutoBean<OSGiSubsystem> osgiSubsystem();
    AutoBean<OSGiPreloadedModule> osgiPreloadedModule();
    AutoBean<OSGiConfigAdminData> osgiConfigAdminData();
}
