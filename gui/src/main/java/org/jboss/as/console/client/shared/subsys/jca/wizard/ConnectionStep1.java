package org.jboss.as.console.client.shared.subsys.jca.wizard;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.jca.model.ConnectionDefinition;
import org.jboss.as.console.client.shared.subsys.jca.model.ResourceAdapter;
import org.jboss.as.console.client.widgets.forms.items.JndiNameItem;
import org.jboss.ballroom.client.widgets.forms.ComboBoxItem;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.FormValidation;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.ballroom.client.widgets.window.DialogueOptions;
import org.jboss.ballroom.client.widgets.window.WindowContentBuilder;
import org.jboss.dmr.client.ModelNode;

/**
 * @author Heiko Braun
 * @date 7/20/11
 */
public class ConnectionStep1 {

    NewConnectionWizard parent;

    public ConnectionStep1(NewConnectionWizard parent) {
        this.parent = parent;
    }

    public Widget asWidget() {

        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("window-content");

        layout.add(new HTML("<h2>Connection Definition Step1/2</h2>"));

        final Form<ConnectionDefinition> form = new Form(ConnectionDefinition.class);

        TextBoxItem jndiName = new JndiNameItem("jndiName", "JNDI Name");
        TextBoxItem classItem = new TextBoxItem("connectionClass", "Connection Class");

        form.setFields(jndiName, classItem);

        final FormHelpPanel helpPanel = new FormHelpPanel(
                new FormHelpPanel.AddressCallback() {
                    @Override
                    public ModelNode getAddress() {
                        ModelNode address = Baseadress.get();
                        address.add("subsystem", "resource-adapters");
                        address.add("resource-adapter", "*");
                        address.add("connection-definitions", "*");
                        return address;
                    }
                }, form
        );
        layout.add(helpPanel.asWidget());

        layout.add(form.asWidget());

        DialogueOptions options = new DialogueOptions(

                // save
                "Next &rsaquo;&rsaquo;",
                new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        FormValidation validation = form.validate();
                        if(!validation.hasErrors())
                           parent.onCompleteStep1(form.getUpdatedEntity());
                    }
                },

                // cancel
                "cancel",
                new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        parent.getPresenter().closeDialoge();
                    }
                }

        );

        // ----------------------------------------

        return new WindowContentBuilder(layout, options).build();
    }
}
