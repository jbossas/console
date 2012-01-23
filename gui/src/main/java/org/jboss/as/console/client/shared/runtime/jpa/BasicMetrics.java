package org.jboss.as.console.client.shared.runtime.jpa;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.help.HelpSystem;
import org.jboss.as.console.client.shared.runtime.RuntimeBaseAddress;
import org.jboss.as.console.client.shared.runtime.charts.Column;
import org.jboss.as.console.client.shared.runtime.charts.NumberColumn;
import org.jboss.as.console.client.shared.runtime.charts.TextColumn;
import org.jboss.as.console.client.shared.runtime.jpa.model.JPADeployment;
import org.jboss.as.console.client.shared.runtime.plain.PlainColumnView;
import org.jboss.as.console.client.shared.viewframework.builder.OneToOneLayout;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;
import org.jboss.dmr.client.ModelDescriptionConstants;
import org.jboss.dmr.client.ModelNode;

/**
 * @author Heiko Braun
 * @date 1/19/12
 */
public class BasicMetrics {

    private JPAMetricPresenter presenter;
    private JPADeployment currentUnit;

    private HTML title;

    private PlainColumnView queryCacheSampler;
    private PlainColumnView txSampler;
    private PlainColumnView queryExecSampler;
    private PlainColumnView secondLevelSampler;
    private PlainColumnView connectionSampler;

    private String[] tokens;
    private HTML slowQuery;

    public BasicMetrics(JPAMetricPresenter presenter) {
        this.presenter = presenter;
    }

    Widget asWidget() {

        final ToolStrip toolStrip = new ToolStrip();
        toolStrip.addToolButtonRight(new ToolButton(Console.CONSTANTS.common_label_refresh(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.loadMetrics(tokens);
            }
        }));

        //  ------

        NumberColumn txCount = new NumberColumn("completed-transaction-count","Completed");

        Column[] cols = new Column[] {
                txCount.setBaseline(true),
                new NumberColumn("successful-transaction-count","Successful").setComparisonColumn(txCount)

        };


        final HelpSystem.AddressCallback addressCallback = new HelpSystem.AddressCallback() {
            @Override
            public ModelNode getAddress() {
                ModelNode address = new ModelNode();
                address.get(ModelDescriptionConstants.ADDRESS).set(RuntimeBaseAddress.get());
                address.get(ModelDescriptionConstants.ADDRESS).add("deployment", "*");
                address.get(ModelDescriptionConstants.ADDRESS).add("subsystem", "jpa");
                address.get(ModelDescriptionConstants.ADDRESS).add("hibernate-persistence-unit", "*");
                return address;
            }
        };

        txSampler = new PlainColumnView("Transactions", addressCallback)
                .setColumns(cols)
                .setWidth(100, Style.Unit.PCT);


        //  ------

        NumberColumn queryCount = new NumberColumn("query-cache-put-count","Query Put Count");

        Column[] queryCols = new Column[] {
                queryCount.setBaseline(true),
                new NumberColumn("query-cache-hit-count","Query Hit Count").setComparisonColumn(queryCount),
                new NumberColumn("query-cache-miss-count","Query Miss Count").setComparisonColumn(queryCount)

        };

        queryCacheSampler = new PlainColumnView("Query Cache", addressCallback)
                .setColumns(queryCols)
                .setWidth(100, Style.Unit.PCT);


        //  ------

        NumberColumn queryExecCount = new NumberColumn("query-execution-count","Query Execution Count");

        Column[] queryExecCols = new Column[] {
                queryExecCount,
                new NumberColumn("query-execution-max-time","Exec Max Time")
        };

        queryExecSampler  = new PlainColumnView("Query Execution", addressCallback)
                .setColumns(queryExecCols)
                .setWidth(100, Style.Unit.PCT);


        //  ------

        NumberColumn secondLevelCount = new NumberColumn("second-level-cache-put-count","Put Count");

        Column[] secondLevelCols = new Column[] {
                secondLevelCount.setBaseline(true),
                new NumberColumn("second-level-cache-hit-count","Hit Count").setComparisonColumn(secondLevelCount),
                new TextColumn("second-level-cache-miss-count","Miss Count").setComparisonColumn(secondLevelCount)

        };

        secondLevelSampler  = new PlainColumnView("Second Level Cache", addressCallback)
                .setColumns(secondLevelCols)
                .setWidth(100, Style.Unit.PCT);



        //  ------


        NumberColumn sessionOpenCount = new NumberColumn("session-open-count", "Session Open Count");
        Column[] connectionCols = new Column[] {
                sessionOpenCount.setBaseline(true),
                new TextColumn("session-close-count","Sesison Close Count").setComparisonColumn(sessionOpenCount),
                new NumberColumn("connect-count","Connection Count")

        };

        connectionSampler  = new PlainColumnView("Connections", addressCallback)
                .setColumns(connectionCols)
                .setWidth(100, Style.Unit.PCT);

        // ----

        title = new HTML();
        title.setStyleName("content-header-label");


        // -------

        VerticalPanel connectionPanel = new VerticalPanel();
        connectionPanel.setStyleName("fill-layout-width");
        connectionPanel.add(connectionSampler.asWidget());

        VerticalPanel txPanel = new VerticalPanel();
        txPanel.setStyleName("fill-layout-width");
        txPanel.add(txSampler.asWidget());

        VerticalPanel queryPanel = new VerticalPanel();
        queryPanel.setStyleName("fill-layout-width");
        queryPanel.add(queryCacheSampler.asWidget());
        queryPanel.add(queryExecSampler.asWidget());

        slowQuery = new HTML();
        slowQuery.setStyleName("help-panel-open");
        slowQuery.getElement().setAttribute("style", "padding:5px");
        queryPanel.add(slowQuery);

        VerticalPanel secondPanel = new VerticalPanel();
        secondPanel.setStyleName("fill-layout-width");
        secondPanel.add(secondLevelSampler.asWidget());


        OneToOneLayout layout = new OneToOneLayout()
                .setPlain(true)
                .setTopLevelTools(toolStrip.asWidget())
                .setHeadlineWidget(title)
                .setDescription(Console.CONSTANTS.subsys_jpa_basicMetric_desc())
                .addDetail("Connections", connectionPanel)
                .addDetail("Transactions", txPanel)
                .addDetail("Queries", queryPanel)
                .addDetail("Second Level Cache", secondPanel);


        return layout.build();
    }

    public void setUnit(JPADeployment unit) {
        this.currentUnit = unit;
    }

    public void setContextName(String[] tokens) {
        this.tokens = tokens;
        title.setText("Persistence Unit Metrics: "+tokens[0]+"#"+tokens[1]);
    }


    public void updateMetric(UnitMetric unitMetric) {
        txSampler.addSample(unitMetric.getTxMetric());
        queryCacheSampler.addSample(unitMetric.getQueryMetric());
        queryExecSampler.addSample(unitMetric.getQueryExecMetric());

        slowQuery.setHTML("<b>Max Time Query</b>: "+ unitMetric.getQueryExecMetric().get(2));

        secondLevelSampler.addSample(unitMetric.getSecondLevelCacheMetric());
        connectionSampler.addSample(unitMetric.getConnectionMetric());
    }
}
