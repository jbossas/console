package org.jboss.as.console.client.shared.subsys.jca;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.jca.model.JcaArchiveValidation;
import org.jboss.as.console.client.shared.subsys.jca.model.JcaConnectionManager;
import org.jboss.as.console.client.shared.viewframework.builder.OneToOneLayout;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
import org.jboss.ballroom.client.widgets.forms.CheckBoxItem;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.dmr.client.ModelNode;

import java.util.Map;

/**
 * @author Heiko Braun
 * @date 11/29/11
 */
public class JcaBaseEditor {

    private Form<JcaArchiveValidation> archiveForm;
    private Form<JcaBeanValidation> validationForm;
    private Form<JcaConnectionManager> connectionManagerForm;

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

        final FormHelpPanel archiveHelpPanel = new FormHelpPanel(
                       new FormHelpPanel.AddressCallback() {
                           @Override
                           public ModelNode getAddress() {
                               ModelNode address = Baseadress.get();
                               address.add("subsystem", "jca");
                               address.add("archive-validation", "archive-validation");
                               return address;
                           }
                       }, archiveForm
               );


        VerticalPanel archivePanel = new VerticalPanel();
        archivePanel.add(archiveTools.asWidget());
        archivePanel.add(archiveHelpPanel.asWidget());
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

        final FormHelpPanel validationHelpPanel = new FormHelpPanel(
                new FormHelpPanel.AddressCallback() {
                    @Override
                    public ModelNode getAddress() {
                        ModelNode address = Baseadress.get();
                        address.add("subsystem", "jca");
                        address.add("bean-validation", "bean-validation");
                        return address;
                    }
                }, validationForm
        );

        VerticalPanel validationPanel = new VerticalPanel();
        validationPanel.add(validationTools.asWidget());
        validationPanel.add(validationHelpPanel.asWidget());
        validationPanel.add(validationForm.asWidget());

        // ----
        // ----


        connectionManagerForm = new Form<JcaConnectionManager>(JcaConnectionManager.class);
        connectionManagerForm.setNumColumns(2);
        connectionManagerForm.setEnabled(false);

        CheckBoxItem errorEnabled = new CheckBoxItem("error", "Error Log Enabled?");
        CheckBoxItem debugEnabled = new CheckBoxItem("debug", "Debug Log Enabled?");

        connectionManagerForm.setFields(errorEnabled, debugEnabled);

        FormToolStrip<JcaConnectionManager> ccmTools = new FormToolStrip<JcaConnectionManager>(
                connectionManagerForm,
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

        final FormHelpPanel helpPanel = new FormHelpPanel(
                new FormHelpPanel.AddressCallback() {
                    @Override
                    public ModelNode getAddress() {
                        ModelNode address = Baseadress.get();
                        address.add("subsystem", "jca");
                        address.add("cached-connection-manager", "cached-connection-manager");
                        return address;
                    }
                }, connectionManagerForm
        );

        VerticalPanel ccmPanel = new VerticalPanel();
        ccmPanel.add(ccmTools.asWidget());
        ccmPanel.add(helpPanel.asWidget());
        ccmPanel.add(connectionManagerForm.asWidget());


        Widget panel = new OneToOneLayout()
                .setPlain(true)
                .setTitle("JCA")
                .setHeadline("JCA Subsystem")
                .setDescription(Console.CONSTANTS.subsys_jca_common_config_desc())
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
        connectionManagerForm.edit(jcaConnectionManager);
    }
}
