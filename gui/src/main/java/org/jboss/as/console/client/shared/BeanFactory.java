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
import org.jboss.as.console.client.shared.jvm.model.HeapMetric;
import org.jboss.as.console.client.shared.jvm.model.OSMetric;
import org.jboss.as.console.client.shared.jvm.model.RuntimeMetric;
import org.jboss.as.console.client.shared.jvm.model.ThreadMetric;
import org.jboss.as.console.client.shared.model.DeploymentRecord;
import org.jboss.as.console.client.shared.model.SubsystemRecord;
import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.jboss.as.console.client.shared.subsys.deploymentscanner.model.DeploymentScanner;
import org.jboss.as.console.client.shared.subsys.ejb3.model.AsyncService;
import org.jboss.as.console.client.shared.subsys.ejb3.model.EJB3Subsystem;
import org.jboss.as.console.client.shared.subsys.ejb3.model.RemoteService;
import org.jboss.as.console.client.shared.subsys.ejb3.model.StrictMaxBeanPool;
import org.jboss.as.console.client.shared.subsys.ejb3.model.ThreadPool;
import org.jboss.as.console.client.shared.subsys.ejb3.model.TimerService;
import org.jboss.as.console.client.shared.subsys.infinispan.model.CacheContainer;
import org.jboss.as.console.client.shared.subsys.jca.model.DataSource;
import org.jboss.as.console.client.shared.subsys.jca.model.JDBCDriver;
import org.jboss.as.console.client.shared.subsys.jca.model.PoolConfig;
import org.jboss.as.console.client.shared.subsys.jca.model.ResourceAdapter;
import org.jboss.as.console.client.shared.subsys.jca.model.XADataSource;
import org.jboss.as.console.client.shared.subsys.logging.model.LoggerConfig;
import org.jboss.as.console.client.shared.subsys.logging.model.LoggingHandler;
import org.jboss.as.console.client.shared.subsys.messaging.model.AddressingPattern;
import org.jboss.as.console.client.shared.subsys.messaging.model.ConnectionFactory;
import org.jboss.as.console.client.shared.subsys.messaging.model.MessagingProvider;
import org.jboss.as.console.client.shared.subsys.messaging.model.Queue;
import org.jboss.as.console.client.shared.subsys.messaging.model.SecurityPattern;
import org.jboss.as.console.client.shared.subsys.messaging.model.Topic;
import org.jboss.as.console.client.shared.subsys.osgi.config.model.OSGiCapability;
import org.jboss.as.console.client.shared.subsys.osgi.config.model.OSGiConfigAdminData;
import org.jboss.as.console.client.shared.subsys.osgi.config.model.OSGiSubsystem;
import org.jboss.as.console.client.shared.subsys.osgi.runtime.model.OSGiBundle;
import org.jboss.as.console.client.shared.subsys.osgi.runtime.model.OSGiFramework;
import org.jboss.as.console.client.shared.subsys.threads.model.BoundedQueueThreadPool;
import org.jboss.as.console.client.shared.subsys.threads.model.ThreadFactory;
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
    AutoBean<Topic> topic();
    AutoBean<ConnectionFactory> connectionFactory();

    AutoBean<EJB3Subsystem> ejb3Subsystem();
    AutoBean<StrictMaxBeanPool> strictMaxBeanPool();
    AutoBean<AsyncService> asyncService();
    AutoBean<TimerService> timerService();
    AutoBean<ThreadPool> ejbThreadPool();
    AutoBean<RemoteService> remoteService();

    AutoBean<LoggingHandler> loggingHandler();
    AutoBean<LoggerConfig> loggerConfig();
    AutoBean<DeploymentScanner> deploymentScanner();
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
    AutoBean<OSGiCapability> osgiCapability();
    AutoBean<OSGiConfigAdminData> osgiConfigAdminData();
    AutoBean<OSGiFramework> osgiFramework();
    AutoBean<OSGiBundle> osgiBundle();

    AutoBean<HeapMetric> heapMetric();
    AutoBean<ThreadMetric> threadMetric();
    AutoBean<RuntimeMetric> runtime();
    AutoBean<OSMetric> osmetric();

    AutoBean<CacheContainer> cacheContainer();

    AutoBean<ThreadFactory> threadFactory();
    AutoBean<BoundedQueueThreadPool> boundedQueueThreadPool();
}
