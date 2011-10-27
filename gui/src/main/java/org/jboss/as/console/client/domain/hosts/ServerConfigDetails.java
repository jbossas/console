package org.jboss.as.console.client.domain.hosts;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.domain.model.Server;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
import org.jboss.ballroom.client.widgets.forms.CheckBoxItem;
import org.jboss.ballroom.client.widgets.forms.ComboBoxItem;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.NumberBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextItem;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.window.Feedback;
import org.jboss.dmr.client.ModelNode;

import java.util.List;
import java.util.Map;

/**
 * @author Heiko Braun
 * @date 10/27/11
 */
public class ServerConfigDetails {

    private ServerConfigPresenter presenter;
    private Form<Server> form;
    private ComboBoxItem socketItem;

    public ServerConfigDetails(ServerConfigPresenter presenter) {
        this.presenter = presenter;
    }

    Widget asWidget() {
        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("fill-layout-width");

        form = new Form<Server>(Server.class);
        form.setNumColumns(2);

        FormToolStrip<Server> toolStrip = new FormToolStrip<Server>(
                form, new FormToolStrip.FormCallback<Server>() {

            @Override
            public void onSave(Map<String, Object> changeset) {
                presenter.onSaveChanges(form.getEditedEntity(), changeset);
            }

            @Override
            public void onDelete(Server entity) {
                Feedback.confirm(
                        Console.MESSAGES.deleteServerConfig(),
                        Console.MESSAGES.deleteServerConfigConfirm(form.getEditedEntity().getName()),
                        new Feedback.ConfirmationHandler() {
                            @Override
                            public void onConfirmation(boolean isConfirmed) {
                                if (isConfirmed)
                                    presenter.tryDeleteCurrentRecord();
                            }
                        });
            }
        });


        layout.add(toolStrip.asWidget());


        TextItem nameItem = new TextItem("name", "Name");

        CheckBoxItem startedItem = new CheckBoxItem("autoStart", Console.CONSTANTS.common_label_autoStart());
        TextItem groupItem = new TextItem("group", Console.CONSTANTS.common_label_serverGroup());

        // ------------------------------------------------------

        final NumberBoxItem portOffset = new NumberBoxItem("portOffset", Console.CONSTANTS.common_label_portOffset());

        socketItem = new ComboBoxItem("socketBinding", Console.CONSTANTS.common_label_socketBinding())
        {
            @Override
            public boolean validate(String value) {
                boolean parentValid = super.validate(value);
                //boolean portDefined = !portOffset.isModified();
                return parentValid ;//&& portDefined;
            }

            @Override
            public String getErrMessage() {
                return Console.MESSAGES.common_validation_portOffsetUndefined(super.getErrMessage());
            }
        };


        form.setFields(nameItem, groupItem, socketItem, portOffset, startedItem);

        final FormHelpPanel helpPanel = new FormHelpPanel(
                new FormHelpPanel.AddressCallback() {
                    @Override
                    public ModelNode getAddress() {
                        ModelNode address = new ModelNode();
                        address.add("host", presenter.getSelectedHost());
                        address.add("server-config", "*");
                        return address;
                    }
                }, form
        );
        layout.add(helpPanel.asWidget());

        form.setEnabled(false);
        layout.add(form.asWidget());

        return layout;
    }

    public void setAvailableSockets(List<String> result) {
        socketItem.clearValue();
        socketItem.clearSelection();

        socketItem.setValueMap(result);
    }

    public void bind(DefaultCellTable table) {
        form.bind(table);
    }
}
