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
import org.jboss.as.console.client.domain.general.model.Interface;
import org.jboss.as.console.client.shared.jvm.Jvm;
import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.jboss.as.console.client.domain.model.Host;
import org.jboss.as.console.client.domain.model.ProfileRecord;
import org.jboss.as.console.client.domain.model.Server;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.domain.model.ServerInstance;
import org.jboss.as.console.client.server.subsys.threads.ThreadFactoryRecord;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.deployment.DeploymentReference;
import org.jboss.as.console.client.shared.model.DeploymentRecord;
import org.jboss.as.console.client.shared.model.SubsystemRecord;
import org.jboss.as.console.client.shared.sockets.SocketBinding;
import org.jboss.as.console.client.shared.subsys.jca.model.DataSource;
import org.jboss.as.console.client.shared.subsys.jca.model.XADataSource;
import org.jboss.as.console.client.shared.subsys.messaging.model.AddressingPattern;
import org.jboss.as.console.client.shared.subsys.messaging.model.ConnectionFactory;
import org.jboss.as.console.client.shared.subsys.messaging.model.JMSEndpoint;
import org.jboss.as.console.client.shared.subsys.logging.model.LoggingHandler;
import org.jboss.as.console.client.shared.subsys.messaging.model.MessagingProvider;
import org.jboss.as.console.client.shared.subsys.messaging.model.Queue;
import org.jboss.as.console.client.shared.subsys.messaging.model.SecurityPattern;
import org.jboss.as.console.client.shared.subsys.web.model.HttpConnector;
import org.jboss.as.console.client.shared.subsys.web.model.JSPContainerConfiguration;
import org.jboss.as.console.client.shared.subsys.web.model.VirtualServer;

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
    public AutoBean<ThreadFactoryRecord> threadFactory() {
        throw new RuntimeException("not implemented");  
    }

    @Override
    public AutoBean<DataSource> dataSource() {
        return new AutoBeanStub<DataSource>(new DataSourceImpl());
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
    public AutoBean<JMSEndpoint> topic() {
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

    @Override
    public AutoBean<Interface> interfaceDeclaration() {
        throw new RuntimeException("not implemented");
    }
}

