package org.jboss.as.console.client.shared.general;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.general.model.SocketBinding;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.widgets.DialogueOptions;
import org.jboss.as.console.client.widgets.WindowContentBuilder;
import org.jboss.as.console.client.widgets.forms.ComboBoxItem;
import org.jboss.as.console.client.widgets.forms.Form;
import org.jboss.as.console.client.widgets.forms.FormValidation;
import org.jboss.as.console.client.widgets.forms.NumberBoxItem;
import org.jboss.as.console.client.widgets.forms.TextBoxItem;
import org.jboss.dmr.client.ModelNode;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 6/10/11
 */
public class NewSocketWizard {

    private SocketBindingPresenter presenter;
    private List<String> bindingGroups;

    public NewSocketWizard(SocketBindingPresenter socketBindingPresenter, List<String> bindingGroups) {
        this.presenter = socketBindingPresenter;
        this.bindingGroups = bindingGroups;
    }

    Widget asWidget() {
        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("window-content");

        final Form<SocketBinding> form = new Form(SocketBinding.class);

        TextBoxItem nameItem = new TextBoxItem("name", "Name");
        NumberBoxItem portItem = new NumberBoxItem("port", "Port");
        final ComboBoxItem groupItem = new ComboBoxItem("group", "Binding Group");
        groupItem.setDefaultToFirstOption(true);
        groupItem.setValueMap(bindingGroups);

        form.setFields(nameItem, portItem, groupItem);

        DialogueOptions options = new DialogueOptions(

                // save
                new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {

                        FormValidation validation = form.validate();
                        if(!validation.hasErrors())
                        {
                            SocketBinding newGroup = form.getUpdatedEntity();
                            presenter.createNewSocketBinding(newGroup);
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
                        address.add("socket-binding-group", "standard-sockets");
                        address.add("socket-binding", "*");
                        return address;
                    }
                }, form
        );
        layout.add(helpPanel.asWidget());

        layout.add(formWidget);

        return new WindowContentBuilder(layout, options).build();
    }
}
