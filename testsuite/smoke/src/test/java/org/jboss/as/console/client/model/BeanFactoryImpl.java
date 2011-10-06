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

package org.jboss.as.console.client.model;

import com.google.gwt.autobean.shared.AutoBean;

import org.jboss.as.console.client.core.settings.CommonSettings;
import org.jboss.as.console.client.domain.model.Host;
import org.jboss.as.console.client.domain.model.ProfileRecord;
import org.jboss.as.console.client.domain.model.Server;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.domain.model.ServerInstance;
import org.jboss.as.console.client.shared.BeanFactory;
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
import org.jboss.as.console.client.shared.subsys.ejb.mdb.model.MessageDrivenBeans;
import org.jboss.as.console.client.shared.subsys.ejb.pool.model.EJBPool;
import org.jboss.as.console.client.shared.subsys.ejb.service.model.TimerService;
import org.jboss.as.console.client.shared.subsys.ejb.session.model.SessionBeans;
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
import org.jboss.as.console.client.shared.subsys.osgi.model.OSGiCapability;
import org.jboss.as.console.client.shared.subsys.osgi.model.OSGiConfigAdminData;
import org.jboss.as.console.client.shared.subsys.osgi.model.OSGiSubsystem;
import org.jboss.as.console.client.shared.subsys.threads.model.BoundedQueueThreadPool;
import org.jboss.as.console.client.shared.subsys.web.model.HttpConnector;
import org.jboss.as.console.client.shared.subsys.web.model.JSPContainerConfiguration;
import org.jboss.as.console.client.shared.subsys.web.model.VirtualServer;
import org.jboss.as.console.client.shared.subsys.ws.model.WebServiceEndpoint;
import org.jboss.as.console.client.standalone.StandaloneServer;

/**
 * @author Heiko Braun
 * @date 4/12/11
 */
public class BeanFactoryImpl implements BeanFactory {
    @Override
    public AutoBean<ProfileRecord> profile() {
        return new AutoBeanStub<ProfileRecord>(new ProfileRecordImpl());
    }

    @Override
    public AutoBean<SubsystemRecord> subsystem() {
        return new AutoBeanStub<SubsystemRecord>(new SubsystemImpl());
    }

    @Override
    public AutoBean<ServerGroupRecord> serverGroup() {
        return new AutoBeanStub<ServerGroupRecord>(new ServerGroupImpl());
    }

    @Override
    public AutoBean<PropertyRecord> property() {
        return new AutoBeanStub<PropertyRecord>(new PropertyImpl());
    }

    @Override
    public AutoBean<DeploymentRecord> deployment() {
        return new AutoBeanStub<DeploymentRecord>(new DeploymentRecordImpl());
    }

    @Override
    public AutoBean<Host> host() {
        return new AutoBeanStub<Host>(new HostImpl());
    }

    @Override
    public AutoBean<Server> server() {
        return new AutoBeanStub<Server>(new ServerImpl());
    }

    @Override
    public AutoBean<Jvm> jvm() {
        return new AutoBeanStub<Jvm>(new JvmImpl());
    }

    @Override
    public AutoBean<ServerInstance> serverInstance() {
        return new AutoBeanStub<ServerInstance>(new ServerInstanceImpl());
    }

    @Override
    public AutoBean<DataSource> dataSource() {
        return new AutoBeanStub<DataSource>(new DataSourceImpl());
    }

    public AutoBean<PoolConfig> poolConfig() {
        throw new RuntimeException("not implemented");
    }

    @Override
    public AutoBean<SessionBeans> sessionBeans() {
        throw new RuntimeException("not implemented");
    }

    @Override
    public AutoBean<MessageDrivenBeans> messageDrivenBeans() {
        throw new RuntimeException("not implemented");
    }

    @Override
    public AutoBean<EJBPool> ejbPool() {
        throw new RuntimeException("not implemented");
    }

