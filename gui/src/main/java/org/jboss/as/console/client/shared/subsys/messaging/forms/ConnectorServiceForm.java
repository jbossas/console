package org.jboss.as.console.client.shared.subsys.messaging.forms;

import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.messaging.model.ConnectorService;
import org.jboss.as.console.client.shared.viewframework.builder.FormLayout;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.FormItem;
import org.jboss.ballroom.client.widgets.forms.TextAreaItem;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextItem;
import org.jboss.dmr.client.ModelNode;

import java.util.Collections;

/**
 * @author Heiko Braun
 * @date 4/3/12
 */
public class ConnectorServiceForm {


    Form<ConnectorService> form = new Form<ConnectorService>(ConnectorService.class);

    boolean isCreate = false;
    private FormToolStrip.FormCallback<ConnectorService> callback;

    private MultiWordSuggestOracle oracle;


    public ConnectorServiceForm(FormToolStrip.FormCallback<ConnectorService> callback) {
        this.callback = callback;
        oracle = new MultiWordSuggestOracle();
        oracle.setDefaultSuggestionsFromText(Collections.EMPTY_LIST);

    }

    public ConnectorServiceForm(FormToolStrip.FormCallback<ConnectorService> callback, boolean create) {
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
                        address.add("connector-service", "*");
                        return address;
                    }
                }, form);

        FormToolStrip<ConnectorService> formTools = new FormToolStrip<ConnectorService>(form, callback);

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

        TextAreaItem factory= new TextAreaItem("factoryClass", "Factory Class");

        form.setFields(name, factory);
    }

    public Form<ConnectorService> getForm() {
        return form;
    }

    public void setIsCreate(boolean create) {
        isCreate = create;
    }
}
