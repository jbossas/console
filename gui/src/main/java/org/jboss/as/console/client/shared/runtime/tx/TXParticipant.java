package org.jboss.as.console.client.shared.runtime.tx;

import org.jboss.as.console.client.widgets.forms.Binding;

/**
 * @author Heiko Braun
 * @date 2/27/13
 */
public interface TXParticipant{


    @Binding(skip = true)
    String getId();
    void setId(String id);

    @Binding(detypedName = "jndi-name")
    String getJndiName();
    void setJndiName(String jndiName);

    @Binding(detypedName = "jmx-name")
    String getJmxName();
    void setJmxName(String jmxName);

    @Binding(detypedName = "eis-product-name")
    String getEisName();
    void setEisName(String eisName);

    String getStatus();
    void setStatus(String status);

    String getType();
    void setType(String type);

    @Binding(skip = true)
    String getLog();
    void setLog(String id);

}
