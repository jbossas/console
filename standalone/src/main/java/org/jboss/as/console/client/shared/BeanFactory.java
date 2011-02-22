package org.jboss.as.console.client.shared;

import com.google.gwt.autobean.shared.AutoBean;
import com.google.gwt.autobean.shared.AutoBeanFactory;
import org.jboss.as.console.client.domain.groups.PropertyRecord;
import org.jboss.as.console.client.domain.model.ProfileRecord;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;

/**
 * @author Heiko Braun
 * @date 2/22/11
 */
public interface BeanFactory extends AutoBeanFactory {

    AutoBean<ProfileRecord> profile();
    AutoBean<ServerGroupRecord> serverGroup();
    AutoBean<PropertyRecord> property();
    AutoBean<DeploymentRecord> deployment();
}
