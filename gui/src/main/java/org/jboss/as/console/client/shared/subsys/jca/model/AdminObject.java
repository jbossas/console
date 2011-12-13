package org.jboss.as.console.client.shared.subsys.jca.model;

import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.jboss.as.console.client.widgets.forms.Binding;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 12/13/11
 */
public interface AdminObject {

    @Binding(skip=true)
    String getName();
    void setName(String name);

    @Binding(detypedName = "jndi-name")
    String getJndiName();
    void setJndiName(String name);

    @Binding(detypedName = "class-name")
    String getAdminClass();
    void setAdminClass(String classname);

    void setEnabled(boolean enabled);
    boolean isEnabled();

    @Binding(skip = true)
    List<PropertyRecord> getProperties();
    void setProperties(List<PropertyRecord> props);

    @Binding(detypedName = "use-java-context")
    boolean isUseJavaContext();
    void setUseJavaContext(boolean b);
}
