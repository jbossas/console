package org.jboss.as.console.client.shared.runtime.ds;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.shared.runtime.Metric;
import org.jboss.as.console.client.shared.subsys.jca.model.DataSource;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 12/19/11
 */
public class DataSourceMetricView extends SuspendableViewImpl implements DataSourceMetricPresenter.MyView {
    private DataSourceMetricPresenter presenter;
    private DataSourceMetrics dsMetrics;
    private DataSourceMetrics xaMetrics;

    @Override
    public Widget createWidget() {

        this.dsMetrics = new DataSourceMetrics(presenter, false);
        this.xaMetrics = new DataSourceMetrics(presenter, true);

        TabLayoutPanel tabLayoutpanel = new TabLayoutPanel(40, Style.Unit.PX);
        tabLayoutpanel.addStyleName("default-tabpanel");

        tabLayoutpanel.add(dsMetrics.asWidget(), "Data Sources", true);
        tabLayoutpanel.add(xaMetrics.asWidget(), "XA Data Sources", true);

        tabLayoutpanel.selectTab(0);

        return tabLayoutpanel;
    }

    @Override
    public void setPresenter(DataSourceMetricPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void clearSamples() {
        dsMetrics.clearSamples();
        xaMetrics.clearSamples();
    }

    @Override
    public void setDatasources(List<DataSource> datasources, boolean isXA) {
        if(isXA)
            xaMetrics.setDataSources(datasources);
        else
            dsMetrics.setDataSources(datasources);
    }

    @Override
    public void setDSPoolMetric(Metric poolMetric, boolean isXA) {
        if(isXA)
            xaMetrics.setDSPoolMetric(poolMetric);
        else
            dsMetrics.setDSPoolMetric(poolMetric);
    }

    @Override
    public void setDSCacheMetric(Metric metric, boolean isXA) {
        if(isXA)
            xaMetrics.setDSCacheMetric(metric);
        else
            dsMetrics.setDSCacheMetric(metric);
    }
}
