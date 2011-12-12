package org.jboss.as.console.client.shared.subsys.jca.wizard;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.jca.ResourceAdapterPresenter;
import org.jboss.as.console.client.shared.subsys.jca.model.ResourceAdapter;
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
public class NewAdapterWizard {


    private ResourceAdapterPresenter presenter;

    public NewAdapterWizard(ResourceAdapterPresenter presenter) {
        this.presenter = presenter;
    }

    public Widget asWidget() {

        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("window-content");

        final Form<ResourceAdapter> form = new Form(ResourceAdapter.class);

        TextBoxItem archiveItem = new TextBoxItem("archive", "Archive");

        ComboBoxItem txItem = new ComboBoxItem("transactionSupport", "TX");
        txItem.setDefaultToFirstOption(true);
        txItem.setValueMap(new String[]{"NoTransaction", "LocalTransaction", "XATransaction"});

        form.setFields(archiveItem, txItem);

        final FormHelpPanel helpPanel = new FormHelpPanel(
                new FormHelpPanel.AddressCallback() {
                    @Override
                    public ModelNode getAddress() {
                        ModelNode address = Baseadress.get();
                        address.add("subsystem", "resource-adapters");
                        address.add("resource-adapter", "*");
                        return address;
                    }
                }, form
        );
        layout.add(helpPanel.asWidget());

        layout.add(form.asWidget());

        DialogueOptions options = new DialogueOptions(

                new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        FormValidation validation = form.validate();
                        if(!validation.hasErrors())
                            presenter.onCreateAdapter(form.getUpdatedEntity());
                    }
                },
                new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        presenter.closeDialoge();
                    }
                }

        );

        // ----------------------------------------

        return new WindowContentBuilder(layout, options).build();
    }

    ResourceAdapterPresenter getPresenter() {
        return presenter;
    }
}
