package org.jboss.as.console.client.shared.subsys.messaging;

import org.jboss.as.console.client.shared.subsys.messaging.model.ConnectionFactory;
import org.jboss.as.console.client.shared.subsys.messaging.model.JMSEndpoint;
import org.jboss.as.console.client.shared.subsys.messaging.model.Queue;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 12/10/11
 */
public class AggregatedJMSModel {

    private List<ConnectionFactory> factories;
    private List<Queue> queues;
    private List<JMSEndpoint> topics;

    public AggregatedJMSModel(
            List<ConnectionFactory> factories,
            List<Queue> queues,
            List<JMSEndpoint> topics) {
        this.factories = factories;
        this.queues = queues;
        this.topics = topics;
    }

    public List<ConnectionFactory> getFactories() {
        return factories;
    }

    public List<Queue> getQueues() {
        return queues;
    }

    public List<JMSEndpoint> getTopics() {
        return topics;
    }
}
