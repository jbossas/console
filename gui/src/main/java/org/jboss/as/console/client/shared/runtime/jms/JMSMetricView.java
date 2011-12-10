package org.jboss.as.console.client.shared.runtime.jms;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.core.DisposableViewImpl;
import org.jboss.as.console.client.shared.runtime.Metric;

/**
 * @author Heiko Braun
 * @date 12/10/11
 */
public class JMSMetricView extends DisposableViewImpl implements JMSMetricPresenter.MyView{

    private JMSMetricPresenter presenter;
    private TopicMetrics topicMetrics;

    @Override
    public Widget createWidget() {

        this.topicMetrics = new TopicMetrics(presenter);

        TabLayoutPanel tabLayoutpanel = new TabLayoutPanel(25, Style.Unit.PX);
        tabLayoutpanel.addStyleName("default-tabpanel");

        tabLayoutpanel.add(topicMetrics.asWidget(), "Topics");
        tabLayoutpanel.add(new HTML("TODO"), "Queues");

        tabLayoutpanel.selectTab(0);

        return tabLayoutpanel;
    }

    @Override
    public void setPresenter(JMSMetricPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void clearSamples() {
       topicMetrics.clearSamples();
    }

    @Override
    public void setNonDurableMetric(Metric durableMetric) {

    }

    @Override
    public void setDurableMetric(Metric durableMetric) {

    }
}
