package org.jboss.as.console.client.shared.subsys.mail;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.ballroom.client.widgets.forms.CheckBoxItem;
import org.jboss.ballroom.client.widgets.forms.ComboBoxItem;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.FormValidation;
import org.jboss.ballroom.client.widgets.forms.PasswordBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.ballroom.client.widgets.window.DialogueOptions;
import org.jboss.ballroom.client.widgets.window.WindowContentBuilder;
import org.jboss.dmr.client.ModelNode;

/**
 * @author Heiko Braun
 * @date 11/28/11
 */
public class NewMailServerWizard {
    private MailPresenter presenter;

    public NewMailServerWizard(MailPresenter presenter) {
        this.presenter = presenter;
    }

    Widget asWidget() {
        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("window-content");

        final Form<MailServerDefinition> form = new Form(MailServerDefinition.class);

        TextBoxItem socket = new TextBoxItem("socketBinding", "Socket Binding");
        TextBoxItem user = new TextBoxItem("username", "Username");
        PasswordBoxItem pass = new PasswordBoxItem("password", "Password");
        CheckBoxItem ssl = new CheckBoxItem("ssl", "Use SSL?");

        final ComboBoxItem type = new ComboBoxItem("type", "Type");

        type.setValueMap(new String[]{
                ServerType.smtp.name(),
                ServerType.imap.name(),
                ServerType.pop3.name()
        });

        type.setDefaultToFirstOption(true);

        form.setFields(socket, type, user, pass, ssl);

        DialogueOptions options = new DialogueOptions(

                // save
                new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        // merge base

                        FormValidation validation = form.validate();
                        if(validation.hasErrors())
                            return;

                        MailServerDefinition entity = form.getUpdatedEntity();
                        entity.setType(ServerType.valueOf(type.getValue()));

                        presenter.onCreateServer(entity);

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
                        ModelNode address = Baseadress.get();
                        address.add("subsystem", "mail");
                        address.add("mail-session", "*");
                        address.add("server", "smtp");
                        return address;
                    }
                }, form
        );

        layout.add(helpPanel.asWidget());

        layout.add(formWidget);

        return new WindowContentBuilder(layout, options).build();
    }
}
