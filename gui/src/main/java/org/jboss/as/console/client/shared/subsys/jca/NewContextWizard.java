package org.jboss.as.console.client.shared.subsys.jca;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.jca.model.JcaBootstrapContext;
import org.jboss.as.console.client.shared.subsys.jca.model.JcaWorkmanager;
import org.jboss.ballroom.client.widgets.forms.ComboBoxItem;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.FormValidation;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.ballroom.client.widgets.window.DialogueOptions;
import org.jboss.ballroom.client.widgets.window.WindowContentBuilder;
import org.jboss.dmr.client.ModelNode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 12/1/11
 */
public class NewContextWizard {

    private JcaPresenter presenter;
    private List<JcaWorkmanager> managers;

    public NewContextWizard(JcaPresenter jcaPresenter, List<JcaWorkmanager> managers) {
        this.presenter = jcaPresenter;
        this.managers = managers;
    }

    Widget asWidget() {
        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("window-content");

        final Form<JcaBootstrapContext> form = new Form(JcaBootstrapContext.class);

        TextBoxItem nameField = new TextBoxItem("name", Console.CONSTANTS.common_label_name());
        ComboBoxItem managerSelection = new ComboBoxItem("workmanager", "Work Manager");

        List<String> names = new ArrayList<String>(managers.size());

        for(JcaWorkmanager manager : managers)
            names.add(manager.getName());

        managerSelection.setDefaultToFirstOption(true);
        managerSelection.setValueMap(names);

        form.setFields(nameField, managerSelection);

        DialogueOptions options = new DialogueOptions(

                // save
                new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        // merge base
                        JcaBootstrapContext context = form.getUpdatedEntity();

                        FormValidation validation = form.validate();
                        if(validation.hasErrors())
                            return;

                        presenter.createNewContext(context);

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
                        address.set(Baseadress.get());
                        address.add("subsystem", "jca");
                        address.add("bootstrap-context", "*");
                        return address;
                    }
                }, form
        );

        layout.add(helpPanel.asWidget());

        layout.add(formWidget);

        return new WindowContentBuilder(layout, options).build();
    }
}
