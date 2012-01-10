package org.jboss.as.console.client.shared.subsys.jca;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.jca.model.WorkmanagerPool;
import org.jboss.ballroom.client.widgets.forms.ComboBoxItem;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.FormValidation;
import org.jboss.ballroom.client.widgets.forms.NumberBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.ballroom.client.widgets.window.DialogueOptions;
import org.jboss.ballroom.client.widgets.window.WindowContentBuilder;
import org.jboss.dmr.client.ModelNode;

/**
 * @author Heiko Braun
 * @date 12/1/11
 */
public class NewPoolWizard {
    private JcaPresenter presenter;
    private String managerName;

    public NewPoolWizard(JcaPresenter presenter, String managerName) {
        this.presenter = presenter;
        this.managerName = managerName;
    }

    Widget asWidget() {
        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("window-content");

        final Form<WorkmanagerPool> form = new Form(WorkmanagerPool.class);

        TextBoxItem nameField = new TextBoxItem("name", Console.CONSTANTS.common_label_name());

        final ComboBoxItem type = new ComboBoxItem("type", "Type");
        type.setDefaultToFirstOption(true);
        type.setValueMap(new String[] {"long-running"});

        NumberBoxItem maxThreads = new NumberBoxItem("maxThreadsCount", "Max Threads");
        NumberBoxItem maxThreadsPerCPU = new NumberBoxItem("maxThreadsPerCPU", "Max Threads/CPU");
        NumberBoxItem queueLength = new NumberBoxItem("queueLengthCount", "Queue Length");
        NumberBoxItem queueLengthPerCPU = new NumberBoxItem("queueLengthPerCPU", "Queue Length/CPU");

        maxThreads.setValue(10);
        maxThreadsPerCPU.setValue(20);
        queueLength.setValue(10);
        queueLengthPerCPU.setValue(20);

        form.setFields(nameField, type, maxThreads, maxThreadsPerCPU, queueLength, queueLengthPerCPU);

        DialogueOptions options = new DialogueOptions(

                // save
                new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        WorkmanagerPool pool = form.getUpdatedEntity();
                        pool.setShortRunning(type.getValue().equals("short-running"));

                        FormValidation validation = form.validate();
                        if(validation.hasErrors())
                            return;

                        presenter.createNewPool(managerName, pool);

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
                        address.add("workmanager", "*");
                        address.add("short-running-threads", "*");
                        return address;
                    }
                }, form
        );

        layout.add(helpPanel.asWidget());

        layout.add(formWidget);

        return new WindowContentBuilder(layout, options).build();
    }

}
