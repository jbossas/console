package org.jboss.as.console.client.shared.subsys.jca;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.shared.subsys.jca.model.JcaArchiveValidation;
import org.jboss.as.console.client.shared.viewframework.builder.OneToOneLayout;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
import org.jboss.ballroom.client.widgets.forms.CheckBoxItem;
import org.jboss.ballroom.client.widgets.forms.Form;

import java.util.Map;

/**
 * @author Heiko Braun
 * @date 11/29/11
 */
public class JcaSubsystemView extends SuspendableViewImpl implements JcaPresenter.MyView {

    private JcaPresenter presenter;
    Form<JcaArchiveValidation> archiveForm;
    Form<JcaBeanValidation> validationForm;

    @Override
    public void setPresenter(JcaPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget createWidget() {

        // ----

        archiveForm = new Form<JcaArchiveValidation>(JcaArchiveValidation.class);
        archiveForm.setNumColumns(2);

        CheckBoxItem enabled = new CheckBoxItem("enabled", "Is Enabled?");
        CheckBoxItem failWarn = new CheckBoxItem("failOnWarn", "Fail on Warn?");
        CheckBoxItem failError = new CheckBoxItem("failOnError", "Fail On Error?");

        archiveForm.setFields(enabled, failWarn, failError);

        FormToolStrip<JcaArchiveValidation> archiveTools = new FormToolStrip<JcaArchiveValidation>(
                archiveForm,
                new FormToolStrip.FormCallback<JcaArchiveValidation>() {
                    @Override
                    public void onSave(Map<String, Object> changeset) {

                    }

                    @Override
                    public void onDelete(JcaArchiveValidation entity) {

                    }
                }
        );
        archiveTools.providesDeleteOp(false);


        // ----


        validationForm = new Form<JcaBeanValidation>(JcaBeanValidation.class);
        validationForm.setNumColumns(2);

        CheckBoxItem validationEnabled = new CheckBoxItem("enabled", "Is Enabled?");

        validationForm.setFields(validationEnabled);

        FormToolStrip<JcaBeanValidation> validationTools = new FormToolStrip<JcaBeanValidation>(
                validationForm,
                new FormToolStrip.FormCallback<JcaBeanValidation>() {
                    @Override
                    public void onSave(Map<String, Object> changeset) {

                    }

                    @Override
                    public void onDelete(JcaBeanValidation entity) {

                    }
                }
        );
        archiveTools.providesDeleteOp(false);

        VerticalPanel validationPanel = new VerticalPanel();
        validationPanel.add(validationTools.asWidget());
        validationPanel.add(validationForm.asWidget());

        Widget panel = new OneToOneLayout()
                .setTitle("JCA")
                .setHeadline("JCA Subsystem")
                .setDescription("The Java EE Connector Architecture (JCA) subsystem providing general configuration for resource adapters.")
                .setMaster("Archive Validation", archiveForm.asWidget())
                .setMasterTools(archiveTools.asWidget())
                .addDetail("Bean Validaton", validationPanel)
                .addDetail("BoostrapContext", new HTML())
                .addDetail("Workmanager", new HTML())
                .build();


        return panel;
    }
}
