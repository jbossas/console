package org.jboss.as.console.client.shared.subsys.ejb3;

import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.core.DisposableViewImpl;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.ejb3.model.EESubsystem;
import org.jboss.as.console.client.shared.viewframework.builder.FormLayout;
import org.jboss.as.console.client.shared.viewframework.builder.OneToOneLayout;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
import org.jboss.ballroom.client.widgets.forms.CheckBoxItem;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.dmr.client.ModelNode;

import java.util.Map;

/**
 * @author Heiko Braun
 * @date 11/28/11
 */
public class EESubsystemView extends DisposableViewImpl implements EEPresenter.MyView {

    private EEPresenter presenter;
    private Form<EESubsystem> form;

    @Override
    public Widget createWidget() {
        form = new Form<EESubsystem>(EESubsystem.class);

        CheckBoxItem isolation = new CheckBoxItem("isolatedSubdeployments", "Isolated Subdeployments?");

        form.setFields(isolation);
        form.setEnabled(false);

        FormToolStrip<EESubsystem> formToolStrip = new FormToolStrip<EESubsystem>(
                form, new FormToolStrip.FormCallback<EESubsystem>() {
            @Override
            public void onSave(Map<String, Object> changeset) {
                presenter.onSave(form.getEditedEntity(), changeset);
            }

            @Override
            public void onDelete(EESubsystem entity) {
                // cannot be removed
            }
        });
        formToolStrip.providesDeleteOp(false);

        FormHelpPanel helpPanel = new FormHelpPanel(new FormHelpPanel.AddressCallback() {
            @Override
            public ModelNode getAddress() {
                ModelNode address = Baseadress.get();
                address.add("subsystem", "ee");
                return address;
            }
        }, form);

        Widget master = new FormLayout()
                .setForm(form)
                .setHelp(helpPanel).build();


        // -----
        // module list

        //DefaultCellTable


        Widget panel = new OneToOneLayout<EESubsystem>()
                .setTitle("EE")
                .setHeadline("EE Subsystem")
                .setDescription("The configuration of the EE subsystem.")
                .setMaster("Details", master)
                .setMasterTools(formToolStrip.asWidget()).build();



        return panel;
    }

    @Override
    public void setPresenter(EEPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void updateFrom(EESubsystem eeSubsystem) {
        form.edit(eeSubsystem);
    }
}
