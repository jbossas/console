package org.jboss.as.console.client.shared.subsys.jca;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.subsys.jca.model.JcaArchiveValidation;
import org.jboss.as.console.client.shared.subsys.jca.model.JcaConnectionManager;
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

    private Form<JcaArchiveValidation> archiveForm;
    private Form<JcaBeanValidation> validationForm;
    private Form<JcaConnectionManager> ccmForm;

    private JcaPresenter presenter;

    public JcaBaseEditor(JcaPresenter presenter) {
        this.presenter = presenter;
    }

    Widget asWidget() {

        archiveForm = new Form<JcaArchiveValidation>(JcaArchiveValidation.class);
        archiveForm.setNumColumns(2);
        archiveForm.setEnabled(false);

        CheckBoxItem enabled = new CheckBoxItem("enabled", "Is Enabled?");
        CheckBoxItem failWarn = new CheckBoxItem("failOnWarn", "Fail on Warn?");
        CheckBoxItem failError = new CheckBoxItem("failOnError", "Fail On Error?");

        archiveForm.setFields(enabled, failWarn, failError);

        FormToolStrip<JcaArchiveValidation> archiveTools = new FormToolStrip<JcaArchiveValidation>(
                archiveForm,
                new FormToolStrip.FormCallback<JcaArchiveValidation>() {
                    @Override
                    public void onSave(Map<String, Object> changeset) {
                        presenter.onSaveArchiveSettings(changeset);
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
        validationForm.setEnabled(false);

        CheckBoxItem validationEnabled = new CheckBoxItem("enabled", "Is Enabled?");

        validationForm.setFields(validationEnabled);

        FormToolStrip<JcaBeanValidation> validationTools = new FormToolStrip<JcaBeanValidation>(
                validationForm,
                new FormToolStrip.FormCallback<JcaBeanValidation>() {
                    @Override
                    public void onSave(Map<String, Object> changeset) {
                        presenter.onSaveBeanSettings(changeset);
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
        // ----


        ccmForm = new Form<JcaConnectionManager>(JcaConnectionManager.class);
        ccmForm.setNumColumns(2);
        ccmForm.setEnabled(false);

        CheckBoxItem errorEnabled = new CheckBoxItem("error", "Error Log Enabled?");
        CheckBoxItem debugEnabled = new CheckBoxItem("debug", "Debug Log Enabled?");

        ccmForm.setFields(errorEnabled, debugEnabled);

        FormToolStrip<JcaConnectionManager> ccmTools = new FormToolStrip<JcaConnectionManager>(
                ccmForm,
                new FormToolStrip.FormCallback<JcaConnectionManager>() {
                    @Override
                    public void onSave(Map<String, Object> changeset) {
                        presenter.onSaveCCMSettings(changeset);
                    }

                    @Override
                    public void onDelete(JcaConnectionManager entity) {

                    }
                }
        );
        ccmTools.providesDeleteOp(false);

        VerticalPanel ccmPanel = new VerticalPanel();
        ccmPanel.add(ccmTools.asWidget());
        ccmPanel.add(ccmForm.asWidget());


        Widget panel = new OneToOneLayout()
                .setPlain(true)
                .setTitle("JCA")
                .setHeadline("JCA Common Config")
                .setDescription("The Java EE Connector Architecture (JCA) subsystem providing general configuration for resource adapters.")
                .setMaster("", new HTML())
                .addDetail("Connection Manager", ccmPanel)
                .addDetail("Archive Validation", archivePanel)
                .addDetail("Bean Validaton", validationPanel)
                .build();

        return panel;

    }

    public void setBeanSettings(JcaBeanValidation jcaBeanValidation) {
        validationForm.edit(jcaBeanValidation);
    }

    public void setArchiveSettings(JcaArchiveValidation jcaArchiveValidation) {
        archiveForm.edit(jcaArchiveValidation);
    }

    public void setCCMSettings(JcaConnectionManager jcaConnectionManager) {
        ccmForm.edit(jcaConnectionManager);
    }
}
