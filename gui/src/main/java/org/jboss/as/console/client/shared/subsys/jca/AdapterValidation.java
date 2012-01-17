package org.jboss.as.console.client.shared.subsys.jca;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.jca.model.ConnectionDefinition;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
import org.jboss.ballroom.client.widgets.forms.CheckBoxItem;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.NumberBoxItem;
import org.jboss.dmr.client.ModelNode;

import java.util.Map;

/**
 * @author Heiko Braun
 * @date 12/12/11
 */
public class AdapterValidation {
    private ResourceAdapterPresenter presenter;
    private Form<ConnectionDefinition> form;

    public AdapterValidation(ResourceAdapterPresenter presenter) {
        this.presenter = presenter;
    }

    Widget asWidget() {
        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("fill-layout-width");


        form = new Form<ConnectionDefinition>(ConnectionDefinition.class);

        CheckBoxItem enabled = new CheckBoxItem("backgroundValidation", "Validation Enabled?");
        NumberBoxItem timeout= new NumberBoxItem("backgroundValidationMillis", "Validation Timeout") {
            @Override
            public boolean isRequired() {
                return false;
            }
        };
        form.setFields(enabled, timeout);
        form.setEnabled(false);
        form.setNumColumns(2);

        FormToolStrip<ConnectionDefinition> tools = new FormToolStrip<ConnectionDefinition>(
                form,

                new FormToolStrip.FormCallback<ConnectionDefinition>() {
                    @Override
                    public void onSave(Map<String, Object> changeset) {
                        presenter.onSaveConnection(form.getEditedEntity(), changeset);
                    }

                    @Override
                    public void onDelete(ConnectionDefinition entity) {

                    }
                }
        );

        tools.providesDeleteOp(false);

        layout.add(tools.asWidget());

        final FormHelpPanel helpPanel = new FormHelpPanel(
                new FormHelpPanel.AddressCallback() {
                    @Override
                    public ModelNode getAddress() {
                        ModelNode address = Baseadress.get();
                        address.add("subsystem", "resource-adapters");
                        address.add("resource-adapter", "*");
                        address.add("connection-definitions", "*");
                        return address;
                    }
                }, form
        );
        layout.add(helpPanel.asWidget());

        layout.add(form.asWidget());
        return layout;
    }

    public void updateFrom(ConnectionDefinition connectionDefinition) {
        form.edit(connectionDefinition);
    }
}
