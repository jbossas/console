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
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.properties.NewPropertyWizard;
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
import org.jboss.ballroom.client.widgets.window.DefaultWindow;
import org.jboss.ballroom.client.widgets.window.Feedback;

import java.util.List;
import java.util.Map;

/**
 * @author Heiko Braun
 * @date 12/12/11
 */
public class ConnectionList implements PropertyManagement, PoolManagement {

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
    private DefaultWindow window;
    private ToolButton disableBtn;


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

                if(selection!=null)
                {
                    Feedback.confirm(
                            Console.MESSAGES.deleteTitle("Connection Definition"),
                            Console.MESSAGES.deleteConfirm("Connection Definition"+selection.getJndiName()),
                            new Feedback.ConfirmationHandler() {
                                @Override
                                public void onConfirmation(boolean isConfirmed) {
                                    if (isConfirmed) {
                                        presenter.onDeleteConnection(selection);
                                    }
                                }
                            });
                }
            }
        };
        ToolButton deleteBtn = new ToolButton(Console.CONSTANTS.common_label_delete());
        deleteBtn.addClickHandler(clickHandler);
        topLevelTools.addToolButtonRight(deleteBtn);


        disableBtn = new ToolButton(Console.CONSTANTS.common_label_disable(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

                final ConnectionDefinition selection = getCurrentSelection();
                if(selection!=null)
                {

                    selection.setEnabled(!selection.isEnabled());

                    Feedback.confirm(
                            Console.MESSAGES.modify("Connection Definition"),
                            Console.MESSAGES.modifyConfirm("Connection Definition " + selection.getJndiName()),
                            new Feedback.ConfirmationHandler() {
                                @Override
                                public void onConfirmation(boolean isConfirmed) {
                                    if (isConfirmed) {
                                        presenter.enOrDisbaleConnection(currentAdapter, selection);
                                    }
                                }
                            });
                }

            }
        });
        topLevelTools.addToolButtonRight(disableBtn);

        // -------

        table = new DefaultCellTable<ConnectionDefinition>(10,
                new ProvidesKey<ConnectionDefinition>() {
                    @Override
                    public Object getKey(ConnectionDefinition item) {
                        return item.getJndiName();
                    }
                });

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
                            res = Icons.INSTANCE.status_good();
                        else
                            res = Icons.INSTANCE.status_bad();

                        return res;
                    }
                };


        table.addColumn(nameColumn, "JNDI Name");
        table.addColumn(statusColumn, "Enabled?");


        table.setSelectionModel(new SingleSelectionModel<ConnectionDefinition>());


        // -------


        connectionDetails = new AdapterConnectionDetails(presenter);

        // ---

        securityConfig = new AdapterSecurity(presenter);

        // ---

        validationConfig = new AdapterValidation(presenter);

        // ---

        connectionProperties = new AdapterConnectionProperties(presenter, this);

        // ---

        final SingleSelectionModel<ConnectionDefinition> selectionModel = (SingleSelectionModel<ConnectionDefinition>)table.getSelectionModel();

        poolConfig = new PoolConfigurationView(this);

        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler () {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                ConnectionDefinition selectedObject = getCurrentSelection();

                connectionProperties.updateFrom(selectedObject.getProperties());
                poolConfig.updateFrom(selectedObject.getJndiName(), selectedObject.getPoolConfig());

                String nextState = selectedObject.isEnabled() ?
                        Console.CONSTANTS.common_label_disable():Console.CONSTANTS.common_label_enable();
                disableBtn.setText(nextState);

            }
        });


        // ----

        headline = new HTML("HEADLINE");
        headline.setStyleName("content-header-label");

        MultipleToOneLayout layout = new MultipleToOneLayout()
                .setPlain(true)
                .setHeadlineWidget(headline)
                .setTitle("TITLE")
                .setDescription(Console.CONSTANTS.subsys_jca_ra_connection_desc())
                .setMaster(Console.MESSAGES.available("Connection Definitions"), table)
                .setMasterTools(topLevelTools.asWidget())
                .addDetail("Attributes", connectionDetails.asWidget())
                .addDetail("Properties", connectionProperties.asWidget())
                .addDetail("Pool", poolConfig.asWidget())
                .addDetail("Security", securityConfig.asWidget())
                .addDetail("Validation", validationConfig.asWidget());



        connectionDetails.getForm().bind(table);
        securityConfig.getForm().bind(table);
        poolConfig.getForm().bind(table);
        validationConfig.getForm().bind(table);

        return layout.build();
    }

    private ConnectionDefinition getCurrentSelection() {
        return ((SingleSelectionModel<ConnectionDefinition >) table.getSelectionModel()).getSelectedObject();
    }

    public void setAdapter(ResourceAdapter adapter) {
        this.currentAdapter = adapter;

        headline.setText("Resource Adapter: "+adapter.getArchive());

        // some subviews require manual cleanup
        connectionProperties.clearProperties();

        List<ConnectionDefinition> connections = adapter.getConnectionDefinitions();
        dataProvider.setList(connections);

        table.selectDefaultEntity();
    }

    @Override
    public void onCreateProperty(String reference, PropertyRecord prop) {
        closePropertyDialoge();
        presenter.onCreateConnectionProperty(getCurrentSelection(), prop);
    }

    @Override
    public void onDeleteProperty(String reference, PropertyRecord prop) {
        presenter.onDeleteConnectionProperty(getCurrentSelection(), prop);
    }

    @Override
    public void onChangeProperty(String reference, PropertyRecord prop) {
        // not possible
    }

    @Override
    public void launchNewPropertyDialoge(String reference) {
        window = new DefaultWindow(Console.MESSAGES.createTitle("Configuration Property"));
        window.setWidth(480);
        window.setHeight(360);

        window.trapWidget(
                new NewPropertyWizard(this, "").asWidget()
        );

        window.setGlassEnabled(true);
        window.center();
    }

    @Override
    public void closePropertyDialoge() {
        window.hide();
    }

    @Override
    public void onSavePoolConfig(String parentName, Map<String, Object> changeset) {
        presenter.onSavePoolConfig(getCurrentSelection(), changeset);
    }

    @Override
    public void onResetPoolConfig(String parentName, PoolConfig entity) {
        presenter.onDeletePoolConfig(getCurrentSelection(), entity);
    }

    @Override
    public void onDoFlush(String editedName) {
        presenter.onDoFlush(getCurrentSelection());
    }
}
