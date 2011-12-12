package org.jboss.as.console.client.shared.subsys.jca;

import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.subsys.jca.model.PoolConfig;
import org.jboss.as.console.client.shared.subsys.jca.model.ResourceAdapter;
import org.jboss.as.console.client.shared.viewframework.builder.MultipleToOneLayout;
import org.jboss.ballroom.client.widgets.icons.Icons;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tables.DefaultPager;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;

import java.util.Map;

/**
 * @author Heiko Braun
 * @date 12/12/11
 */
public class ConnectionList {

    private ResourceAdapterPresenter presenter;
    private ResourceAdapter currentAdapter;
    private DefaultCellTable<ResourceAdapter> table;
    private ListDataProvider<ResourceAdapter> dataProvider;
    private PoolConfigurationView poolConfig;
    private AdapterConnectionDetails connectionDetails;
    private AdapterConnectionProperties connectionProperties;

    public ConnectionList(ResourceAdapterPresenter presenter) {
        this.presenter = presenter;
    }

    Widget asWidget() {

        ToolStrip topLevelTools = new ToolStrip();
        topLevelTools.addToolButtonRight(new ToolButton(Console.CONSTANTS.common_label_add(), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                presenter.launchNewAdapterWizard();
            }
        }));

        ClickHandler clickHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {



                /*Feedback.confirm(
                        Console.MESSAGES.deleteTitle("resource adapter"),
                        Console.MESSAGES.deleteConfirm("resource adapter "+ra.getName()),
                        new Feedback.ConfirmationHandler() {
                            @Override
                            public void onConfirmation(boolean isConfirmed) {
                                if (isConfirmed) {
                                    presenter.onDelete(ra);
                                }
                            }
                        }); */
            }
        };
        ToolButton deleteBtn = new ToolButton(Console.CONSTANTS.common_label_delete());
        deleteBtn.addClickHandler(clickHandler);
        topLevelTools.addToolButtonRight(deleteBtn);


        // ----

      /*  VerticalPanel vpanel = new VerticalPanel();
        vpanel.setStyleName("rhs-content-panel");

        vpanel.add(new ContentHeaderLabel(Console.CONSTANTS.subsys_jca_ra_configurations()));

        ScrollPanel scroll = new ScrollPanel(vpanel);
        layout.add(scroll);

        layout.setWidgetTopHeight(titleBar, 0, Style.Unit.PX, 26, Style.Unit.PX);
        layout.setWidgetTopHeight(topLevelTools, 26, Style.Unit.PX, 30, Style.Unit.PX);
        layout.setWidgetTopHeight(scroll, 56, Style.Unit.PX, 100, Style.Unit.PCT);

        vpanel.add(new ContentGroupLabel(Console.CONSTANTS.subsys_jca_ra_registered()));   */

        // -------

        table = new DefaultCellTable<ResourceAdapter>(10);
        dataProvider = new ListDataProvider<ResourceAdapter>();
        dataProvider.addDataDisplay(table);

        TextColumn<ResourceAdapter> nameColumn = new TextColumn<ResourceAdapter>() {
            @Override
            public String getValue(ResourceAdapter record) {
                return record.getName();
            }
        };

        TextColumn<ResourceAdapter> jndiNameColumn = new TextColumn<ResourceAdapter>() {
            @Override
            public String getValue(ResourceAdapter record) {
                return record.getJndiName();
            }
        };

        Column<ResourceAdapter, ImageResource> statusColumn =
                       new Column<ResourceAdapter, ImageResource>(new ImageResourceCell()) {
                           @Override
                           public ImageResource getValue(ResourceAdapter ra) {

                               ImageResource res = null;

                               if(ra.isEnabled())
                                   res = Icons.INSTANCE.statusGreen_small();
                               else
                                   res = Icons.INSTANCE.statusRed_small();

                               return res;
                           }
                       };


        table.addColumn(nameColumn, "Name");
        table.addColumn(jndiNameColumn, "JNDI Name");
        table.addColumn(statusColumn, "Enabled?");




        // -------

        DefaultPager pager = new DefaultPager();
        pager.setDisplay(table);



        // -------


        connectionDetails = new AdapterConnectionDetails(presenter);
        connectionDetails.getForm().bind(table);
        //bottomPanel.add(connectionDetails.asWidget(), "Attributes");

        // ---

        AdapterSecurity securityConfig = new AdapterSecurity(presenter);
        //bottomPanel.add(securityConfig.asWidget(), "Security");

        // ---

        AdapterValidation  validationConfig = new AdapterValidation(presenter);
        //bottomPanel.add(validationConfig.asWidget(), "Validation");


        // --

        connectionProperties = new AdapterConnectionProperties(presenter);
        //bottomPanel.add(connectionProperties.asWidget(), "Properties");

        // ---

        final SingleSelectionModel<ResourceAdapter> selectionModel =
                (SingleSelectionModel<ResourceAdapter>)table.getSelectionModel();
        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {

            public void onSelectionChange(SelectionChangeEvent event) {
                ResourceAdapter selectedRa = selectionModel.getSelectedObject();

            }
        });

        poolConfig = new PoolConfigurationView(new PoolManagement() {
            @Override
            public void onSavePoolConfig(String parentName, Map<String, Object> changeset) {
                presenter.onSavePoolConfig(getCurrentSelection(), changeset);
            }

            @Override
            public void onResetPoolConfig(String parentName, PoolConfig entity) {
                presenter.onDeletePoolConfig(getCurrentSelection(), entity);
            }
        });


        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler () {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                ResourceAdapter selectedObject = getCurrentSelection();
                presenter.loadPoolConfig(selectedObject);
            }
        });


        //bottomPanel.add(poolConfig.asWidget(), "Pool");

        MultipleToOneLayout layout = new MultipleToOneLayout()
                .setPlain(true)
                .setTitle(Console.CONSTANTS.subsys_jca_ra_configurations())
                .setMaster(Console.CONSTANTS.subsys_jca_ra_registered(), table)
                .setMasterTools(topLevelTools.asWidget())
                .addDetail("Attributes", connectionDetails.asWidget())
                .addDetail("Properties", connectionProperties.asWidget())
                .addDetail("Security", securityConfig.asWidget())
                .addDetail("Validation", validationConfig.asWidget());


        return layout.build();
    }

    private ResourceAdapter getCurrentSelection() {
        return ((SingleSelectionModel<ResourceAdapter >) table.getSelectionModel()).getSelectedObject();
    }

    public void setAdapter(ResourceAdapter adapter) {
        this.currentAdapter = adapter;
    }
}
