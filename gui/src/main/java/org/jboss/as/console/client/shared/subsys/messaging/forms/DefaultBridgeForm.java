package org.jboss.as.console.client.shared.subsys.messaging.forms;

import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.messaging.model.Bridge;
import org.jboss.as.console.client.shared.viewframework.builder.FormLayout;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
import org.jboss.ballroom.client.widgets.forms.CheckBoxItem;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.ListItem;
import org.jboss.ballroom.client.widgets.forms.PasswordBoxItem;
import org.jboss.ballroom.client.widgets.forms.SuggestBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextAreaItem;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextItem;
import org.jboss.dmr.client.ModelNode;

import java.util.Collections;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 4/3/12
 */
public class DefaultBridgeForm {


    private Form<Bridge> form = new Form<Bridge>(Bridge.class);
    private FormToolStrip.FormCallback<Bridge> callback;
    private boolean provideTools = true;
    private boolean isCreate = false;
    private MultiWordSuggestOracle oracle;

    public DefaultBridgeForm(FormToolStrip.FormCallback<Bridge> callback) {
        this.callback = callback;
        form.setNumColumns(2);
        oracle = new MultiWordSuggestOracle();
        oracle.setDefaultSuggestionsFromText(Collections.EMPTY_LIST);
    }

    public DefaultBridgeForm(FormToolStrip.FormCallback<Bridge> callback, boolean provideTools) {
        this.callback = callback;
        this.provideTools = provideTools;
        form.setNumColumns(2);
        oracle = new MultiWordSuggestOracle();
        oracle.setDefaultSuggestionsFromText(Collections.EMPTY_LIST);
    }

    public void setIsCreate(boolean b) {
        this.isCreate = b;
    }

    public Widget asWidget() {


        SuggestBoxItem queueName = new SuggestBoxItem("queueName", "Queue Name");
        SuggestBoxItem forward = new SuggestBoxItem("forwardingAddress", "Forward Address");
        TextAreaItem filter = new TextAreaItem("filter", "Filter", false);
        TextAreaItem transformer = new TextAreaItem("transformerClass", "Transformer Class", false);

        queueName.setOracle(oracle);
        forward.setOracle(oracle);

        CheckBoxItem failoverInitial = new CheckBoxItem("failoverInitial", "Failover Initial?");
        CheckBoxItem failoverShutdown = new CheckBoxItem("failoverShutdown", "Failover Shutdown?");

        CheckBoxItem started = new CheckBoxItem("started", "Started?");

        TextBoxItem discoveryGroup = new TextBoxItem("discoveryGroup", "Discovery Group", false);
        ListItem connectors = new ListItem("staticConnectors", "Static Connectors", false);

        if(isCreate) {

            TextBoxItem name = new TextBoxItem("name", "Name");

            form.setFields(
                    name, queueName,
                    forward, discoveryGroup,
                    connectors);

            form.setNumColumns(1);
        }
        else
        {
            TextItem name = new TextItem("name", "Name");

            form.setFields(
                    name, started,
                    queueName, forward,
                    discoveryGroup, connectors,
                    filter,transformer,
                    failoverInitial, failoverShutdown
            );
        }

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

     public void setQueueNames(List<String> queueNames) {
        oracle.addAll(queueNames);
    }
}
