package org.jboss.as.console.client.shared.subsys.messaging.forms;

import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.messaging.model.ClusterConnection;
import org.jboss.as.console.client.shared.viewframework.builder.FormLayout;
import org.jboss.as.console.client.widgets.forms.BlankItem;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
import org.jboss.ballroom.client.widgets.forms.CheckBoxItem;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.FormItem;
import org.jboss.ballroom.client.widgets.forms.NumberBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextItem;
import org.jboss.dmr.client.ModelNode;

import java.util.Collections;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 4/3/12
 */
public class ClusterConnectionForm {

    Form<ClusterConnection> form = new Form<ClusterConnection>(ClusterConnection.class);

    boolean isCreate = false;
    private FormToolStrip.FormCallback<ClusterConnection> callback;

    private MultiWordSuggestOracle oracle;


    public ClusterConnectionForm(FormToolStrip.FormCallback<ClusterConnection> callback) {
        this.callback = callback;
        oracle = new MultiWordSuggestOracle();
        oracle.setDefaultSuggestionsFromText(Collections.EMPTY_LIST);
    }

    public ClusterConnectionForm(FormToolStrip.FormCallback<ClusterConnection> callback, boolean create) {
        this.callback = callback;
        isCreate = create;
        oracle = new MultiWordSuggestOracle();
        oracle.setDefaultSuggestionsFromText(Collections.EMPTY_LIST);

    }

    public Widget asWidget() {

        buildForm();

        if(isCreate)
        {
            form.setNumColumns(1);
        }
        else {

            form.setNumColumns(2);
            form.setEnabled(false);
        }

        FormHelpPanel helpPanel = new FormHelpPanel(
                new FormHelpPanel.AddressCallback() {
                    @Override
                    public ModelNode getAddress() {

                        ModelNode address = Baseadress.get();
                        address.add("subsystem", "messaging");
                        address.add("hornetq-server", "*");
                        address.add("cluster-connection", "*");
                        return address;
                    }
                }, form);

        FormToolStrip<ClusterConnection> formTools = new FormToolStrip<ClusterConnection>(form, callback);

        FormLayout formLayout = new FormLayout()
                .setForm(form)
                .setHelp(helpPanel);

        if(!isCreate)
            formLayout.setSetTools(formTools);

        return formLayout.build();
    }

    private void buildForm() {
        FormItem name = null;

        if(isCreate)
            name = new TextBoxItem("name", "Name");
        else
            name = new TextItem("name", "Name");


        NumberBoxItem callTimeout = new NumberBoxItem("callTimeout", "Call Timeout");
        NumberBoxItem checkPeriod = new NumberBoxItem("checkPeriod", "Check Period");


        TextBoxItem connectionAddress= new TextBoxItem("clusterConnectionAddress", "Connection Address");
        NumberBoxItem connectionTtl= new NumberBoxItem("connectionTTL", "Connection TTL");

        TextBoxItem connectorRef= new TextBoxItem("connectorRef", "Connector Ref");
        TextBoxItem groupName= new TextBoxItem("discoveryGroupName", "Discovery Group");
        CheckBoxItem forward = new CheckBoxItem("forwardWhenNoConsumers","Forward?");

        NumberBoxItem maxHops = new NumberBoxItem("maxHops", "Max Hops");
        NumberBoxItem retryInterval = new NumberBoxItem("retryInterval", "Retry Interval");
        NumberBoxItem maxRetryInterval = new NumberBoxItem("maxRetryInterval", "Max Retry");
        NumberBoxItem reconnect = new NumberBoxItem("reconnectAttempts", "Reconnect Attempts");

        CheckBoxItem duplicateDetection = new CheckBoxItem("duplicateDetection","Duplicate Detection?");
        CheckBoxItem allowDirect = new CheckBoxItem("allowDirect","Direct Connections?");

        if(isCreate)
            form.setFields(name, groupName, connectorRef, connectionAddress);
        else
            form.setFields(name, groupName, connectorRef, connectionAddress,
                    duplicateDetection, allowDirect,
                    forward, BlankItem.INSTANCE,
                    callTimeout, checkPeriod,
                    connectionTtl, maxHops,
                    retryInterval, maxRetryInterval,
                    reconnect);
    }

    public Form<ClusterConnection> getForm() {
        return form;
    }

    public void setIsCreate(boolean create) {
        isCreate = create;
    }

    public void setSocketBindings(List<String> socketBindings) {
        this.oracle.clear();
        this.oracle.addAll(socketBindings);
    }
}
