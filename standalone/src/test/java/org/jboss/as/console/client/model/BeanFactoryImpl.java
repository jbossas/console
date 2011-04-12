/*
 * JBoss, Home of Professional Open Source 
 * Copyright <YEAR> Red Hat Inc. and/or its affiliates and other contributors
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
import org.jboss.as.console.client.domain.groups.PropertyRecord;
import org.jboss.as.console.client.domain.groups.deployment.DeploymentReference;
import org.jboss.as.console.client.domain.model.Host;
import org.jboss.as.console.client.domain.model.ProfileRecord;
import org.jboss.as.console.client.domain.model.Server;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.domain.model.ServerInstance;
import org.jboss.as.console.client.server.subsys.threads.ThreadFactoryRecord;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.model.DeploymentRecord;
import org.jboss.as.console.client.shared.model.SubsystemRecord;
import org.jboss.as.console.client.shared.sockets.SocketBinding;
import org.jboss.as.console.client.shared.subsys.jca.model.DataSource;
import org.jboss.as.console.client.shared.subsys.jms.model.JMSEndpoint;
import org.jboss.as.console.client.shared.subsys.logging.model.LoggingHandler;

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
        throw new RuntimeException("not implemented");  
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
    public AutoBean<ServerInstance> serverInstance() {
        return new AutoBeanStub<ServerInstance>(new ServerInstanceImpl());
    }

    @Override
    public AutoBean<ThreadFactoryRecord> threadFactory() {
        throw new RuntimeException("not implemented");  
    }

    @Override
    public AutoBean<DataSource> dataSource() {
        throw new RuntimeException("not implemented");  
    }

    @Override
    public AutoBean<JMSEndpoint> jmsEndpoint() {
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
}
