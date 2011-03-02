package org.jboss.as.console.client.shared;

import com.google.gwt.autobean.shared.AutoBean;
import com.google.gwt.autobean.shared.AutoBeanFactory;
import org.jboss.as.console.client.domain.groups.PropertyRecord;
import org.jboss.as.console.client.domain.model.Host;
import org.jboss.as.console.client.domain.model.ProfileRecord;
import org.jboss.as.console.client.domain.model.Server;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.server.subsys.threads.ThreadFactoryRecord;


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
    AutoBean<Server> serverInstance();

    AutoBean<ThreadFactoryRecord> threadFactory();
}
