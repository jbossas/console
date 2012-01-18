package org.jboss.as.console.client.shared.subsys.ejb3;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.subsys.ejb3.model.Module;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.ballroom.client.widgets.window.DialogueOptions;
import org.jboss.ballroom.client.widgets.window.WindowContentBuilder;

/**
 * @author Heiko Braun
 * @date 11/28/11
 */
public class NewModuleWizard {
    private EEPresenter presenter;

    public NewModuleWizard(EEPresenter eePresenter) {
        this.presenter = eePresenter;

    }

    public Widget asWidget() {
        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("window-content");

        final Form<Module> form = new Form<Module>(Module.class);

       /* FormHelpPanel helpPanel = new FormHelpPanel(new FormHelpPanel.AddressCallback() {
            @Override
            public ModelNode getAddress() {
                ModelNode address = Baseadress.get();
                address.add("subsystem", "ee");
                return address;
            }
        }, form);*/


        TextBoxItem name = new TextBoxItem("name", "Name");
        TextBoxItem slot = new TextBoxItem("slot", "Slot");
        slot.setValue("main"); // default slot

        form.setFields(name, slot);

        //layout.add(helpPanel.asWidget());
        layout.add(form.asWidget());

        DialogueOptions options = new DialogueOptions(

                // save
                new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {

                        Module module = form.getUpdatedEntity();
                        presenter.onAddModule(module);

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


        return new WindowContentBuilder(layout, options).build();
    }

}
