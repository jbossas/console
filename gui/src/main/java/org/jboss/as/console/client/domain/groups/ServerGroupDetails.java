package org.jboss.as.console.client.domain.groups;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
import org.jboss.ballroom.client.widgets.forms.ComboBoxItem;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.TextItem;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.dmr.client.ModelNode;

import java.util.List;
import java.util.Map;

/**
 * @author Heiko Braun
 * @date 10/27/11
 */
public class ServerGroupDetails {

    private Form<ServerGroupRecord> form;
    private ComboBoxItem socketBindingItem;
    private ServerGroupPresenter presenter;

    public ServerGroupDetails(ServerGroupPresenter presenter) {
        this.presenter = presenter;
    }

    Widget asWidget() {

        VerticalPanel layout = new VerticalPanel();

        form = new Form<ServerGroupRecord>(ServerGroupRecord.class);
        form.setNumColumns(2);

        FormToolStrip<ServerGroupRecord> toolstrip = new FormToolStrip<ServerGroupRecord>(
                form,
                new FormToolStrip.FormCallback<ServerGroupRecord>() {
                    @Override
                    public void onSave(Map<String, Object> changeset) {
                        presenter.onSaveChanges(form.getEditedEntity(), changeset);
                    }

                    @Override
                    public void onDelete(ServerGroupRecord entity) {
                        presenter.onDeleteGroup(entity);
                    }
                }
        );

        toolstrip.providesDeleteOp(false);

        TextItem nameItem = new TextItem("groupName", "Name");
        TextItem profileItem = new TextItem("profileName", Console.CONSTANTS.common_label_profile());
        socketBindingItem = new ComboBoxItem("socketBinding", Console.CONSTANTS.common_label_socketBinding());
        socketBindingItem.setDefaultToFirstOption(true);

        form.setFields(nameItem, profileItem, socketBindingItem);

        final FormHelpPanel helpPanel = new FormHelpPanel(
                new FormHelpPanel.AddressCallback() {
                    @Override
                    public ModelNode getAddress() {
                        ModelNode address = new ModelNode();
                        address.add("server-group", "*");
                        return address;
                    }
                }, form
        );

        layout.add(toolstrip.asWidget());
        layout.add(helpPanel.asWidget());
        layout.add(form.asWidget());

        return layout;
    }

    public void setSocketBindings(List<String> result) {
        socketBindingItem.clearValue();
        socketBindingItem.clearSelection();
        socketBindingItem.setValueMap(result);
    }

    public void bind(DefaultCellTable<ServerGroupRecord> serverGroupTable) {
        form.bind(serverGroupTable);
    }
}
