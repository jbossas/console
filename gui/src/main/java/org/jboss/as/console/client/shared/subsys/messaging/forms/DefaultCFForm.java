package org.jboss.as.console.client.shared.subsys.messaging.forms;

import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.messaging.model.ConnectionFactory;
import org.jboss.as.console.client.shared.viewframework.builder.FormLayout;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
import org.jboss.as.console.client.widgets.forms.items.JndiNameItem;
import org.jboss.ballroom.client.widgets.forms.CheckBoxItem;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.NumberBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextItem;
import org.jboss.dmr.client.ModelNode;

/**
 * @author Heiko Braun
 * @date 4/3/12
 */
public class DefaultCFForm {


    private Form<ConnectionFactory> form = new Form<ConnectionFactory>(ConnectionFactory.class);
    private FormToolStrip.FormCallback<ConnectionFactory> callback;
    private boolean provideTools = true;
    private boolean isCreate = false;

    public DefaultCFForm(FormToolStrip.FormCallback<ConnectionFactory> callback) {
        this.callback = callback;
        form.setNumColumns(2);
    }

    public DefaultCFForm(FormToolStrip.FormCallback<ConnectionFactory> callback, boolean provideTools) {
        this.callback = callback;
        this.provideTools = provideTools;
        form.setNumColumns(2);
    }

    public void setIsCreate(boolean b) {
        this.isCreate = b;
    }

    public Widget asWidget() {


        JndiNameItem jndiName = new JndiNameItem("jndiName", "JNDI Name");

        TextBoxItem groupId = new TextBoxItem("groupId", "Group ID", false);
        TextBoxItem connector = new TextBoxItem("connector", "Connector");

        CheckBoxItem failoverInitial = new CheckBoxItem("failoverInitial", "Failover Initial?");
        CheckBoxItem failoverShutdown = new CheckBoxItem("failoverShutdown", "Failover Shutdown=");

        CheckBoxItem globalPools = new CheckBoxItem("useGlobalPools", "Global Pools?");

        NumberBoxItem threadPool = new NumberBoxItem("threadPoolMax", "Thread Pool Max");
        NumberBoxItem txBatch = new NumberBoxItem("transactionBatchSize", "Transaction Batch Size");

        if(isCreate) {

            TextBoxItem name = new TextBoxItem("name", "Name");

            form.setFields(
                    name, jndiName,
                    connector);
        }
        else
        {
            TextItem name = new TextItem("name", "Name");

            form.setFields(
                    name, jndiName,
                    connector, groupId,
                    failoverInitial, failoverShutdown,
                    threadPool, txBatch,
                    globalPools);
        }

        FormHelpPanel helpPanel = new FormHelpPanel(
                new FormHelpPanel.AddressCallback() {
                    @Override
                    public ModelNode getAddress() {
                        ModelNode address = Baseadress.get();
                        address.add("subsystem", "messaging");
                        address.add("hornetq-server", "*");
                        address.add("connection-factory", "*");
                        return address;
                    }
                }, form);

        FormToolStrip<ConnectionFactory> formTools = new FormToolStrip<ConnectionFactory>(form, callback);

        FormLayout formLayout = new FormLayout()
                .setForm(form)
                .setHelp(helpPanel);

        if(provideTools)
            formLayout.setSetTools(formTools);

        return formLayout.build();
    }

    public Form<ConnectionFactory> getForm() {
        return form;
    }
}
