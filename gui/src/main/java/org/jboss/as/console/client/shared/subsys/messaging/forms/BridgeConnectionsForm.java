package org.jboss.as.console.client.shared.subsys.messaging.forms;

import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.messaging.model.Bridge;
import org.jboss.as.console.client.shared.viewframework.builder.FormLayout;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.NumberBoxItem;
import org.jboss.ballroom.client.widgets.forms.PasswordBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.dmr.client.ModelNode;

/**
 * @author Heiko Braun
 * @date 4/3/12
 */
public class BridgeConnectionsForm {


    private Form<Bridge> form = new Form<Bridge>(Bridge.class);
    private FormToolStrip.FormCallback<Bridge> callback;
    private boolean provideTools = true;

    public BridgeConnectionsForm(FormToolStrip.FormCallback<Bridge> callback) {
        this.callback = callback;
        form.setNumColumns(2);
    }

    public Widget asWidget() {


        NumberBoxItem retry = new NumberBoxItem("retryInterval", "Retry Interval");
        NumberBoxItem reconnect = new NumberBoxItem("reconnectAttempts", "Reconnect Attempts");

        TextBoxItem user = new TextBoxItem("user", "User", false);
        PasswordBoxItem pass = new PasswordBoxItem("password", "Password", false);

        form.setFields(
                user, pass,
                retry, reconnect
        );

        FormHelpPanel helpPanel = new FormHelpPanel(
                new FormHelpPanel.AddressCallback() {
                    @Override
                    public ModelNode getAddress() {
                        ModelNode address = Baseadress.get();
                        address.add("subsystem", "messaging");
                        address.add("hornetq-server", "*");
                        address.add("bridge", "*");
                        return address;
                    }
                }, form);

        FormToolStrip<Bridge> formTools = new FormToolStrip<Bridge>(form, callback);

        FormLayout formLayout = new FormLayout()
                .setForm(form)
                .setHelp(helpPanel);

        if(provideTools)
            formLayout.setSetTools(formTools);

        return formLayout.build();
    }

    public Form<Bridge> getForm() {
        return form;
    }
}