    @Override
    public AutoBean<TimerService> timerService() {
        throw new RuntimeException("not implemented");
    }

    @Override
    public AutoBean<LoggingHandler> loggingHandler() {
        throw new RuntimeException("not implemented");
    }

    @Override
    public AutoBean<SocketBinding> socketBinding() {
        throw new RuntimeException("not implemented");
    }

    @Override
    public AutoBean<DeploymentReference> deploymentReference() {
        throw new RuntimeException("not implemented");
    }

    @Override
    public <T> AutoBean<T> create(Class<T> clazz) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public <T, U extends T> AutoBean<T> create(Class<T> clazz, U delegate) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public AutoBean<CommonSettings> settings() {
        throw new RuntimeException("not implemented");
    }

    @Override
    public AutoBean<XADataSource> xaDataSource() {
        throw new RuntimeException("not implemented");
    }

    @Override
    public AutoBean<MessagingProvider> messagingProvider() {
        throw new RuntimeException("not implemented");
    }

    @Override
    public AutoBean<SecurityPattern> messagingSecurity() {
        throw new RuntimeException("not implemented");
    }

    @Override
    public AutoBean<Queue> queue() {
        throw new RuntimeException("not implemented");
    }

    @Override
    public AutoBean<Topic> topic() {
        throw new RuntimeException("not implemented");
    }

    @Override
    public AutoBean<ConnectionFactory> connectionFactory() {
        throw new RuntimeException("not implemented");
    }

    @Override
    public AutoBean<AddressingPattern> messagingAddress() {
        throw new RuntimeException("not implemented");
    }

    @Override
    public AutoBean<HttpConnector> httpConnector() {
        throw new RuntimeException("not implemented");
    }

    @Override
    public AutoBean<JSPContainerConfiguration> jspConfig() {
        throw new RuntimeException("not implemented");
    }

    @Override
    public AutoBean<VirtualServer> virtualServer() {
        throw new RuntimeException("not implemented");
    }


    public AutoBean<Interface> interfaceDeclaration() {
        throw new RuntimeException("not implemented");
    }

    public AutoBean<JDBCDriver> jdbcDriver() {
        throw new RuntimeException("not implemented");

    }

    @Override
    public AutoBean<LoggerConfig> loggerConfig() {
        throw new RuntimeException("not implemented");
    }

    @Override
    public AutoBean<StandaloneServer> standaloneServer() {
         throw new RuntimeException("not implemented");
    }

    @Override
    public AutoBean<WebServiceEndpoint> webServiceEndpoint() {
        throw new RuntimeException("not implemented");
    }

    @Override
    public AutoBean<ResourceAdapter> resourceAdapter() {
        throw new RuntimeException("not implemented");
    }

    @Override
    public AutoBean<OSGiSubsystem> osgiSubsystem() {
        throw new RuntimeException("not implemented");
    }

    @Override
    public AutoBean<OSGiCapability> osgiCapability() {
        throw new RuntimeException("not implemented");
    }

    @Override
    public AutoBean<OSGiConfigAdminData> osgiConfigAdminData() {
        throw new RuntimeException("not implemented");
    }

    @Override
    public AutoBean<DeploymentScanner> deploymentScanner() {
        throw new RuntimeException("not implemented");
    }

    public AutoBean<CacheContainer> cacheContainer() {
        throw new RuntimeException("not implemented");
    }

    public AutoBean<HeapMetric> heapMetric() {
        throw new RuntimeException("not implemented");
    }

    public AutoBean<ThreadMetric> threadMetric(){
        throw new RuntimeException("not implemented");
    }

    public AutoBean<RuntimeMetric> runtime(){
        throw new RuntimeException("not implemented");
    }

    public AutoBean<OSMetric> osmetric(){
        throw new RuntimeException("not implemented");
    }

    @Override
    public AutoBean<BoundedQueueThreadPool> boundedQueueThreadPool() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

