package org.jboss.as.console.client.shared.general.forms;

import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.general.model.RemoteSocketBinding;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.viewframework.builder.FormLayout;
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
public class RemoteSocketForm {


    Form<RemoteSocketBinding> form = new Form<RemoteSocketBinding>(RemoteSocketBinding.class);

    boolean isCreate = false;
    private FormToolStrip.FormCallback<RemoteSocketBinding> callback;


    public RemoteSocketForm(FormToolStrip.FormCallback<RemoteSocketBinding> callback) {
        this.callback = callback;

    }

    public RemoteSocketForm(FormToolStrip.FormCallback<RemoteSocketBinding> callback, boolean create) {
        this.callback = callback;
        isCreate = create;

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

                        ModelNode address = new ModelNode();
                        address.add("socket-binding-group", "*");
                        address.add("remote-destination-outbound-socket-binding", "*");
                        return address;
                    }
                }, form);

        FormToolStrip<RemoteSocketBinding> formTools = new FormToolStrip<RemoteSocketBinding>(form, callback);

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

        NumberBoxItem port= new NumberBoxItem("port", "Port");
        TextBoxItem host = new TextBoxItem("host", "Host");

        NumberBoxItem sourcePort = new NumberBoxItem("sourcePort", "Source Port");
        TextBoxItem sourceInterface = new TextBoxItem("sourceInterface", "Source Interface");

        CheckBoxItem fixed = new CheckBoxItem("fixedSourcePort", "Fixed Source Port?");


        if(isCreate)
            form.setFields(name, host, port);
        else
            form.setFields(name, host, port, sourceInterface, sourcePort, fixed);
    }

    public Form<RemoteSocketBinding> getForm() {
        return form;
    }

    public void setIsCreate(boolean create) {
        isCreate = create;
    }
}
