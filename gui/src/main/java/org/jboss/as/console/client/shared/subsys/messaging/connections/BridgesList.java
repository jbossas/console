package org.jboss.as.console.client.shared.subsys.messaging.connections;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SingleSelectionModel;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.subsys.messaging.forms.BridgeConnectionsForm;
import org.jboss.as.console.client.shared.subsys.messaging.forms.DefaultBridgeForm;
import org.jboss.as.console.client.shared.subsys.messaging.model.Bridge;
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
public class BridgesList {


    private ContentHeaderLabel serverName;
    private DefaultCellTable<Bridge> factoryTable;
    private ListDataProvider<Bridge> factoryProvider;
    private MsgConnectionsPresenter presenter;
    private DefaultBridgeForm defaultAttributes;
    private BridgeConnectionsForm connectionAttributes;

    public BridgesList(MsgConnectionsPresenter presenter) {
        this.presenter = presenter;
    }

    Widget asWidget() {


        serverName = new ContentHeaderLabel();

        factoryTable = new DefaultCellTable<Bridge>(10, new ProvidesKey<Bridge>() {
            @Override
            public Object getKey(Bridge Bridge) {
                return Bridge.getName();
            }
        });

        factoryProvider = new ListDataProvider<Bridge>();
        factoryProvider.addDataDisplay(factoryTable);

        Column<Bridge, String> nameColumn = new Column<Bridge, String>(new TextCell()) {
            @Override
            public String getValue(Bridge object) {
                return object.getName();
            }
        };

        Column<Bridge, String> queueColumn = new Column<Bridge, String>(new TextCell()) {
            @Override
            public String getValue(Bridge object) {
                return object.getQueueName();
            }
        };

        Column<Bridge, String> toColumn = new Column<Bridge, String>(new TextCell()) {
            @Override
            public String getValue(Bridge object) {
                return object.getForwardingAddress();
            }
        };

        factoryTable.addColumn(nameColumn, "Name");
        factoryTable.addColumn(queueColumn, "Queue");
        factoryTable.addColumn(toColumn, "Forward");

        // defaultAttributes
        defaultAttributes = new DefaultBridgeForm(new FormToolStrip.FormCallback<Bridge>() {
            @Override
            public void onSave(Map<String, Object> changeset) {
                presenter.onSaveBridge(getSelectedEntity().getName(), changeset);
            }
            @Override
            public void onDelete(Bridge entity) {

            }
        });

        connectionAttributes = new BridgeConnectionsForm(new FormToolStrip.FormCallback<Bridge>() {
            @Override
            public void onSave(Map<String, Object> changeset) {
                presenter.onSaveBridge(getSelectedEntity().getName(), changeset);
            }

            @Override
            public void onDelete(Bridge entity) {

            }
        });


        ToolStrip tools = new ToolStrip();
        tools.addToolButtonRight(
                new ToolButton(Console.CONSTANTS.common_label_add(), new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent clickEvent) {
                        presenter.launchNewBridgeWizard();
                    }
                }));

        tools.addToolButtonRight(
                new ToolButton(Console.CONSTANTS.common_label_remove(), new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent clickEvent) {

                        Feedback.confirm(
                                Console.MESSAGES.deleteTitle("Bridge"),
                                Console.MESSAGES.deleteConfirm("Bridge " + getSelectedEntity().getName()),
                                new Feedback.ConfirmationHandler() {
                                    @Override
                                    public void onConfirmation(boolean isConfirmed) {
                                        if (isConfirmed) {
                                            presenter.onDeleteBridge(getSelectedEntity().getName());
                                        }
                                    }
                                });

                    }

                }));

        MultipleToOneLayout layout = new MultipleToOneLayout()
                .setPlain(true)
                .setHeadlineWidget(serverName)
                .setDescription("The function of a bridge is to consume messages from a source queue, and forward them to a target address, typically on a different HornetQ server.")
                .setMaster("Bridges", factoryTable)
                .setMasterTools(tools)
                .addDetail("Common", defaultAttributes.asWidget())
                .addDetail("Connection Management", connectionAttributes.asWidget());

        defaultAttributes.getForm().bind(factoryTable);
        defaultAttributes.getForm().setEnabled(false);

        connectionAttributes.getForm().bind(factoryTable);
        connectionAttributes.getForm().setEnabled(false);

        return layout.build();
    }

    public void setBridges(List<Bridge> bridges) {
        factoryProvider.setList(bridges);
        serverName.setText("Bridges: Provider "+presenter.getCurrentServer());

        factoryTable.selectDefaultEntity();

         // populate oracle
        presenter.loadExistingQueueNames(new AsyncCallback<List<String>>() {
            @Override
            public void onFailure(Throwable throwable) {

            }

            @Override
            public void onSuccess(List<String> names) {
                defaultAttributes.setQueueNames(names);
            }
        });
    }

    public Bridge getSelectedEntity() {
        SingleSelectionModel<Bridge> selectionModel = (SingleSelectionModel<Bridge>)factoryTable.getSelectionModel();
        return selectionModel.getSelectedObject();
    }


}
