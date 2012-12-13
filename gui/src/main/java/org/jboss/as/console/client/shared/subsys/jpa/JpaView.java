package org.jboss.as.console.client.shared.subsys.jpa;

import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.DisposableViewImpl;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.jpa.model.JpaSubsystem;
import org.jboss.as.console.client.shared.viewframework.builder.FormLayout;
import org.jboss.as.console.client.shared.viewframework.builder.OneToOneLayout;
import org.jboss.as.console.client.widgets.forms.BlankItem;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
import org.jboss.ballroom.client.widgets.forms.CheckBoxItem;
import org.jboss.ballroom.client.widgets.forms.ComboBoxItem;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.dmr.client.ModelNode;

import java.util.Map;

/**
 * @author Heiko Braun
 * @date 11/28/11
 */
public class JpaView extends DisposableViewImpl implements JpaPresenter.MyView {

    private JpaPresenter presenter;
    private Form<JpaSubsystem> form;

    @Override
    public Widget createWidget() {

        form = new Form<JpaSubsystem>(JpaSubsystem.class);
        form.setNumColumns(2);

        TextBoxItem defaultDs = new TextBoxItem("defaultDataSource", "Default Datasource", false);
        ComboBoxItem inheritance = new ComboBoxItem("inheritance", "Persistence Inheritance")
        {
            @Override
            public boolean isRequired() {
                return false;
            }
        };

        inheritance.setValueMap(new String[] {"DEEP", "SHALLOW"});

        //CheckBoxItem vfs = new CheckBoxItem("defaultVfs", "Enable VFS?");

        form.setFields(defaultDs, BlankItem.INSTANCE, inheritance);
        form.setEnabled(false);

        FormToolStrip<JpaSubsystem> formToolStrip = new FormToolStrip<JpaSubsystem>(
                form, new FormToolStrip.FormCallback<JpaSubsystem>() {
            @Override
            public void onSave(Map<String, Object> changeset) {
                presenter.onSave(form.getEditedEntity(), changeset);
            }

            @Override
            public void onDelete(JpaSubsystem entity) {
                // cannot be removed
            }
        });
        formToolStrip.providesDeleteOp(false);

        FormHelpPanel helpPanel = new FormHelpPanel(new FormHelpPanel.AddressCallback() {
            @Override
            public ModelNode getAddress() {
                ModelNode address = Baseadress.get();
                address.add("subsystem", "jpa");
                return address;
            }
        }, form);

        Widget detail = new FormLayout()
                .setForm(form)
                .setHelp(helpPanel).build();

        Widget panel = new OneToOneLayout()
                .setTitle("JPA")
                .setHeadline("JPA Subsystem")
                .setDescription(Console.CONSTANTS.subsys_jpa_desc())
                .setMaster("Details", detail)
                .setMasterTools(formToolStrip.asWidget()).build();



        return panel;
    }

    @Override
    public void setPresenter(JpaPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void updateFrom(JpaSubsystem jpaSubsystem) {
        form.edit(jpaSubsystem);
    }
}
