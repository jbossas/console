package org.jboss.as.console.client.shared.subsys.jca;

import org.jboss.as.console.client.widgets.forms.Address;
import org.jboss.as.console.client.widgets.forms.Binding;

/**
 * @author Heiko Braun
 * @date 11/29/11
 */
@Address("/subsystem=jca/bean-validation=bean-validation")
public interface JcaBeanValidation {

    @Binding(detypedName = "enabled")
    boolean isEnabled();
    void setEnabled(boolean b);
}
