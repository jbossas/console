package org.jboss.as.console.client.shared.subsys.messaging.cluster;

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
import org.jboss.as.console.client.shared.subsys.messaging.forms.ClusterConnectionForm;
import org.jboss.as.console.client.shared.subsys.messaging.model.ClusterConnection;
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
public class ClusterConnectionList {


    private ContentHeaderLabel serverName;
    private DefaultCellTable<ClusterConnection> factoryTable;
    private ListDataProvider<ClusterConnection> factoryProvider;
    private MsgClusteringPresenter presenter;
    private ClusterConnectionForm defaultAttributes;

    public ClusterConnectionList(MsgClusteringPresenter presenter) {
        this.presenter = presenter;
    }

    Widget asWidget() {


        serverName = new ContentHeaderLabel();

        factoryTable = new DefaultCellTable<ClusterConnection>(10, new ProvidesKey<ClusterConnection>() {
            @Override
            public Object getKey(ClusterConnection ClusterConnection) {
                return ClusterConnection.getName();
            }
        });

        factoryProvider = new ListDataProvider<ClusterConnection>();
        factoryProvider.addDataDisplay(factoryTable);

        Column<ClusterConnection, String> nameColumn = new Column<ClusterConnection, String>(new TextCell()) {
            @Override
            public String getValue(ClusterConnection object) {
                return object.getName();
            }
        };


        factoryTable.addColumn(nameColumn, "Name");

        // defaultAttributes
        defaultAttributes = new ClusterConnectionForm(new FormToolStrip.FormCallback<ClusterConnection>() {
            @Override
            public void onSave(Map<String, Object> changeset) {
                presenter.saveClusterConnection(getSelectedEntity().getName(), changeset);
            }
            @Override
            public void onDelete(ClusterConnection entity) {

            }
        });

        ToolStrip tools = new ToolStrip();
        tools.addToolButtonRight(
                new ToolButton(Console.CONSTANTS.common_label_add(), new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent clickEvent) {
                        presenter.launchNewClusterConnectionWizard();
                    }
                }));

        tools.addToolButtonRight(
                new ToolButton(Console.CONSTANTS.common_label_remove(), new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent clickEvent) {

                        Feedback.confirm(
                                Console.MESSAGES.deleteTitle("ClusterConnection"),
                                Console.MESSAGES.deleteConfirm("ClusterConnection " + getSelectedEntity().getName()),
                                new Feedback.ConfirmationHandler() {
                                    @Override
                                    public void onConfirmation(boolean isConfirmed) {
                                        if (isConfirmed) {
                                            presenter.onDeleteClusterConnection(getSelectedEntity().getName());
                                        }
                                    }
                                });

                    }

                }));

        MultipleToOneLayout layout = new MultipleToOneLayout()
                .setPlain(true)
                .setHeadlineWidget(serverName)
                .setDescription("Multicast group to listen to receive broadcast from other servers announcing their connectors.")
                .setMaster("ClusterConnections", factoryTable)
                .setMasterTools(tools)
                .setDetail("Details", defaultAttributes.asWidget());

        defaultAttributes.getForm().bind(factoryTable);
        defaultAttributes.getForm().setEnabled(false);

        return layout.build();
    }

    public void setClusterConnections(List<ClusterConnection> ClusterConnections) {
        factoryProvider.setList(ClusterConnections);
        serverName.setText("ClusterConnections: Provider "+presenter.getCurrentServer());

        factoryTable.selectDefaultEntity();

         // populate oracle
        presenter.loadExistingSocketBindings(new AsyncCallback<List<String>>() {
            @Override
            public void onFailure(Throwable throwable) {

            }

            @Override
            public void onSuccess(List<String> names) {
                defaultAttributes.setSocketBindings(names);
            }
        });
    }

    public ClusterConnection getSelectedEntity() {
        SingleSelectionModel<ClusterConnection> selectionModel = (SingleSelectionModel<ClusterConnection>)factoryTable.getSelectionModel();
        return selectionModel.getSelectedObject();
    }


}
