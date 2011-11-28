package org.jboss.as.console.client.shared.subsys.ejb3.model;

import org.jboss.as.console.client.widgets.forms.Address;
import org.jboss.as.console.client.widgets.forms.Binding;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 11/28/11
 */
@Address("/subsystem=ee")
public interface EESubsystem {

    @Binding(detypedName = "ear-subdeployments-isolated")
    boolean isIsolatedSubdeployments();
    void setIsolatedSubdeployments(boolean b);

    @Binding(skip = true)
    List<Module> getModules();
    void setModules(List<Module> modules);
}
