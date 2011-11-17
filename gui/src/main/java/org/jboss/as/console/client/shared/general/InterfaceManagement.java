package org.jboss.as.console.client.shared.general;

import org.jboss.as.console.client.shared.general.model.Interface;
import org.jboss.as.console.client.shared.general.validation.ValidationResult;
import org.jboss.dmr.client.ModelNode;

import java.util.Map;

/**
 * @author Heiko Braun
 * @date 11/17/11
 */
public interface InterfaceManagement {

    public interface Callback {
        void loadInterfaces();
        ModelNode getBaseAddress();
    }

    void launchNewInterfaceDialogue();

    void createNewInterface(Interface entity);

    void onRemoveInterface(Interface entity);

    ValidationResult validateInterfaceConstraints(Interface entity, Map<String, Object> changeset);

    void onSaveInterface(Interface entity, Map<String, Object> changeset);

    void closeDialoge();

    void setCallback(Callback callback);
}
