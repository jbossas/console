package org.jboss.as.console.client.shared.subsys.jca.model;


import org.jboss.as.console.client.widgets.forms.Address;
import org.jboss.as.console.client.widgets.forms.Binding;

/**
 * @author Heiko Braun
 * @date 11/29/11
 */
@Address("/subsystem=jca/archive-validation=archive-validation")
public interface JcaArchiveValidation {

    @Binding(detypedName = "enabled")
    boolean isEnabled();
    void setEnabled(boolean b);

    @Binding(detypedName = "fail-on-error")
    boolean isFailOnError();
    void setFailOnError(boolean b);

    @Binding(detypedName = "fail-on-warn")
    boolean isFailOnWarn();
    void setFailOnWarn(boolean b);

}
