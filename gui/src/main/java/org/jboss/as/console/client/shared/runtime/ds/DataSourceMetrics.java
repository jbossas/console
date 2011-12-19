package org.jboss.as.console.client.shared.runtime.ds;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.help.HelpSystem;
import org.jboss.as.console.client.shared.runtime.Metric;
import org.jboss.as.console.client.shared.runtime.RuntimeBaseAddress;
import org.jboss.as.console.client.shared.runtime.Sampler;
import org.jboss.as.console.client.shared.runtime.charts.Column;
import org.jboss.as.console.client.shared.runtime.charts.NumberColumn;
import org.jboss.as.console.client.shared.runtime.plain.PlainColumnView;
import org.jboss.as.console.client.shared.subsys.jca.model.DataSource;
import org.jboss.as.console.client.shared.viewframework.builder.SimpleLayout;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tables.DefaultPager;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;
import org.jboss.dmr.client.ModelDescriptionConstants;
import org.jboss.dmr.client.ModelNode;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 12/10/11
 */
public class DataSourceMetrics {

    
    private DataSourceMetricPresenter presenter;
    private CellTable<DataSource> table;
    private ListDataProvider<DataSource> dataProvider;
    private Sampler poolSampler;

    public DataSourceMetrics(DataSourceMetricPresenter presenter) {
        this.presenter = presenter;
    }

    Widget asWidget() {
        final ToolStrip toolStrip = new ToolStrip();
        toolStrip.addToolButtonRight(new ToolButton(Console.CONSTANTS.common_label_refresh(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.setSelectedDS(getCurrentSelection());
            }
        }));

        // ----

        table = new DefaultCellTable<DataSource>(5);
        table.setSelectionModel(new SingleSelectionModel<DataSource>());

        dataProvider = new ListDataProvider<DataSource>();
        dataProvider.addDataDisplay(table);

        com.google.gwt.user.cellview.client.Column<DataSource, String> nameColumn = new com.google.gwt.user.cellview.client.Column<DataSource, String>(new TextCell()) {
            @Override
            public String getValue(DataSource object) {
                return object.getName();
            }
        };


        com.google.gwt.user.cellview.client.Column<DataSource, String> protocolColumn = new com.google.gwt.user.cellview.client.Column<DataSource, String>(new TextCell()) {
            @Override
            public String getValue(DataSource object) {
                return object.getJndiName();
            }
        };

        table.addColumn(nameColumn, "Name");
        table.addColumn(protocolColumn, "JNDI");

        table.getSelectionModel().addSelectionChangeHandler(new SelectionChangeEvent.Handler(){
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                DataSource ds = getCurrentSelection();
                presenter.setSelectedDS(ds);

            }
        });
        table.getElement().setAttribute("style", "margin-top:15px;margin-bottom:0px;");

        // ----

        String title = "Pool Usage";

         final HelpSystem.AddressCallback addressCallback = new HelpSystem.AddressCallback() {
            @Override
            public ModelNode getAddress() {
                ModelNode address = new ModelNode();
                address.get(ModelDescriptionConstants.ADDRESS).set(RuntimeBaseAddress.get());
                address.get(ModelDescriptionConstants.ADDRESS).add("subsystem", "datasources");
                address.get(ModelDescriptionConstants.ADDRESS).add("data-source", getCurrentSelection().getName());
                address.get(ModelDescriptionConstants.ADDRESS).add("statistics", "pool");

                System.out.println(address);

                return address;
            }
        };



        // ----


        NumberColumn avail = new NumberColumn("AvailableCount", "Available");
        Column[] cols = new Column[] {
                avail.setBaseline(true),
                new NumberColumn("AvailableCount","Active Count").setComparisonColumn(avail),
                new NumberColumn("MaxUsedCount","Max Used").setComparisonColumn(avail)
        };

        poolSampler = new PlainColumnView(title, addressCallback)
                        .setColumns(cols)
                        .setWidth(100, Style.Unit.PCT);


        // ----

        DefaultPager pager = new DefaultPager();
        pager.setDisplay(table);

        VerticalPanel tablePanel = new VerticalPanel();
        tablePanel.setStyleName("fill-layout-width");
        tablePanel.add(table);
        tablePanel.add(pager);


        SimpleLayout layout = new SimpleLayout()
                .setTitle("DataSources")
                .setPlain(true)
                .setTopLevelTools(toolStrip.asWidget())
                .setHeadline("DataSource Metrics")
                .setDescription("Metrics for datasources.")
                .addContent("DS Selection", tablePanel)
                .addContent("Pool Usage", poolSampler.asWidget());

        return layout.build();
    }

    private DataSource getCurrentSelection() {
        return ((SingleSelectionModel<DataSource>) table.getSelectionModel()).getSelectedObject();
    }

    public void clearSamples() {
        poolSampler.clearSamples();

    }

    public void setDataSources(List<DataSource> topics) {
        dataProvider.setList(topics);

        if(!topics.isEmpty())
            table.getSelectionModel().setSelected(topics.get(0), true);
    }

    public void setDSPoolMetric(Metric poolMetric) {
        poolSampler.addSample(poolMetric);
    }
}
