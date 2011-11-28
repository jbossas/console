package org.jboss.as.console.client.shared.subsys.jmx;

import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.core.DisposableViewImpl;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.jmx.model.JMXSubsystem;
import org.jboss.as.console.client.shared.subsys.jpa.model.JpaSubsystem;
import org.jboss.as.console.client.shared.viewframework.builder.FormLayout;
import org.jboss.as.console.client.shared.viewframework.builder.OneToOneLayout;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
import org.jboss.ballroom.client.widgets.forms.CheckBoxItem;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.dmr.client.ModelNode;

import java.util.Map;

/**
 * @author Heiko Braun
 * @date 11/28/11
 */
public class JMXSubsystemView extends DisposableViewImpl implements JMXPresenter.MyView {

    private JMXPresenter presenter;
    private Form<JMXSubsystem> form;

    @Override
    public Widget createWidget() {

        form = new Form<JMXSubsystem>(JMXSubsystem.class);

        TextBoxItem server = new TextBoxItem("serverBinding", "Server Binding");
        TextBoxItem registry = new TextBoxItem("registryBinding", "Registry Binding");
        CheckBoxItem showModel = new CheckBoxItem("showModel", "Show Model?");

        form.setFields(server, registry, showModel);
        form.setNumColumns(2);
        form.setEnabled(false);

        FormToolStrip<JMXSubsystem> formToolStrip = new FormToolStrip<JMXSubsystem>(
                form, new FormToolStrip.FormCallback<JMXSubsystem>() {
            @Override
            public void onSave(Map<String, Object> changeset) {
                presenter.onSave(form.getEditedEntity(), changeset);
            }

            @Override
            public void onDelete(JMXSubsystem entity) {
                // cannot be removed
            }
        });
        formToolStrip.providesDeleteOp(false);

        FormHelpPanel helpPanel = new FormHelpPanel(new FormHelpPanel.AddressCallback() {
            @Override
            public ModelNode getAddress() {
                ModelNode address = Baseadress.get();
                address.add("subsystem", "jmx");
                return address;
            }
        }, form);

        Widget detail = new FormLayout()
                .setForm(form)
                .setHelp(helpPanel).build();

        Widget panel = new OneToOneLayout<JpaSubsystem>()
                .setTitle("JMX")
                .setHeadline("JMX Subsystem")
                .setDescription("The configuration of the JMX subsystem.")
                .setMaster("Details", detail)
                .setMasterTools(formToolStrip.asWidget()).build();



        return panel;
    }

    @Override
    public void setPresenter(JMXPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void updateFrom(JMXSubsystem jmxSubsystem) {
        form.edit(jmxSubsystem);
    }
}
