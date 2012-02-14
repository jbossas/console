package org.jboss.as.console.client.shared.subsys.mail;

/**
 * @author Heiko Braun
 * @date 11/28/11
 */

import org.jboss.as.console.client.widgets.forms.Address;
import org.jboss.as.console.client.widgets.forms.Binding;

@Address("/subsystem=mail/mail-session={0}")
public interface MailSession {

    @Binding(detypedName = "jndi-name")
    String getJndiName();
    void setJndiName(String jndiName);

    String getFrom();
    void setFrom(String jndiName);

    boolean isDebug();
    void setDebug(boolean debug);

    @Binding(skip = true)
    MailServerDefinition getSmtpServer();
    void setSmtpServer(MailServerDefinition server);

    @Binding(skip=true)
    MailServerDefinition getImapServer();
    void setImapServer(MailServerDefinition server);

    @Binding(skip=true)
    MailServerDefinition getPopServer();
    void setPopServer(MailServerDefinition server);

}
