package org.jboss.as.console.client.shared.subsys.ws.model;

import org.jboss.as.console.client.widgets.forms.Address;
import org.jboss.as.console.client.widgets.forms.Binding;

/**
 * @author Heiko Braun
 * @date 1/11/12
 */
@Address("/subsystem=webservices")
public interface WebServiceProvider {

    @Binding(detypedName = "modify-wsdl-address")
    boolean isModifyAddress();
    void setModifyAddress(boolean b);

    @Binding(detypedName = "wsdl-host",expr = true)
    String getWsdlHost();
    void setWsdlHost(String host);

    @Binding(detypedName = "wsdl-port",expr = true)
    int getWsdlPort();
    void setWsdlPort(int port);

    @Binding(detypedName = "wsdl-secure-port",expr = true)
    int getWsdlSecurePort();
    void setWsdlSecurePort(int port);
}
