package org.jboss.as.console.client.shared.subsys.messaging.forms;

import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.messaging.model.DiscoveryGroup;
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
public class DiscoveryGroupForm {


    Form<DiscoveryGroup> form = new Form<DiscoveryGroup>(DiscoveryGroup.class);

    boolean isCreate = false;
    private FormToolStrip.FormCallback<DiscoveryGroup> callback;

    private MultiWordSuggestOracle oracle;


    public DiscoveryGroupForm(FormToolStrip.FormCallback<DiscoveryGroup> callback) {
        this.callback = callback;
        oracle = new MultiWordSuggestOracle();
        oracle.setDefaultSuggestionsFromText(Collections.EMPTY_LIST);
    }

    public DiscoveryGroupForm(FormToolStrip.FormCallback<DiscoveryGroup> callback, boolean create) {
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
                        address.add("discovery-group", "*");
                        return address;
                    }
                }, form);

        FormToolStrip<DiscoveryGroup> formTools = new FormToolStrip<DiscoveryGroup>(form, callback);

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

        NumberBoxItem initialWait= new NumberBoxItem("initialWaitTimeout", "Initial Wait Timeout");
        NumberBoxItem refresh = new NumberBoxItem("refreshTimeout", "Refresh Timeout");
        TextBoxItem socket = new TextBoxItem("socketBinding", "Socket Binding");

        if(isCreate)
            form.setFields(name, socket);
        else
            form.setFields(name, socket, initialWait, refresh);
    }

    public Form<DiscoveryGroup> getForm() {
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
