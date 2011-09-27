package org.jboss.as.console.client.shared.subsys.messaging.model;

import org.jboss.as.console.client.widgets.forms.Address;

/**
 * @author Heiko Braun
 * @date 9/27/11
 */
@Address("/subsystem=messaging/hornetq-server={0}/jms-topic={1}")
public interface Topic extends JMSEndpoint {

}
