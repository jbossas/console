package org.jboss.as.console.client.shared.subsys.modcluster;

import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.modcluster.model.Modcluster;
import org.jboss.as.console.client.shared.viewframework.builder.FormLayout;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.FormItem;
import org.jboss.dmr.client.ModelNode;

import java.util.Map;

/**
 * @author Heiko Braun
 * @date 2/27/12
 */
public class ModclusterForm {



    private ModclusterManagement presenter;
    private Form<Modcluster> form;

    public ModclusterForm(ModclusterManagement presenter) {

        this.presenter = presenter;

        form = new Form<Modcluster>(Modcluster.class);
        form.setNumColumns(2);
    }

    public void setFields(FormItem... items) {
        form.setFields(items);
    }
    public Widget asWidget() {


        FormHelpPanel helpPanel = new FormHelpPanel(new FormHelpPanel.AddressCallback() {
            @Override
            public ModelNode getAddress() {
                ModelNode address = Baseadress.get();
                address.add("subsystem", "modcluster");
                address.add("mod-cluster-config", "configuration");
                return address;
            }
        }, form);

        FormToolStrip<Modcluster> formToolStrip = new FormToolStrip<Modcluster>(
                form, new FormToolStrip.FormCallback<Modcluster>() {
            @Override
            public void onSave(Map<String, Object> changeset) {
                presenter.onSave(form.getEditedEntity(), changeset);
            }

            @Override
            public void onDelete(Modcluster entity) {

            }
        });

        formToolStrip.providesDeleteOp(false);

        Widget formPanel = new FormLayout()
                .setForm(form)
                .setSetTools(formToolStrip)
                .setHelp(helpPanel).build();


        form.setEnabled(false);

        return formPanel;
    }

    public void updateFrom(Modcluster entity)
    {
        form.edit(entity);
    }
}
