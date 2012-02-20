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
import com.google.gwt.autobean.shared.AutoBeanFactory.Category;
import org.jboss.as.console.client.core.settings.CommonSettings;
import org.jboss.as.console.client.domain.model.Host;
import org.jboss.as.console.client.domain.model.ProfileRecord;
import org.jboss.as.console.client.domain.model.Server;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.domain.model.ServerInstance;
import org.jboss.as.console.client.shared.deployment.DeploymentReference;
import org.jboss.as.console.client.shared.general.model.Interface;
import org.jboss.as.console.client.shared.general.model.SocketBinding;
import org.jboss.as.console.client.shared.general.model.SocketGroup;
import org.jboss.as.console.client.shared.jvm.Jvm;
import org.jboss.as.console.client.shared.jvm.model.HeapMetric;
import org.jboss.as.console.client.shared.jvm.model.OSMetric;
import org.jboss.as.console.client.shared.jvm.model.RuntimeMetric;
import org.jboss.as.console.client.shared.jvm.model.ThreadMetric;
import org.jboss.as.console.client.shared.model.DeploymentRecord;
import org.jboss.as.console.client.shared.model.SubsystemRecord;
import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.jboss.as.console.client.shared.properties.PropertyRecordCategory;
import org.jboss.as.console.client.shared.runtime.jpa.model.JPADeployment;
import org.jboss.as.console.client.shared.subsys.configadmin.model.ConfigAdminData;
import org.jboss.as.console.client.shared.subsys.deploymentscanner.model.DeploymentScanner;
import org.jboss.as.console.client.shared.subsys.ejb3.model.AsyncService;
import org.jboss.as.console.client.shared.subsys.ejb3.model.EESubsystem;
import org.jboss.as.console.client.shared.subsys.ejb3.model.EJB3Subsystem;
import org.jboss.as.console.client.shared.subsys.ejb3.model.EJB3ThreadPool;
import org.jboss.as.console.client.shared.subsys.ejb3.model.Module;
import org.jboss.as.console.client.shared.subsys.ejb3.model.RemoteService;
import org.jboss.as.console.client.shared.subsys.ejb3.model.StrictMaxBeanPool;
import org.jboss.as.console.client.shared.subsys.ejb3.model.TimerService;
import org.jboss.as.console.client.shared.subsys.infinispan.model.CacheContainer;
import org.jboss.as.console.client.shared.subsys.infinispan.model.DefaultCacheContainer;
import org.jboss.as.console.client.shared.subsys.infinispan.model.DistributedCache;
import org.jboss.as.console.client.shared.subsys.infinispan.model.InvalidationCache;
import org.jboss.as.console.client.shared.subsys.infinispan.model.LocalCache;
import org.jboss.as.console.client.shared.subsys.infinispan.model.ReplicatedCache;
import org.jboss.as.console.client.shared.subsys.jacorb.model.JacOrbSubsystem;
import org.jboss.as.console.client.shared.subsys.jca.JcaBeanValidation;
import org.jboss.as.console.client.shared.subsys.jca.model.AdminObject;
import org.jboss.as.console.client.shared.subsys.jca.model.ConnectionDefinition;
import org.jboss.as.console.client.shared.subsys.jca.model.DataSource;
import org.jboss.as.console.client.shared.subsys.jca.model.JDBCDriver;
import org.jboss.as.console.client.shared.subsys.jca.model.JcaArchiveValidation;
import org.jboss.as.console.client.shared.subsys.jca.model.JcaBootstrapContext;
import org.jboss.as.console.client.shared.subsys.jca.model.JcaConnectionManager;
import org.jboss.as.console.client.shared.subsys.jca.model.JcaWorkmanager;
import org.jboss.as.console.client.shared.subsys.jca.model.PoolConfig;
import org.jboss.as.console.client.shared.subsys.jca.model.ResourceAdapter;
import org.jboss.as.console.client.shared.subsys.jca.model.WorkmanagerPool;
import org.jboss.as.console.client.shared.subsys.jca.model.XADataSource;
import org.jboss.as.console.client.shared.subsys.jgroups.JGroupsProtocol;
import org.jboss.as.console.client.shared.subsys.jgroups.JGroupsStack;
import org.jboss.as.console.client.shared.subsys.jgroups.JGroupsTransport;
import org.jboss.as.console.client.shared.subsys.jmx.model.JMXSubsystem;
import org.jboss.as.console.client.shared.subsys.jpa.model.JpaSubsystem;
import org.jboss.as.console.client.shared.subsys.logging.model.AsyncHandler;
import org.jboss.as.console.client.shared.subsys.logging.model.ConsoleHandler;
import org.jboss.as.console.client.shared.subsys.logging.model.CustomHandler;
import org.jboss.as.console.client.shared.subsys.logging.model.FileHandler;
import org.jboss.as.console.client.shared.subsys.logging.model.Logger;
import org.jboss.as.console.client.shared.subsys.logging.model.PeriodicRotatingFileHandler;
import org.jboss.as.console.client.shared.subsys.logging.model.RootLogger;
import org.jboss.as.console.client.shared.subsys.logging.model.SizeRotatingFileHandler;
import org.jboss.as.console.client.shared.subsys.mail.MailServerDefinition;
import org.jboss.as.console.client.shared.subsys.mail.MailSession;
import org.jboss.as.console.client.shared.subsys.messaging.model.AddressingPattern;
import org.jboss.as.console.client.shared.subsys.messaging.model.ConnectionFactory;
import org.jboss.as.console.client.shared.subsys.messaging.model.MessagingProvider;
import org.jboss.as.console.client.shared.subsys.messaging.model.Queue;
import org.jboss.as.console.client.shared.subsys.messaging.model.SecurityPattern;
import org.jboss.as.console.client.shared.subsys.messaging.model.Topic;
import org.jboss.as.console.client.shared.subsys.osgi.config.model.OSGiCapability;
import org.jboss.as.console.client.shared.subsys.osgi.config.model.OSGiSubsystem;
import org.jboss.as.console.client.shared.subsys.osgi.runtime.model.OSGiBundle;
import org.jboss.as.console.client.shared.subsys.osgi.runtime.model.OSGiFramework;
import org.jboss.as.console.client.shared.subsys.security.model.AuthenticationLoginModule;
import org.jboss.as.console.client.shared.subsys.security.model.AuthorizationPolicyProvider;
import org.jboss.as.console.client.shared.subsys.security.model.GenericSecurityDomainData;
import org.jboss.as.console.client.shared.subsys.security.model.MappingModule;
import org.jboss.as.console.client.shared.subsys.security.model.SecurityDomain;
import org.jboss.as.console.client.shared.subsys.security.model.SecuritySubsystem;
import org.jboss.as.console.client.shared.subsys.threads.model.BlockingBoundedQueueThreadPool;
import org.jboss.as.console.client.shared.subsys.threads.model.BlockingQueuelessThreadPool;
import org.jboss.as.console.client.shared.subsys.threads.model.BoundedQueueThreadPool;
import org.jboss.as.console.client.shared.subsys.threads.model.QueuelessThreadPool;
import org.jboss.as.console.client.shared.subsys.threads.model.ScheduledThreadPool;
import org.jboss.as.console.client.shared.subsys.threads.model.ThreadFactory;
import org.jboss.as.console.client.shared.subsys.threads.model.UnboundedQueueThreadPool;
import org.jboss.as.console.client.shared.subsys.tx.model.TransactionManager;
import org.jboss.as.console.client.shared.subsys.web.model.HttpConnector;
import org.jboss.as.console.client.shared.subsys.web.model.JSPContainerConfiguration;
import org.jboss.as.console.client.shared.subsys.web.model.VirtualServer;
import org.jboss.as.console.client.shared.subsys.ws.model.WebServiceEndpoint;
import org.jboss.as.console.client.shared.subsys.ws.model.WebServiceProvider;
import org.jboss.as.console.client.standalone.StandaloneServer;


