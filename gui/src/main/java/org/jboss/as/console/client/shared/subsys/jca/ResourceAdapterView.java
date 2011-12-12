package org.jboss.as.console.client.shared.subsys.jca;

import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.shared.subsys.jca.model.DataSource;
import org.jboss.as.console.client.shared.subsys.jca.model.PoolConfig;
import org.jboss.as.console.client.shared.subsys.jca.model.ResourceAdapter;
import org.jboss.ballroom.client.widgets.ContentGroupLabel;
import org.jboss.ballroom.client.widgets.ContentHeaderLabel;
import org.jboss.ballroom.client.widgets.icons.Icons;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tables.DefaultPager;
import org.jboss.ballroom.client.widgets.tabs.FakeTabPanel;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;
import org.jboss.ballroom.client.widgets.window.Feedback;

import java.util.List;
import java.util.Map;

/**
 * @author Heiko Braun
 * @date 7/19/11
 */
public class ResourceAdapterView extends SuspendableViewImpl implements ResourceAdapterPresenter.MyView {

    private ResourceAdapterPresenter presenter;
    private static final int PAGE_SIZE = 5;
    private CellTable<ResourceAdapter> table;
    private ListDataProvider<ResourceAdapter> dataProvider;
    private AdapterDetails detailsPanel;
    private AdapterConfigProperties configPanel;
    private PoolConfigurationView poolConfig;

    @Override
    public void setPresenter(ResourceAdapterPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget createWidget() {

        LayoutPanel layout = new LayoutPanel();

        FakeTabPanel titleBar = new FakeTabPanel("Resource Adapter");
        layout.add(titleBar);

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

                final ResourceAdapter ra = detailsPanel.getCurrentSelection();

                Feedback.confirm(
                        Console.MESSAGES.deleteTitle("resource adapter"),
                        Console.MESSAGES.deleteConfirm("resource adapter "+ra.getName()),
                        new Feedback.ConfirmationHandler() {
                            @Override
                            public void onConfirmation(boolean isConfirmed) {
                                if (isConfirmed) {
                                    presenter.onDelete(ra);
                                }
                            }
                        });
            }
        };
        ToolButton deleteBtn = new ToolButton(Console.CONSTANTS.common_label_delete());
        deleteBtn.addClickHandler(clickHandler);
        topLevelTools.addToolButtonRight(deleteBtn);

        layout.add(topLevelTools);

        // ----

        VerticalPanel vpanel = new VerticalPanel();
        vpanel.setStyleName("rhs-content-panel");

        vpanel.add(new ContentHeaderLabel(Console.CONSTANTS.subsys_jca_ra_configurations()));

        ScrollPanel scroll = new ScrollPanel(vpanel);
        layout.add(scroll);

        layout.setWidgetTopHeight(titleBar, 0, Style.Unit.PX, 26, Style.Unit.PX);
        layout.setWidgetTopHeight(topLevelTools, 26, Style.Unit.PX, 30, Style.Unit.PX);
        layout.setWidgetTopHeight(scroll, 56, Style.Unit.PX, 100, Style.Unit.PCT);

        vpanel.add(new ContentGroupLabel(Console.CONSTANTS.subsys_jca_ra_registered()));

        // -------

        table = new DefaultCellTable<ResourceAdapter>(PAGE_SIZE);
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


        vpanel.add(table);

        // -------

        DefaultPager pager = new DefaultPager();
        pager.setDisplay(table);
        vpanel.add(pager);


        // -------
        vpanel.add(new ContentGroupLabel("Resource Adapter"));

        TabPanel bottomPanel = new TabPanel();
        bottomPanel.setStyleName("default-tabpanel");

        detailsPanel = new AdapterDetails(presenter);
        detailsPanel.getForm().bind(table);
        bottomPanel.add(detailsPanel.asWidget(), "Attributes");

        configPanel = new AdapterConfigProperties(presenter);
        bottomPanel.add(configPanel.asWidget(), "Properties");

        final SingleSelectionModel<ResourceAdapter> selectionModel =
                (SingleSelectionModel<ResourceAdapter>)table.getSelectionModel();
        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {

            public void onSelectionChange(SelectionChangeEvent event) {
                ResourceAdapter selectedRa = selectionModel.getSelectedObject();
                configPanel.setAdapter(selectedRa);
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
                ResourceAdapter selectedObject = ((SingleSelectionModel<ResourceAdapter >) table.getSelectionModel()).getSelectedObject();
                presenter.loadPoolConfig(selectedObject);
            }
        });


        bottomPanel.add(poolConfig.asWidget(), "Pool");

        bottomPanel.selectTab(0);
        vpanel.add(bottomPanel);


        return layout;
    }

    private ResourceAdapter getCurrentSelection() {
        ResourceAdapter selection = ((SingleSelectionModel<ResourceAdapter>) table.getSelectionModel()).getSelectedObject();
        return selection;
    }

    @Override
    public void setAdapters(List<ResourceAdapter> adapters) {
        dataProvider.setList(adapters);

        if(!adapters.isEmpty())
            table.getSelectionModel().setSelected(adapters.get(0), true);

    }

    @Override
    public void setEnabled(boolean b) {
        detailsPanel.setEnabled(b);
        configPanel.setEnabled(b);
    }

    @Override
    public void setPoolConfig(String parent, PoolConfig poolConfig) {
        this.poolConfig.updateFrom(parent, poolConfig);
    }
}
