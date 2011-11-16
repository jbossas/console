package org.jboss.as.console.client.shared.general;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.general.model.Interface;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.ballroom.client.widgets.forms.CheckBoxItem;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.FormValidation;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.ballroom.client.widgets.window.DialogueOptions;
import org.jboss.ballroom.client.widgets.window.WindowContentBuilder;
import org.jboss.dmr.client.ModelNode;

/**
 * @author Heiko Braun
 * @date 10/24/11
 */
public class NewInterfaceWizard {

    private InterfacePresenter presenter;

    public NewInterfaceWizard(InterfacePresenter presenter) {
        this.presenter = presenter;
    }

    Widget asWidget() {
        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("window-content");

        final Form<Interface> form = new Form(Interface.class);


        TextBoxItem nameItem = new TextBoxItem("name", "Name");

        TextBoxItem inetAddress = new TextBoxItem("inetAddress", "InetAddress", false);
        TextBoxItem nic = new TextBoxItem("nic", "Nic", false);
        TextBoxItem nicMatch = new TextBoxItem("nicMatch", "Nic Match", false);

        CheckBoxItem publicAddress = new CheckBoxItem("publicAddress", "Public Address");
        CheckBoxItem siteLocalAddress = new CheckBoxItem("siteLocal", "Site Local Address");

        form.setFields(nameItem, inetAddress, nic, nicMatch, publicAddress, siteLocalAddress);

        DialogueOptions options = new DialogueOptions(

                // save
                new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {

                        FormValidation validation = form.validate();
                        if(!validation.hasErrors())
                        {
                            Interface entity = form.getUpdatedEntity();
                            presenter.createNewInterface(entity);
                        }
                    }
                },

                // cancel
                new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        presenter.closeDialoge();
                    }
                }

        );

        // ----------------------------------------

        Widget formWidget = form.asWidget();

        final FormHelpPanel helpPanel = new FormHelpPanel(
                new FormHelpPanel.AddressCallback() {
                    @Override
                    public ModelNode getAddress() {
                        ModelNode address = new ModelNode();
                        address.add("interface", "*");
                        return address;
                    }
                }, form
        );
        layout.add(helpPanel.asWidget());

        layout.add(formWidget);

        return new WindowContentBuilder(layout, options).build();
    }
}
