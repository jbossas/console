package org.jboss.as.console.client.shared.subsys.jca.model;

import org.jboss.as.console.client.widgets.forms.Address;
import org.jboss.as.console.client.widgets.forms.Binding;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 11/29/11
 */
@Address("/subsystem=jca/workmanager={0}")
public interface JcaWorkmanager {

    String getName();
    void setName(String name);

    @Binding(skip = true)
    List<WorkmanagerPool> getShortRunning();
    void setShortRunning(List<WorkmanagerPool> pools);

    @Binding(skip = true)
    List<WorkmanagerPool> getLongRunning();
    void setLongRunning(List<WorkmanagerPool> pools);
}
