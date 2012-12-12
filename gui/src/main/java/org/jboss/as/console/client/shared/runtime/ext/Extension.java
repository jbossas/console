package org.jboss.as.console.client.shared.runtime.ext;

import org.jboss.as.console.client.widgets.forms.Binding;

/**
 * @author Heiko Braun
 * @date 12/6/12
 */
public interface Extension {

    @Binding(key = true)
    String getName();
    void setName(String key);

    String getVersion();
    void setVersion(String version);

    String getSubsystem();
    void setSubsystem(String subsystem);

    String getModule();
    void setModule(String module);

    String getCompatibleVersion();
    void setCompatibleVersion(String version);
}
