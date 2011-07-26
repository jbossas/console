package org.jboss.as.console.client.shared.subsys.web;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.web.model.VirtualServer;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.FormValidation;
import org.jboss.ballroom.client.widgets.forms.ListItem;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.ballroom.client.widgets.window.DialogueOptions;
import org.jboss.ballroom.client.widgets.window.WindowContentBuilder;
import org.jboss.dmr.client.ModelNode;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 6/10/11
 */
public class NewVirtualServerWizard {
    private WebPresenter presenter;
    private List<VirtualServer> existing;

    public NewVirtualServerWizard(WebPresenter presenter, List<VirtualServer> virtualServers) {
        this.presenter = presenter;
        this.existing = virtualServers;
    }

    Widget asWidget() {
        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("window-content");

        // ---

        final Form<VirtualServer> form = new Form<VirtualServer>(VirtualServer.class);

        TextBoxItem name = new TextBoxItem("name", "Name");
        ListItem alias = new ListItem("alias", "Alias")
        {
            @Override
            public boolean isRequired() {
                return false;
            }
        };
        TextBoxItem defaultModule = new TextBoxItem("defaultWebModule", "Default Module")
        {

            @Override
            public boolean isRequired() {
                return false;
            }
        };


        form.setFields(name, alias, defaultModule);

        final FormHelpPanel helpPanel = new FormHelpPanel(
                new FormHelpPanel.AddressCallback() {
                    @Override
                    public ModelNode getAddress() {
                        ModelNode address = Baseadress.get();
                        address.add("subsystem", "web");
                        address.add("virtual-server", "*");
                        return address;
                    }
                }, form
        );
        layout.add(helpPanel.asWidget());

        layout.add(form.asWidget());

        DialogueOptions options = new DialogueOptions(

                // save
                new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {

                        FormValidation validation = form.validate();
                        if(!validation.hasErrors())
                        {
                            VirtualServer entity = form.getUpdatedEntity();
                            presenter.onCreateVirtualServer(entity);
                        }
                    }
                },

                // cancel
                new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        presenter.closeDialogue();
                    }
                }

        );

        return new WindowContentBuilder(layout, options).build();
    }
}
