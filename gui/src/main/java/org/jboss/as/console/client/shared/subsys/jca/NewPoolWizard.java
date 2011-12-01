package org.jboss.as.console.client.shared.subsys.jca;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.threads.model.BoundedQueueThreadPool;
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
    private WorkmanagerPresenter presenter;
    private boolean isShortRunning;

    public NewPoolWizard(WorkmanagerPresenter presenter, boolean shortRunning) {
        this.presenter = presenter;
        this.isShortRunning = shortRunning;
    }

    Widget asWidget() {
        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("window-content");

        final Form<BoundedQueueThreadPool> form = new Form(BoundedQueueThreadPool.class);

        TextBoxItem nameField = new TextBoxItem("name", Console.CONSTANTS.common_label_name());

        NumberBoxItem maxThreads = new NumberBoxItem("maxThreadsCount", "Max Threads");
        NumberBoxItem maxThreadsPerCPU = new NumberBoxItem("maxThreadsPerCPU", "Max Threads/CPU");
        NumberBoxItem queueLength = new NumberBoxItem("queueLengthCount", "Queue Length");
        NumberBoxItem queueLengthPerCPU = new NumberBoxItem("queueLengthPerCPU", "Queue Length/CPU");

        maxThreads.setValue(10);
        maxThreadsPerCPU.setValue(20);
        queueLength.setValue(10);
        queueLengthPerCPU.setValue(20);

        form.setFields(nameField, maxThreads, maxThreadsPerCPU, queueLength, queueLengthPerCPU);

        DialogueOptions options = new DialogueOptions(

                // save
                new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        BoundedQueueThreadPool pool = form.getUpdatedEntity();

                        FormValidation validation = form.validate();
                        if(validation.hasErrors())
                            return;

                        presenter.createNewPool(pool, isShortRunning);

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
