package org.jboss.as.console.client.shared.general.forms;

import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.general.model.LocalSocketBinding;
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
public class LocalSocketForm {


    Form<LocalSocketBinding> form = new Form<LocalSocketBinding>(LocalSocketBinding.class);

    boolean isCreate = false;
    private FormToolStrip.FormCallback<LocalSocketBinding> callback;

    private MultiWordSuggestOracle oracle;


    public LocalSocketForm(FormToolStrip.FormCallback<LocalSocketBinding> callback) {
        this.callback = callback;
        oracle = new MultiWordSuggestOracle();
        oracle.setDefaultSuggestionsFromText(Collections.EMPTY_LIST);
    }

    public LocalSocketForm(FormToolStrip.FormCallback<LocalSocketBinding> callback, boolean create) {
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
                        address.add("socket-binding-group", "*");
                        address.add("local-destination-outbound-socket-binding", "*");
                        return address;
                    }
                }, form);

        FormToolStrip<LocalSocketBinding> formTools = new FormToolStrip<LocalSocketBinding>(form, callback);

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

        TextBoxItem socket = new TextBoxItem("socketBinding", "Socket Binding");

        NumberBoxItem sourcePort = new NumberBoxItem("sourcePort", "Source Port");
        TextBoxItem sourceInterface = new TextBoxItem("sourceInterface", "Source Interface");

        CheckBoxItem fixed = new CheckBoxItem("fixedSourcePort", "Fixed Source Port?");


        if(isCreate)
            form.setFields(name, socket);
        else
            form.setFields(name, socket, sourceInterface, sourcePort, fixed);
    }

    public Form<LocalSocketBinding> getForm() {
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
