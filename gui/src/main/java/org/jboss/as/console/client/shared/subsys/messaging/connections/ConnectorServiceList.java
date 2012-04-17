package org.jboss.as.console.client.shared.subsys.messaging.connections;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SingleSelectionModel;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.subsys.messaging.forms.ConnectorServiceForm;
import org.jboss.as.console.client.shared.subsys.messaging.model.ConnectorService;
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
public class ConnectorServiceList {

    private ContentHeaderLabel serverName;
    private DefaultCellTable<ConnectorService> table;
    private ListDataProvider<ConnectorService> provider;
    private MsgConnectionsPresenter presenter;
    private ConnectorServiceForm ConnectorServiceForm;

    public ConnectorServiceList(MsgConnectionsPresenter presenter) {
        this.presenter = presenter;
    }

    Widget asWidget() {


        serverName = new ContentHeaderLabel();

        table = new DefaultCellTable<ConnectorService>(10, new ProvidesKey<ConnectorService>() {
            @Override
            public Object getKey(ConnectorService ConnectorService) {
                return ConnectorService.getName();
            }
        });

        provider = new ListDataProvider<ConnectorService>();
        provider.addDataDisplay(table);

        Column<ConnectorService, String> name = new Column<ConnectorService, String>(new TextCell()) {
            @Override
            public String getValue(ConnectorService object) {
                return object.getName();
            }
        };

        table.addColumn(name, "Name");

        ToolStrip tools = new ToolStrip();
        tools.addToolButtonRight(
                new ToolButton(Console.CONSTANTS.common_label_add(), new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent clickEvent) {
                        presenter.launchNewConnectorServiceWizard();
                    }
                }));

        tools.addToolButtonRight(
                new ToolButton(Console.CONSTANTS.common_label_remove(), new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent clickEvent) {

                        Feedback.confirm(
                                Console.MESSAGES.deleteTitle("ConnectorService"),
                                Console.MESSAGES.deleteConfirm("ConnectorService " + getSelectedEntity().getName()),
                                new Feedback.ConfirmationHandler() {
                                    @Override
                                    public void onConfirmation(boolean isConfirmed) {
                                        if (isConfirmed) {
                                            presenter.onDeleteConnectorService(getSelectedEntity());
                                        }
                                    }
                                });

                    }

                }));

        // ----

        ConnectorServiceForm = new ConnectorServiceForm(new FormToolStrip.FormCallback<ConnectorService>()
        {
            @Override
            public void onSave(Map<String, Object> changeset) {
                presenter.onSaveConnectorService(getSelectedEntity(), changeset);
            }

            @Override
            public void onDelete(ConnectorService entity) {

            }
        });

        // ----
        MultipleToOneLayout layout = new MultipleToOneLayout()
                .setPlain(true)
                .setHeadlineWidget(serverName)
                .setDescription("Class name of the factory class that can instantiate the connector service.")
                .setMaster(Console.MESSAGES.available("Services"), table)
                .setMasterTools(tools)
                .setDetail("Detail", ConnectorServiceForm.asWidget());

        ConnectorServiceForm.getForm().bind(table);

        return layout.build();
    }

    public void setConnectorServices(List<ConnectorService> ConnectorServices) {
        provider.setList(ConnectorServices);
        serverName.setText("ConnectorServices: Provider "+presenter.getCurrentServer());

        table.selectDefaultEntity();

    }

    public ConnectorService getSelectedEntity() {
        SingleSelectionModel<ConnectorService> selectionModel = (SingleSelectionModel<ConnectorService>) table.getSelectionModel();
        return selectionModel.getSelectedObject();
    }


}
