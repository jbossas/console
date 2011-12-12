package org.jboss.as.console.client.shared.subsys.jca;

import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.properties.PropertyManagement;
import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.jboss.as.console.client.shared.subsys.jca.model.ConnectionDefinition;
import org.jboss.as.console.client.shared.subsys.jca.model.PoolConfig;
import org.jboss.as.console.client.shared.subsys.jca.model.ResourceAdapter;
import org.jboss.as.console.client.shared.viewframework.builder.MultipleToOneLayout;
import org.jboss.ballroom.client.widgets.icons.Icons;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;
import org.jboss.ballroom.client.widgets.window.Feedback;

import java.util.List;
import java.util.Map;

/**
 * @author Heiko Braun
 * @date 12/12/11
 */
public class ConnectionList implements PropertyManagement {

    private ResourceAdapterPresenter presenter;
    private ResourceAdapter currentAdapter;
    private DefaultCellTable<ConnectionDefinition> table;
    private ListDataProvider<ConnectionDefinition> dataProvider;
    private PoolConfigurationView poolConfig;
    private AdapterConnectionDetails connectionDetails;
    private AdapterConnectionProperties connectionProperties;
    private AdapterSecurity securityConfig;
    private AdapterValidation validationConfig;
    private HTML headline;

    public ConnectionList(ResourceAdapterPresenter presenter) {
        this.presenter = presenter;
    }

    Widget asWidget() {

        ToolStrip topLevelTools = new ToolStrip();
        topLevelTools.addToolButtonRight(new ToolButton(Console.CONSTANTS.common_label_add(), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                presenter.launchNewConnectionWizard();
            }
        }));

        ClickHandler clickHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

                final ConnectionDefinition selection = getCurrentSelection();

                Feedback.confirm(
                        Console.MESSAGES.deleteTitle("connection definition"),
                        Console.MESSAGES.deleteConfirm("connection definition"+selection.getJndiName()),
                        new Feedback.ConfirmationHandler() {
                            @Override
                            public void onConfirmation(boolean isConfirmed) {
                                if (isConfirmed) {
                                    presenter.onDeleteConnection(selection);
                                }
                            }
                        });
            }
        };
        ToolButton deleteBtn = new ToolButton(Console.CONSTANTS.common_label_delete());
        deleteBtn.addClickHandler(clickHandler);
        topLevelTools.addToolButtonRight(deleteBtn);

        // -------

        table = new DefaultCellTable<ConnectionDefinition>(10);
        dataProvider = new ListDataProvider<ConnectionDefinition>();
        dataProvider.addDataDisplay(table);

        TextColumn<ConnectionDefinition> nameColumn = new TextColumn<ConnectionDefinition>() {
            @Override
            public String getValue(ConnectionDefinition record) {
                return record.getJndiName();
            }
        };

        Column<ConnectionDefinition, ImageResource> statusColumn =
                       new Column<ConnectionDefinition, ImageResource>(new ImageResourceCell()) {
                           @Override
                           public ImageResource getValue(ConnectionDefinition ra) {

                               ImageResource res = null;

                               if(ra.isEnabled())
                                   res = Icons.INSTANCE.statusGreen_small();
                               else
                                   res = Icons.INSTANCE.statusRed_small();

                               return res;
                           }
                       };


        table.addColumn(nameColumn, "JNDI Name");
        table.addColumn(statusColumn, "Enabled?");


        // -------


        connectionDetails = new AdapterConnectionDetails(presenter);
        connectionDetails.getForm().bind(table);

        // ---

        securityConfig = new AdapterSecurity(presenter);

        // ---

        validationConfig = new AdapterValidation(presenter);

        // ---

        connectionProperties = new AdapterConnectionProperties(presenter, this);

        // ---

        final SingleSelectionModel<ConnectionDefinition> selectionModel = (SingleSelectionModel<ConnectionDefinition>)table.getSelectionModel();

        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {

            public void onSelectionChange(SelectionChangeEvent event) {
                ConnectionDefinition selection = selectionModel.getSelectedObject();
                connectionProperties.updateFrom(selection.getProperties());
            }
        });

        poolConfig = new PoolConfigurationView(new PoolManagement() {
            @Override
            public void onSavePoolConfig(String parentName, Map<String, Object> changeset) {
                //presenter.onSavePoolConfig(getCurrentSelection(), changeset);
            }

            @Override
            public void onResetPoolConfig(String parentName, PoolConfig entity) {
                //presenter.onDeletePoolConfig(getCurrentSelection(), entity);
            }
        });


        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler () {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                ConnectionDefinition selectedObject = getCurrentSelection();
                //presenter.loadPoolConfig(selectedObject);
            }
        });

        // ----


        headline = new HTML("HEADLINE");
        headline.setStyleName("content-header-label");

        MultipleToOneLayout layout = new MultipleToOneLayout()
                .setPlain(true)
                .setHeadlineWidget(headline)
                .setTitle("TITLE")
                .setMaster("Registered Connection Definitions", table)
                .setMasterTools(topLevelTools.asWidget())
                .addDetail("Attributes", connectionDetails.asWidget())
                .addDetail("Properties", connectionProperties.asWidget())
                .addDetail("Security", securityConfig.asWidget())
                .addDetail("Validation", validationConfig.asWidget());


        return layout.build();
    }

    private ConnectionDefinition getCurrentSelection() {
        return ((SingleSelectionModel<ConnectionDefinition >) table.getSelectionModel()).getSelectedObject();
    }

    public void setAdapter(ResourceAdapter adapter) {
        this.currentAdapter = adapter;

        headline.setText("Resource Adapter: "+adapter.getArchive());

        List<ConnectionDefinition> connections = adapter.getConnectionDefinitions();
        dataProvider.setList(connections);

        if(!connections.isEmpty())
            table.getSelectionModel().setSelected(connections.get(0), true);
    }

    @Override
    public void onCreateProperty(String reference, PropertyRecord prop) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onDeleteProperty(String reference, PropertyRecord prop) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onChangeProperty(String reference, PropertyRecord prop) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void launchNewPropertyDialoge(String reference) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void closePropertyDialoge() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
