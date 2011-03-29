package org.jboss.as.console.client.shared;

import com.google.gwt.autobean.shared.AutoBean;
import com.google.gwt.autobean.shared.AutoBeanFactory;
import org.jboss.as.console.client.domain.groups.PropertyRecord;
import org.jboss.as.console.client.domain.model.*;
import org.jboss.as.console.client.server.subsys.threads.ThreadFactoryRecord;
import org.jboss.as.console.client.shared.model.DeploymentRecord;
import org.jboss.as.console.client.shared.model.SubsystemRecord;
import org.jboss.as.console.client.shared.subsys.jca.model.DataSource;
import org.jboss.as.console.client.shared.subsys.jms.model.JMSEndpoint;


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
    AutoBean<ServerInstance> serverInstance();
    AutoBean<ThreadFactoryRecord> threadFactory();

    AutoBean<DataSource> dataSource();
    AutoBean<JMSEndpoint> jmsEndpoint();
}
