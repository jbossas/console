package org.jboss.as.console.client.shared.subsys.jgroups;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.shared.properties.PropertyEditor;
import org.jboss.as.console.client.shared.properties.PropertyManagement;
import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.viewframework.builder.FormLayout;
import org.jboss.as.console.client.shared.viewframework.builder.OneToOneLayout;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
import org.jboss.ballroom.client.widgets.forms.CheckBoxItem;
import org.jboss.ballroom.client.widgets.forms.DisclosureGroupRenderer;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextItem;
import org.jboss.dmr.client.ModelNode;

import java.util.Map;

/**
 * @author Heiko Braun
 * @date 2/16/12
 */
public class TransportEditor implements PropertyManagement {

    private HTML headline;
    private JGroupsPresenter presenter;
    private PropertyEditor properyEditor;
    private Form<JGroupsTransport> form;

    public TransportEditor(JGroupsPresenter presenter) {
        this.presenter = presenter;
    }

    Widget asWidget() {

        form = new Form<JGroupsTransport>(JGroupsTransport.class);
        form.setNumColumns(2);

        TextItem type = new TextItem("type", "Type");
        TextBoxItem socket= new TextBoxItem("socketBinding", "Socket Binding");
        TextBoxItem diagSocket = new TextBoxItem("diagSocketBinding", "Diagnostics Socket");
        CheckBoxItem shared= new CheckBoxItem("shared", "Is Shared?");
        TextBoxItem oobExecutor = new TextBoxItem("oobExecutor", "OOB Executor");
        TextBoxItem timerExecutor = new TextBoxItem("timerExecutor", "timer Executor");
        TextBoxItem defaultExecutor = new TextBoxItem("defaultExecutor", "Default Executor");
        TextBoxItem threadFactory= new TextBoxItem("threadFactory", "Thread Factory");
        TextBoxItem machine = new TextBoxItem("machine", "Machine", false);
        TextBoxItem site= new TextBoxItem("site", "Site", false);
        TextBoxItem rack= new TextBoxItem("rack", "Rack", false);

        /*
    @Binding(skip = true)
    List<PropertyRecord> getProperties();
    void setProperties(List<PropertyRecord> properties);

         */

        form.setFields(type, socket, diagSocket, machine, shared, site, rack);
        form.setFieldsInGroup("Executors", new DisclosureGroupRenderer(), threadFactory, defaultExecutor, oobExecutor, timerExecutor);

        form.setEnabled(false);


        FormHelpPanel helpPanel = new FormHelpPanel(new FormHelpPanel.AddressCallback() {
            @Override
            public ModelNode getAddress() {
                ModelNode address = Baseadress.get();
                address.add("subsystem", "jgroups");
                address.add("stack", "*");
                address.add("transport", "TRANSPORT");
                return address;
            }
        }, form);

        FormToolStrip<JGroupsTransport> formToolStrip = new FormToolStrip<JGroupsTransport>(
                form, new FormToolStrip.FormCallback<JGroupsTransport>() {
            @Override
            public void onSave(Map<String, Object> changeset) {
                presenter.onSaveTransport(form.getEditedEntity(), changeset);
            }

            @Override
            public void onDelete(JGroupsTransport entity) {

            }
        });
        formToolStrip.providesDeleteOp(false);

        Widget detail = new FormLayout()
                .setForm(form)
                .setHelp(helpPanel)
                .setSetTools(formToolStrip).build();

        headline = new HTML();
        headline.setStyleName("content-header-label");

        properyEditor = new PropertyEditor(this, true);

        Widget panel = new OneToOneLayout()
                .setPlain(true)
                .setTitle("JGroups")
                .setHeadlineWidget(headline)
                .setDescription(Console.CONSTANTS.subsys_jgroups_transport_desc())
                .setMaster("Transport Attributes", detail)
                .addDetail("Properties", properyEditor.asWidget())
                .build();


        properyEditor.setAllowEditProps(false);

        return panel;
    }

    public void setStack(JGroupsStack stack) {
        headline.setText("Transport: Stack "+stack.getName());

        form.edit(stack.getTransport());

    }

    @Override
    public void onCreateProperty(String reference, PropertyRecord prop) {
        
    }

    @Override
    public void onDeleteProperty(String reference, PropertyRecord prop) {
        
    }

    @Override
    public void onChangeProperty(String reference, PropertyRecord prop) {
        
    }

    @Override
    public void launchNewPropertyDialoge(String reference) {
        
    }

    @Override
    public void closePropertyDialoge() {
        
    }
}
