package org.jboss.as.console.client.shared.subsys.ws.model;

import org.jboss.ballroom.client.widgets.forms.Binding;

/**
 * @author Heiko Braun
 * @date 6/10/11
 */
public interface WebServiceEndpoint {

    String getName();
    void setName(String name);

    String getContext();
    void setContext(String context);

    String getClassName();
    void setClassName(String classname);

    String getType();
    void setType(String type);

    @Binding(detypedName = "wsdl-url")
    String getWsdl();
    void setWsdl(String wsdl);

}
