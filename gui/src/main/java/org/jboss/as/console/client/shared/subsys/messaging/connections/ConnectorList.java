package org.jboss.as.console.client.shared.subsys.messaging.connections;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.properties.PropertyEditor;
import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.jboss.as.console.client.shared.subsys.messaging.forms.ConnectorForm;
import org.jboss.as.console.client.shared.subsys.messaging.model.Connector;
import org.jboss.as.console.client.shared.subsys.messaging.model.ConnectorType;
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
public class ConnectorList {

    private ContentHeaderLabel serverName;
    private DefaultCellTable<Connector> table;
    private ListDataProvider<Connector> provider;
    private MsgConnectionsPresenter presenter;
    private ConnectorForm ConnectorForm;
    private ConnectorType type;
    private PropertyEditor properties;

    public ConnectorList(MsgConnectionsPresenter presenter,  ConnectorType type) {
        this.presenter = presenter;
        this.type = type;
    }

    Widget asWidget() {


        serverName = new ContentHeaderLabel();

        table = new DefaultCellTable<Connector>(10, new ProvidesKey<Connector>() {
            @Override
            public Object getKey(Connector Connector) {
                return Connector.getName();
            }
        });

        provider = new ListDataProvider<Connector>();
        provider.addDataDisplay(table);

        Column<Connector, String> name = new Column<Connector, String>(new TextCell()) {
            @Override
            public String getValue(Connector object) {
                return object.getName();
            }
        };

        table.addColumn(name, "Name");

        ToolStrip tools = new ToolStrip();
        tools.addToolButtonRight(
                new ToolButton(Console.CONSTANTS.common_label_add(), new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent clickEvent) {
                        presenter.launchNewConnectorWizard(type);
                    }
                }));

        tools.addToolButtonRight(
                new ToolButton(Console.CONSTANTS.common_label_remove(), new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent clickEvent) {

                        Feedback.confirm(
                                Console.MESSAGES.deleteTitle("Connector"),
                                Console.MESSAGES.deleteConfirm("Connector " + getSelectedEntity().getSocketBinding()),
                                new Feedback.ConfirmationHandler() {
                                    @Override
                                    public void onConfirmation(boolean isConfirmed) {
                                        if (isConfirmed) {
                                            presenter.onDeleteConnector(getSelectedEntity());
                                        }
                                    }
                                });

                    }

                }));

        // ----

        ConnectorForm = new ConnectorForm(new FormToolStrip.FormCallback<Connector>()
        {
            @Override
            public void onSave(Map<String, Object> changeset) {
                presenter.onSaveConnector(getSelectedEntity(), changeset);
            }

            @Override
            public void onDelete(Connector entity) {

            }
        }, type);

        // ----
        properties = new PropertyEditor(presenter, true);

        VerticalPanel layout = new VerticalPanel();

        layout.add(tools);
        layout.add(table);

        ConnectorForm.getForm().bind(table);


        TabPanel tabs = new TabPanel();
        tabs.setStyleName("default-tabpanel");
        tabs.getElement().setAttribute("style", "margin-top:15px;");

        tabs.add(ConnectorForm.asWidget(), "Details");
        tabs.add(properties.asWidget(), "Properties");

        layout.add(tabs);
        tabs.selectTab(0);


        // ----

        table.getSelectionModel().addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent selectionChangeEvent) {
                List<PropertyRecord> props = getSelectedEntity().getParameter();

                String tokens = getSelectedEntity().getType().getResource() + "_#_" + getSelectedEntity().getName();
                properties.setProperties(tokens, props);
            }
        });

        return layout;
    }

    public void setConnectors(List<Connector> Connectors) {
        properties.clearValues();
        provider.setList(Connectors);
        serverName.setText("Connectors: Provider "+presenter.getCurrentServer());

        table.selectDefaultEntity();


        // populate oracle
        presenter.loadSocketBindings(
                new AsyncCallback<List<String>>() {
                    @Override
                    public void onFailure(Throwable throwable) {

                    }

                    @Override
                    public void onSuccess(List<String> names) {
                        ConnectorForm.setSocketBindings(names);
                    }
                });
    }

    public Connector getSelectedEntity() {
        SingleSelectionModel<Connector> selectionModel = (SingleSelectionModel<Connector>) table.getSelectionModel();
        return selectionModel.getSelectedObject();
    }


}
