package org.jboss.as.console.client.shared.subsys.jca;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.jca.model.ResourceAdapter;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
import org.jboss.ballroom.client.widgets.forms.CheckBoxItem;
import org.jboss.ballroom.client.widgets.forms.ComboBoxItem;
import org.jboss.ballroom.client.widgets.forms.DisclosureGroupRenderer;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextItem;
import org.jboss.dmr.client.ModelNode;

import java.util.Map;

/**
 * @author Heiko Braun
 * @date 7/19/11
 */
public class AdapterDetails {

    private VerticalPanel layout;
    private Form<ResourceAdapter> form;
    private ResourceAdapterPresenter presenter;

    public AdapterDetails(final ResourceAdapterPresenter presenter) {

        this.presenter = presenter;

        layout = new VerticalPanel();
        layout.setStyleName("fill-layout-width");

        form = new Form<ResourceAdapter>(ResourceAdapter.class);
        form.setNumColumns(2);

        FormToolStrip<ResourceAdapter> toolStrip = new FormToolStrip<ResourceAdapter>(
                form,
                new FormToolStrip.FormCallback<ResourceAdapter>() {
                    @Override
                    public void onSave(Map<String, Object> changeset) {
                        presenter.onSave(form.getEditedEntity(), form.getChangedValues());
                    }

                    @Override
                    public void onDelete(ResourceAdapter entity) {

                    }
                });

        toolStrip.providesDeleteOp(false);


        layout.add(toolStrip.asWidget());

        // ----

        TextItem nameItem = new TextItem("name", "Name");
        TextItem jndiItem = new TextItem("jndiName", "JNDI");
        CheckBoxItem enabled = new CheckBoxItem("enabled", "Enabled?");

        ComboBoxItem txItem = new ComboBoxItem("transactionSupport", "TX");
        txItem.setDefaultToFirstOption(true);
        txItem.setValueMap(new String[]{"NoTransaction", "LocalTransaction", "XATransaction"});

        TextBoxItem classItem = new TextBoxItem("connectionClass", "Connection Class");

        form.setFields(nameItem, jndiItem, enabled, txItem, classItem);

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

        form.setEnabled(false   );

    }

    Widget asWidget() {
        return layout;
    }

    public Form<ResourceAdapter> getForm() {
        return form;
    }

    public void setEnabled(boolean b) {
        form.setEnabled(b);
    }

    public ResourceAdapter getCurrentSelection() {
        return form.getEditedEntity();
    }
}
