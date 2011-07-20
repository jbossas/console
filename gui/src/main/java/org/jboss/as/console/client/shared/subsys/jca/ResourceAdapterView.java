package org.jboss.as.console.client.shared.subsys.jca;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.RowCountChangeEvent;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.shared.subsys.jca.model.ResourceAdapter;
import org.jboss.ballroom.client.widgets.ContentGroupLabel;
import org.jboss.ballroom.client.widgets.ContentHeaderLabel;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tables.DefaultPager;
import org.jboss.ballroom.client.widgets.tabs.FakeTabPanel;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;

import java.util.List;

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
    private AdapterConfigDetails configPanel;

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
        topLevelTools.addToolButtonRight(new ToolButton("New Resource Adapter", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                presenter.launchNewAdapterWizard();
            }
        }));

        layout.add(topLevelTools);

        // ----

        VerticalPanel vpanel = new VerticalPanel();
        vpanel.setStyleName("rhs-content-panel");

        vpanel.add(new ContentHeaderLabel("Resource Adapter Configurations"));

        ScrollPanel scroll = new ScrollPanel(vpanel);
        layout.add(scroll);

        layout.setWidgetTopHeight(titleBar, 0, Style.Unit.PX, 26, Style.Unit.PX);
        layout.setWidgetTopHeight(topLevelTools, 26, Style.Unit.PX, 30, Style.Unit.PX);
        layout.setWidgetTopHeight(scroll, 56, Style.Unit.PX, 100, Style.Unit.PCT);

        vpanel.add(new ContentGroupLabel("Registered Adapter"));

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

        TextColumn<ResourceAdapter> poolColumn = new TextColumn<ResourceAdapter>() {
            @Override
            public String getValue(ResourceAdapter record) {
                return record.getPoolName();
            }
        };

        table.addColumn(nameColumn, "Name");
        table.addColumn(jndiNameColumn, "JNDI Name");
        table.addColumn(poolColumn, "Pool");


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

        configPanel = new AdapterConfigDetails(presenter);
        bottomPanel.add(configPanel.asWidget(), "Configuration");

        bottomPanel.selectTab(0);

        vpanel.add(bottomPanel);

        return layout;
    }

    @Override
    public void setAdapters(List<ResourceAdapter> adapters) {
        dataProvider.setList(adapters);

        if(!adapters.isEmpty())
            table.getSelectionModel().setSelected(adapters.get(0), true);

    }
}
