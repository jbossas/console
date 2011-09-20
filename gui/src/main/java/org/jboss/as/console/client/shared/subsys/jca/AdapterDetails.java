package org.jboss.as.console.client.shared.subsys.jca;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.jca.model.ResourceAdapter;
import org.jboss.ballroom.client.widgets.forms.ComboBoxItem;
import org.jboss.ballroom.client.widgets.forms.DisclosureGroupRenderer;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextItem;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;
import org.jboss.ballroom.client.widgets.window.Feedback;
import org.jboss.dmr.client.ModelNode;

/**
 * @author Heiko Braun
 * @date 7/19/11
 */
public class AdapterDetails {

    private VerticalPanel layout;
    private Form<ResourceAdapter> form;
    private ToolButton editBtn;
    private ResourceAdapterPresenter presenter;

    public AdapterDetails(final ResourceAdapterPresenter presenter) {

        this.presenter = presenter;

        layout = new VerticalPanel();
        layout.setStyleName("fill-layout-width");

        form = new Form<ResourceAdapter>(ResourceAdapter.class);
        form.setNumColumns(2);

        ToolStrip detailToolStrip = new ToolStrip();
        editBtn = new ToolButton(Console.CONSTANTS.common_label_edit());
        ClickHandler editHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {


                if(null == form.getEditedEntity())
                    return;

                if(editBtn.getText().equals(Console.CONSTANTS.common_label_edit()))
                    presenter.onEdit(form.getEditedEntity());
                else
                    presenter.onSave(form.getEditedEntity().getName(), form.getChangedValues());
            }
        };
        editBtn.addClickHandler(editHandler);
        detailToolStrip.addToolButton(editBtn);

        layout.add(detailToolStrip);

        // ----

        TextItem nameItem = new TextItem("name", "Name");
        TextBoxItem poolItem = new TextBoxItem("poolName", "Pool");
        TextBoxItem jndiItem = new TextBoxItem("jndiName", "JNDI");
        TextItem archiveItem = new TextItem("archive", "Archive");

        ComboBoxItem txItem = new ComboBoxItem("transactionSupport", "TX");
        txItem.setDefaultToFirstOption(true);
        txItem.setValueMap(new String[]{"NoTransaction", "LocalTransaction", "XATransaction"});

        TextBoxItem classItem = new TextBoxItem("connectionClass", "Connection Class");


        form.setFields(nameItem, jndiItem, poolItem, archiveItem);
        form.setFieldsInGroup("Advanced", new DisclosureGroupRenderer(), txItem, classItem);

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

        if(!b)
            editBtn.setText("Edit");
        else
            editBtn.setText("Save");
    }

    public ResourceAdapter getCurrentSelection() {
        return form.getEditedEntity();
    }
}
