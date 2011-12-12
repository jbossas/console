package org.jboss.as.console.client.shared.runtime.jms;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.core.DisposableViewImpl;
import org.jboss.as.console.client.shared.runtime.Metric;
import org.jboss.as.console.client.shared.subsys.messaging.model.JMSEndpoint;
import org.jboss.as.console.client.shared.subsys.messaging.model.Queue;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 12/10/11
 */
public class JMSMetricView extends DisposableViewImpl implements JMSMetricPresenter.MyView{

    private JMSMetricPresenter presenter;
    private TopicMetrics topicMetrics;
    private QueueMetrics queueMetrics;

    @Override
    public Widget createWidget() {

        this.topicMetrics = new TopicMetrics(presenter);
        this.queueMetrics= new QueueMetrics(presenter);

        TabLayoutPanel tabLayoutpanel = new TabLayoutPanel(25, Style.Unit.PX);
        tabLayoutpanel.addStyleName("default-tabpanel");

        tabLayoutpanel.add(topicMetrics.asWidget(), "Topics");
        tabLayoutpanel.add(queueMetrics.asWidget(), "Queues");

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
    public void setTopics(List<JMSEndpoint> topics) {
        topicMetrics.setTopics(topics);
    }

    @Override
    public void setQueues(List<Queue> queues) {
        queueMetrics.setQueues(queues);
    }

    @Override
    public void setQueueConsumerMetrics(Metric metrics) {
        queueMetrics.setQueueConsumerMetrics(metrics);
    }

    @Override
    public void setQueueMessageMetric(Metric metric) {
        queueMetrics.setQueueMessageMetric(metric);
    }

    @Override
    public void setTopcSubscriptionMetric(Metric subscriptions) {
        topicMetrics.setSubscriptionMetric(subscriptions);
    }

    @Override
    public void setTopicMessageMetric(Metric metric) {
        topicMetrics.setMessageCountMetric(metric);
    }
}
