package org.jboss.as.console.client.shared.subsys.jgroups;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.ballroom.client.widgets.forms.ComboBoxItem;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.FormValidation;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.ballroom.client.widgets.window.DialogueOptions;
import org.jboss.ballroom.client.widgets.window.WindowContentBuilder;
import org.jboss.dmr.client.ModelNode;

/**
 * @author Heiko Braun
 * @date 2/22/12
 */
public class StackStep1 {

    NewStackWizard presenter;

    public StackStep1(NewStackWizard presenter) {
        this.presenter = presenter;
    }

    Widget asWidget() {
        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("window-content");

        layout.add(new HTML("<h3>"+ Console.CONSTANTS.subsys_jgroups_step1()+"</h3>"));

        final Form<JGroupsStack> form = new Form<JGroupsStack>(JGroupsStack.class);

        TextBoxItem nameField = new TextBoxItem("type", "Name");
        ComboBoxItem transportType = new ComboBoxItem("transportType", "Transport");
        transportType.setDefaultToFirstOption(true);
        transportType.setValueMap(new String[]{"UDP", "TCP", "TUNNEL"});

        form.setFields(nameField, transportType);

        // ----------------------------------------

        Widget formWidget = form.asWidget();

        FormHelpPanel helpPanel = new FormHelpPanel(new FormHelpPanel.AddressCallback() {
            @Override
            public ModelNode getAddress() {
                ModelNode address = Baseadress.get();
                address.add("subsystem", "jgroups");
                address.add("stack", "*");
                return address;
            }
        }, form);

        layout.add(helpPanel.asWidget());

        layout.add(formWidget);

        ClickHandler submitHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                FormValidation validation = form.validate();
                if(validation.hasErrors())
                    return;

                presenter.onFinishStep1(form.getUpdatedEntity());

            }
        };

        ClickHandler cancelHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.cancel();
            }
        };

        DialogueOptions options = new DialogueOptions(
                Console.CONSTANTS.common_label_next(),submitHandler,
                Console.CONSTANTS.common_label_cancel(),cancelHandler
        );

        return new WindowContentBuilder(layout, options).build();
    }
}
