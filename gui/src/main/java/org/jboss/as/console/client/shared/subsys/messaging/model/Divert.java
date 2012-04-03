package org.jboss.as.console.client.shared.subsys.messaging.model;

import org.jboss.as.console.client.widgets.forms.Binding;

/**
 * @author Heiko Braun
 * @date 4/3/12
 */
public interface Divert {

    @Binding(detypedName = "routing-name")
    String getRoutingName();
    void setRoutingName(String name);

    @Binding(detypedName = "divert-address")
    String getDivertAddress();
    void setDivertAddress(String divertFrom);

    @Binding(detypedName = "forwarding-address")
    String getForwardingAddress();
    void setForwardingAddress(String divertTo);

    @Binding(detypedName = "filter")
    String getFilter();
    void setFilter(String filterString);

    @Binding(detypedName = "transformer-class-name")
    String getTransformerClass();
    void setTransformerClass(String transformerClass);

    boolean isExclusive();
    void setExclusive(boolean b);
}

