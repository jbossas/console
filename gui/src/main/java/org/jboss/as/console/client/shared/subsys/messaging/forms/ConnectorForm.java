package org.jboss.as.console.client.shared.subsys.messaging.forms;

import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.messaging.model.Connector;
import org.jboss.as.console.client.shared.subsys.messaging.model.ConnectorType;
import org.jboss.as.console.client.shared.viewframework.builder.FormLayout;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.NumberBoxItem;
import org.jboss.ballroom.client.widgets.forms.SuggestBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextAreaItem;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.dmr.client.ModelNode;

import java.util.Collections;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 4/3/12
 */
public class ConnectorForm {


    Form<Connector> form = new Form<Connector>(Connector.class);
    private ConnectorType type = null;

    boolean isCreate = false;
    private FormToolStrip.FormCallback<Connector> callback;

    private MultiWordSuggestOracle oracle;


    public ConnectorForm(FormToolStrip.FormCallback<Connector> callback, ConnectorType type) {
        this.callback = callback;
        oracle = new MultiWordSuggestOracle();
        oracle.setDefaultSuggestionsFromText(Collections.EMPTY_LIST);
        this.type = type;
    }

    public ConnectorForm(FormToolStrip.FormCallback<Connector> callback, ConnectorType type, boolean create) {
        this.callback = callback;
        isCreate = create;
        oracle = new MultiWordSuggestOracle();
        oracle.setDefaultSuggestionsFromText(Collections.EMPTY_LIST);
        this.type = type;
    }

    public Widget asWidget() {

        switch (type)
        {
            case GENERIC:
                buildGenericForm();
                break;
            case REMOTE:
                buildRemoteForm();
                break;
            case INVM:
                buildInvmForm();

        }

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
                        address.add(type.getResource(), "*");
                        return address;
                    }
                }, form);

        FormToolStrip<Connector> formTools = new FormToolStrip<Connector>(form, callback);

        FormLayout formLayout = new FormLayout()
                .setForm(form)
                .setHelp(helpPanel);

        if(!isCreate)
            formLayout.setSetTools(formTools);

        return formLayout.build();
    }

    private void buildInvmForm() {
        TextBoxItem name = new TextBoxItem("name", "Name");
        NumberBoxItem server = new NumberBoxItem("serverId", "Server ID");

        form.setFields(name, server);
    }

    private void buildRemoteForm() {
        TextBoxItem name = new TextBoxItem("name", "Name");
        SuggestBoxItem socket = new SuggestBoxItem("socketBinding", "Socket Binding");

        socket.setOracle(oracle);

        form.setFields(name,socket);
    }

    private void buildGenericForm() {
        TextBoxItem name = new TextBoxItem("name", "Name");
        SuggestBoxItem socket = new SuggestBoxItem("socketBinding", "Socket Binding");
        TextAreaItem factory= new TextAreaItem("factoryClass", "Factory Class");

        socket.setOracle(oracle);

        form.setFields(name,socket, factory);
    }

    public Form<Connector> getForm() {
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
