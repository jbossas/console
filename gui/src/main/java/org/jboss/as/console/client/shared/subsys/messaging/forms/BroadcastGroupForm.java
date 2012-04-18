package org.jboss.as.console.client.shared.subsys.messaging.forms;

import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.messaging.model.BroadcastGroup;
import org.jboss.as.console.client.shared.viewframework.builder.FormLayout;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.FormItem;
import org.jboss.ballroom.client.widgets.forms.ListItem;
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
public class BroadcastGroupForm {


    Form<BroadcastGroup> form = new Form<BroadcastGroup>(BroadcastGroup.class);

    boolean isCreate = false;
    private FormToolStrip.FormCallback<BroadcastGroup> callback;

    private MultiWordSuggestOracle oracle;


    public BroadcastGroupForm(FormToolStrip.FormCallback<BroadcastGroup> callback) {
        this.callback = callback;
        oracle = new MultiWordSuggestOracle();
        oracle.setDefaultSuggestionsFromText(Collections.EMPTY_LIST);
    }

    public BroadcastGroupForm(FormToolStrip.FormCallback<BroadcastGroup> callback, boolean create) {
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
                        address.add("broadcast-group", "*");
                        return address;
                    }
                }, form);

        FormToolStrip<BroadcastGroup> formTools = new FormToolStrip<BroadcastGroup>(form, callback);

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

        ListItem connectors= new ListItem("connectors", "Connectors");
        TextBoxItem socket = new TextBoxItem("socketBinding", "Socket Binding");
        NumberBoxItem period = new NumberBoxItem("broadcastPeriod", "Broadcast Period");

        form.setFields(name, socket, connectors, period);
    }

    public Form<BroadcastGroup> getForm() {
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
