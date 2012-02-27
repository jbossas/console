package org.jboss.as.console.client.shared.subsys.jmx;

import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.DisposableViewImpl;
import org.jboss.as.console.client.shared.general.DelegatingOracle;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.jmx.model.JMXSubsystem;
import org.jboss.as.console.client.shared.viewframework.builder.FormLayout;
import org.jboss.as.console.client.shared.viewframework.builder.OneToOneLayout;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
import org.jboss.ballroom.client.widgets.forms.CheckBoxItem;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.SuggestBoxItem;
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

        SuggestBoxItem server = new SuggestBoxItem("serverBinding", "Server Binding");
        SuggestBoxItem registry = new SuggestBoxItem("registryBinding", "Registry Binding");

        SuggestOracle oracle = new DelegatingOracle(presenter);
        server.setOracle(oracle);
        registry.setOracle(oracle);

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

        Widget panel = new OneToOneLayout()
                .setTitle("JMX")
                .setHeadline("JMX Subsystem")
                .setDescription(Console.CONSTANTS.subsys_jmx_desc())
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
