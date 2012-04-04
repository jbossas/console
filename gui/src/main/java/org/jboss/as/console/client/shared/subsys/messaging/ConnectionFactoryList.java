package org.jboss.as.console.client.shared.subsys.messaging;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SingleSelectionModel;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.subsys.messaging.forms.CFConnectionsForm;
import org.jboss.as.console.client.shared.subsys.messaging.forms.DefaultCFForm;
import org.jboss.as.console.client.shared.subsys.messaging.model.ConnectionFactory;
import org.jboss.as.console.client.shared.viewframework.builder.MultipleToOneLayout;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
import org.jboss.ballroom.client.widgets.ContentHeaderLabel;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;
import org.jboss.ballroom.client.widgets.window.Feedback;

import java.util.List;
import java.util.Map;

/**
 * @author Heiko Braun
 * @date 4/2/12
 */
public class ConnectionFactoryList {


    private ContentHeaderLabel serverName;
    private DefaultCellTable<ConnectionFactory> factoryTable;
    private ListDataProvider<ConnectionFactory> factoryProvider;
    private MsgDestinationsPresenter presenter;
    private DefaultCFForm defaultAttributes;
    private CFConnectionsForm connectionAttributes;

    public ConnectionFactoryList(MsgDestinationsPresenter presenter) {
        this.presenter = presenter;
    }

    Widget asWidget() {


        serverName = new ContentHeaderLabel();

        factoryTable = new DefaultCellTable<ConnectionFactory>(10, new ProvidesKey<ConnectionFactory>() {
            @Override
            public Object getKey(ConnectionFactory connectionFactory) {
                return connectionFactory.getName();
            }
        });

        factoryProvider = new ListDataProvider<ConnectionFactory>();
        factoryProvider.addDataDisplay(factoryTable);

        Column<ConnectionFactory, String> nameColumn = new Column<ConnectionFactory, String>(new TextCell()) {
            @Override
            public String getValue(ConnectionFactory object) {
                return object.getName();
            }
        };

        Column<ConnectionFactory, String> jndiColumn = new Column<ConnectionFactory, String>(new TextCell()) {
            @Override
            public String getValue(ConnectionFactory object) {
                return object.getJndiName();
            }
        };

        factoryTable.addColumn(nameColumn, "Name");
        factoryTable.addColumn(jndiColumn, "JNDI");


        // defaultAttributes
        defaultAttributes = new DefaultCFForm(new FormToolStrip.FormCallback<ConnectionFactory>() {
            @Override
            public void onSave(Map<String, Object> changeset) {
                presenter.saveConnnectionFactory(getSelectedFactory().getName(), changeset);
            }
            @Override
            public void onDelete(ConnectionFactory entity) {

            }
        });

        connectionAttributes = new CFConnectionsForm(new FormToolStrip.FormCallback<ConnectionFactory>() {
            @Override
            public void onSave(Map<String, Object> changeset) {
                presenter.saveConnnectionFactory(getSelectedFactory().getName(), changeset);
            }

            @Override
            public void onDelete(ConnectionFactory entity) {

            }
        });


        ToolStrip tools = new ToolStrip();
        tools.addToolButtonRight(
                new ToolButton(Console.CONSTANTS.common_label_add(), new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent clickEvent) {
                        presenter.launchNewCFWizard();
                    }
                }));

        tools.addToolButtonRight(
                new ToolButton(Console.CONSTANTS.common_label_remove(), new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent clickEvent) {

                        Feedback.confirm(
                                Console.MESSAGES.deleteTitle("Connection Factory"),
                                Console.MESSAGES.deleteConfirm("Connection Factory " + getSelectedFactory().getName()),
                                new Feedback.ConfirmationHandler() {
                                    @Override
                                    public void onConfirmation(boolean isConfirmed) {
                                        if (isConfirmed) {
                                            presenter.onDeleteCF(getSelectedFactory().getName());
                                        }
                                    }
                                });

                    }

                }));

        MultipleToOneLayout layout = new MultipleToOneLayout()
                .setPlain(true)
                .setHeadlineWidget(serverName)
                .setDescription("Connection factories for applications. Used to connect to the server using the JMS API.")
                .setMaster("Connection Factories", factoryTable)
                .setMasterTools(tools)
                .addDetail("Common", defaultAttributes.asWidget())
                .addDetail("Connection Management", connectionAttributes.asWidget());

        defaultAttributes.getForm().bind(factoryTable);
        defaultAttributes.getForm().setEnabled(false);

        connectionAttributes.getForm().bind(factoryTable);
        connectionAttributes.getForm().setEnabled(false);

        return layout.build();
    }

    public void setFactories(List<ConnectionFactory> factories) {
        factoryProvider.setList(factories);
        serverName.setText("Connection Factories: Provider "+presenter.getCurrentServer());

        factoryTable.selectDefaultEntity();
    }

    public ConnectionFactory getSelectedFactory() {
        SingleSelectionModel<ConnectionFactory> selectionModel = (SingleSelectionModel<ConnectionFactory>)factoryTable.getSelectionModel();
        return selectionModel.getSelectedObject();
    }


}
