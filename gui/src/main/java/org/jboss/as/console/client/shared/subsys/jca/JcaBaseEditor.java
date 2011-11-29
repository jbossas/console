package org.jboss.as.console.client.shared.subsys.jca;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
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
public class JcaBaseEditor {

    Form<JcaArchiveValidation> archiveForm;
    Form<JcaBeanValidation> validationForm;

    Widget asWidget() {

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

        VerticalPanel archivePanel = new VerticalPanel();
        archivePanel.add(archiveTools.asWidget());
        archivePanel.add(archiveForm.asWidget());

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
        validationTools.providesDeleteOp(false);

        VerticalPanel validationPanel = new VerticalPanel();
        validationPanel.add(validationTools.asWidget());
        validationPanel.add(validationForm.asWidget());

        // ----

        Widget panel = new OneToOneLayout()
                .setPlain(true)
                .setTitle("JCA")
                .setHeadline("JCA Subsystem")
                .setDescription("The Java EE Connector Architecture (JCA) subsystem providing general configuration for resource adapters.")
                .setMaster("", new HTML())
                .addDetail("Archive Validation", archivePanel)
                .addDetail("Bean Validaton", validationPanel)
                .build();

        return panel;

    }
}
