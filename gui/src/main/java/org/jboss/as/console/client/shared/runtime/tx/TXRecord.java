package org.jboss.as.console.client.shared.runtime.tx;

import org.jboss.as.console.client.widgets.forms.Binding;

/**
 * @author Heiko Braun
 * @date 2/27/13
 */
public interface TXRecord {

    String getId();
    void setId(String id);

    @Binding(detypedName = "age-in-seconds")
    String getAge();
    void setAge(String age);

    String getType();
    void setType(String type);

}
