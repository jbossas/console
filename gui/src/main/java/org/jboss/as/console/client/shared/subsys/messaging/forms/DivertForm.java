package org.jboss.as.console.client.shared.subsys.messaging.forms;

import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.messaging.model.Divert;
import org.jboss.as.console.client.shared.viewframework.builder.FormLayout;
import org.jboss.as.console.client.widgets.forms.BlankItem;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
import org.jboss.ballroom.client.widgets.forms.CheckBoxItem;
import org.jboss.ballroom.client.widgets.forms.Form;
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
public class DivertForm {

    Form<Divert> form = new Form<Divert>(Divert.class);
    boolean isCreate = false;
    private FormToolStrip.FormCallback<Divert> callback;

    private MultiWordSuggestOracle oracle;


    public DivertForm(FormToolStrip.FormCallback<Divert> callback) {
        this.callback = callback;
        oracle = new MultiWordSuggestOracle();
        oracle.setDefaultSuggestionsFromText(Collections.EMPTY_LIST);
    }

    public DivertForm(FormToolStrip.FormCallback<Divert> callback, boolean create) {
        this.callback = callback;
        isCreate = create;
        oracle = new MultiWordSuggestOracle();
        oracle.setDefaultSuggestionsFromText(Collections.EMPTY_LIST);
    }

    public Widget asWidget() {

        TextBoxItem routingName = new TextBoxItem("routingName", "Routing Name");
        SuggestBoxItem divertFrom = new SuggestBoxItem("divertAddress", "Divert Address");
        SuggestBoxItem divertTo = new SuggestBoxItem("forwardingAddress", "Forwarding Address");

        divertFrom.setOracle(oracle);
        divertTo.setOracle(oracle);

        TextAreaItem filter = new TextAreaItem("filter", "Filter");
        TextAreaItem transformer = new TextAreaItem("transformerClass", "Transformer Class");

        CheckBoxItem exclusive = new CheckBoxItem("exclusive", "Exlusive?");

        if(isCreate)
        {
            form.setFields(
                    routingName,
                    divertFrom, divertTo
            );
            form.setNumColumns(1);

        }
        else {
            form.setFields(
                    routingName, BlankItem.INSTANCE,
                    divertFrom, divertTo,
                    exclusive, BlankItem.INSTANCE,
                    filter, transformer);

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
                        address.add("divert", "*");
                        return address;
                    }
                }, form);

        FormToolStrip<Divert> formTools = new FormToolStrip<Divert>(form, callback);

        FormLayout formLayout = new FormLayout()
                .setForm(form)
                .setHelp(helpPanel);

        if(!isCreate)
            formLayout.setSetTools(formTools);

        return formLayout.build();
    }

    public Form<Divert> getForm() {
        return form;
    }

    public void setIsCreate(boolean create) {
        isCreate = create;
    }

    public void setQueueNames(List<String> queueNames) {
        oracle.addAll(queueNames);
    }
}
