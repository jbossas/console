package org.jboss.as.console.client.shared.subsys.messaging.forms;

import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.messaging.model.ConnectionFactory;
import org.jboss.as.console.client.shared.viewframework.builder.FormLayout;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
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


    private Form<ConnectionFactory> form;
    private FormToolStrip.FormCallback<ConnectionFactory> callback;

    public DefaultCFForm(FormToolStrip.FormCallback<ConnectionFactory> callback) {
        this.callback = callback;
    }

    public Widget asWidget() {
        form = new Form<ConnectionFactory>(ConnectionFactory.class);
        form.setNumColumns(2);

        TextItem name = new TextItem("name", "Name");
        TextItem jndiName = new TextItem("jndiName", "JNDI Name");

        TextBoxItem groupId = new TextBoxItem("groupId", "Group ID");
        TextItem connector = new TextItem("connector", "Connector");

        NumberBoxItem callTimeout = new NumberBoxItem("callTimeout", "Call Timeout");
        NumberBoxItem connectionTTL = new NumberBoxItem("connectionTTL", "Connection TTL");

        CheckBoxItem failoverInitial = new CheckBoxItem("failoverInitial", "Failover Initial");
        CheckBoxItem failoverShutdown = new CheckBoxItem("failoverShutdown", "Failover Initial");

        NumberBoxItem maxRetryInterval = new NumberBoxItem("maxRetryInterval", "Max Retry");
        NumberBoxItem retryInterval = new NumberBoxItem("retryInterval", "Retry Interval");

        form.setFields(
                name, jndiName,
                connector, groupId,
                callTimeout, connectionTTL,
                retryInterval, maxRetryInterval,
                failoverInitial, failoverShutdown);


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
                .setSetTools(formTools)
                .setForm(form)
                .setHelp(helpPanel);

        return formLayout.build();
    }

    public Form<ConnectionFactory> getForm() {
        return form;
    }
}