/**
 * @author Heiko Braun
 * @date 2/22/11
 */
@Category(PropertyRecordCategory.class)
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
    AutoBean<ConnectionDefinition> ConnectionDefinition();
    AutoBean<AdminObject> AdminObject();
    AutoBean<PoolConfig> poolConfig();

    AutoBean<Queue> queue();
    AutoBean<Topic> topic();
    AutoBean<ConnectionFactory> connectionFactory();

    AutoBean<EJB3Subsystem> ejb3Subsystem();
    AutoBean<StrictMaxBeanPool> strictMaxBeanPool();
    AutoBean<AsyncService> asyncService();
    AutoBean<TimerService> timerService();
    AutoBean<RemoteService> remoteService();
    AutoBean<EJB3ThreadPool> ejb3ThreadPool();

    // logging subsystem
    AutoBean<RootLogger> rootLogger();
    AutoBean<Logger> logger();
    AutoBean<AsyncHandler> asyncHandler();
    AutoBean<ConsoleHandler> consoleHanlder();
    AutoBean<FileHandler> fileHandler();
    AutoBean<PeriodicRotatingFileHandler> periodicRotatingFileHandler();
    AutoBean<SizeRotatingFileHandler> sizeRotatingFileHandler();
    AutoBean<CustomHandler> customHandler();

    AutoBean<DeploymentScanner> deploymentScanner();
    AutoBean<SocketBinding> socketBinding();
    AutoBean<SocketGroup> socketGroup();
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
    AutoBean<WebServiceProvider> WebServiceProvider();

    AutoBean<ConfigAdminData> configAdminData();
    AutoBean<OSGiSubsystem> osgiSubsystem();
    AutoBean<OSGiCapability> osgiCapability();
    AutoBean<OSGiFramework> osgiFramework();
    AutoBean<OSGiBundle> osgiBundle();

    AutoBean<HeapMetric> heapMetric();
    AutoBean<ThreadMetric> threadMetric();
    AutoBean<RuntimeMetric> runtime();
    AutoBean<OSMetric> osmetric();

    // Infinispan subsystem
    AutoBean<CacheContainer> cacheContainer();
    AutoBean<DefaultCacheContainer> defaultCacheContainer();
    AutoBean<LocalCache> localCache();
    AutoBean<InvalidationCache> invalidationCache();
    AutoBean<ReplicatedCache> replicatedCache();
    AutoBean<DistributedCache> distributedCache();

    // Thread subsystem
    AutoBean<ThreadFactory> threadFactory();
    AutoBean<BoundedQueueThreadPool> boundedQueueThreadPool();
    AutoBean<BlockingBoundedQueueThreadPool> blockingBoundedQueueThreadPool();
    AutoBean<UnboundedQueueThreadPool> unboundedQueueThreadPool();
    AutoBean<QueuelessThreadPool> queuelessThreadPool();
    AutoBean<BlockingQueuelessThreadPool> blockingQueuelessThreadPool();
    AutoBean<ScheduledThreadPool> scheduledThreadPool();

    AutoBean<TransactionManager> transactionManager();
    AutoBean<SecuritySubsystem> securitySubsystem();
    AutoBean<SecurityDomain> securityDomain();
    AutoBean<AuthenticationLoginModule> authenticationLoginModule();
    AutoBean<AuthorizationPolicyProvider> authorizationPolicyModule();

    AutoBean<MappingModule> mappingModule();
    AutoBean<GenericSecurityDomainData> genericSecurityDomainData();

    AutoBean<JpaSubsystem> jpaSubystem();
    AutoBean<MailSession> mailSession();
    AutoBean<MailServerDefinition> mailServerDefinition();
    AutoBean<JMXSubsystem> jmxSubsystem();
    AutoBean<EESubsystem> eeSubsystem();
    AutoBean<Module> eeModuleRef();

    AutoBean<JcaArchiveValidation> JcaArchiveValidation();
    AutoBean<JcaBootstrapContext> JcaBootstrapContext();
    AutoBean<JcaBeanValidation> JcaBeanValidation();
    AutoBean<JcaWorkmanager> JcaWorkmanager();
    AutoBean<WorkmanagerPool> WorkmanagerPool();
    AutoBean<JcaConnectionManager> JcaConnectionManager();


    AutoBean<JacOrbSubsystem> jacORBSubsystem();
    AutoBean<JPADeployment> jpaDeployment();

    AutoBean<JGroupsStack> jGroupsStack();
    AutoBean<JGroupsProtocol> jGroupsProtocol();
    AutoBean<JGroupsTransport> jGroupsTransport();
}
