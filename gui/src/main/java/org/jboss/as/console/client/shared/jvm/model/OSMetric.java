package org.jboss.as.console.client.shared.jvm.model;

import org.jboss.as.console.client.widgets.forms.Binding;

/**
 * @author Heiko Braun
 * @date 9/29/11
 */
public interface OSMetric {

    String getName();
    void setName(String name);

    String getVersion();
    void setVersion(String version);

    @Binding(detypedName = "available-processors")
    int getNumProcessors();
    void setNumProcessors(int number);
}
