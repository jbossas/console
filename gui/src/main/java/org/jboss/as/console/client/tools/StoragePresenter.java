package org.jboss.as.console.client.tools;

/**
 * @author Heiko Braun
 * @date 7/23/12
 */
public interface StoragePresenter {
    void launchNewTemplateWizard();

    void onRemoveTemplate(String id);

    void closeDialogue();

    void onCreateTemplate(FXTemplate template);

    void launchNewModelStepWizard(FXTemplate template);

    void onRemoveModelStep(FXTemplate currentTemplate, String stepId);
}
