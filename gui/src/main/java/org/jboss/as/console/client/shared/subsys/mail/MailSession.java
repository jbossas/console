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

    @Binding(detypedName = "smtp-server")
    String getSmtpServer();
    void setSmtpServer(String server);

    @Binding(detypedName = "imap-server")
    String getImapServer();
    void setImapServer(String server);

    @Binding(detypedName = "pop3-server")
    String getPopServer();
    void setPopServer(String server);

}
