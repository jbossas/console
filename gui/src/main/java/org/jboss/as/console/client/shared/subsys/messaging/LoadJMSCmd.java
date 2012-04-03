package org.jboss.as.console.client.shared.subsys.messaging;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.dispatch.AsyncCommand;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.shared.subsys.messaging.model.ConnectionFactory;
import org.jboss.as.console.client.shared.subsys.messaging.model.JMSEndpoint;
import org.jboss.as.console.client.shared.subsys.messaging.model.Queue;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.as.console.client.widgets.forms.EntityAdapter;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.Property;

import java.util.ArrayList;
import java.util.List;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * @author Heiko Braun
 * @date 12/10/11
 */
public class LoadJMSCmd implements AsyncCommand<AggregatedJMSModel> {

    private DispatchAsync dispatcher;
    private BeanFactory factory;
    private ApplicationMetaData metaData;
    private EntityAdapter<ConnectionFactory> factoryAdapter;

    public LoadJMSCmd(DispatchAsync dispatcher, BeanFactory factory, ApplicationMetaData metaData) {
        this.dispatcher = dispatcher;
        this.metaData = metaData;
        this.factory = factory;


        factoryAdapter = new EntityAdapter<ConnectionFactory>(ConnectionFactory.class, metaData);
    }

    @Override
    public void execute(AsyncCallback<AggregatedJMSModel> topicsAndQueuesAsyncCallback) {
        throw new RuntimeException("Use overridden method instead!");
    }

    public void execute(ModelNode address, final AsyncCallback<AggregatedJMSModel> callback) {
        ModelNode operation = new ModelNode();
        operation.get(OP).set(READ_RESOURCE_OPERATION);
        operation.get(RECURSIVE).set(Boolean.TRUE);
        operation.get(ADDRESS).set(address);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();
                ModelNode payload = response.get("result").asObject();

                List<ConnectionFactory> factories = parseFactories(payload);
                List<Queue> queues = parseQueues(payload);
                List<JMSEndpoint> topics = parseTopics(payload);

                AggregatedJMSModel model = new AggregatedJMSModel(factories, queues, topics);
                callback.onSuccess(model);
            }
        });
    }

    private List<ConnectionFactory> parseFactories(ModelNode response) {

        List<ConnectionFactory> factoryModels = new ArrayList<ConnectionFactory>();

        try {

            // factories
            List<Property> factories = response.get("connection-factory").asPropertyList();


            for(Property factoryProp : factories)
            {
                String name = factoryProp.getName();

                ModelNode factoryValue = factoryProp.getValue();
                String jndi = factoryValue.get("entries").asList().get(0).asString();

                /*ConnectionFactory factoryModel = factory.connectionFactory().as();
                factoryModel.setName(name);
                factoryModel.setJndiName(jndi);*/

                ConnectionFactory connectionFactory = factoryAdapter.fromDMR(factoryValue);
                connectionFactory.setName(name);
                connectionFactory.setJndiName(jndi);

                factoryModels.add(connectionFactory);
            }




        } catch (Throwable e) {
            Console.error("Failed to parse response: " + e.getMessage());
        }

        return factoryModels;
    }


    private List<Queue> parseQueues(ModelNode response) {

        List<Queue> queues = new ArrayList<Queue>();

        if(response.hasDefined("jms-queue")) {
            List<Property> propList = response.get("jms-queue").asPropertyList();

            for(Property prop : propList)
            {
                Queue queue = factory.queue().as();
                queue.setName(prop.getName());

                ModelNode propValue = prop.getValue();
                String jndi = propValue.get("entries").asList().get(0).asString();
                queue.setJndiName(jndi);

                if(propValue.hasDefined("durable"))
                    queue.setDurable(propValue.get("durable").asBoolean());

                if(propValue.hasDefined("selector"))
                    queue.setSelector(propValue.get("selector").asString());

                queues.add(queue);
            }
        }

        return queues;

    }

    private List<JMSEndpoint> parseTopics(ModelNode response) {
        List<JMSEndpoint> topics = new ArrayList<JMSEndpoint>();

        if(response.hasDefined("jms-topic"))
        {
            List<Property> propList = response.get("jms-topic").asPropertyList();

            for(Property prop : propList)
            {
                JMSEndpoint topic = factory.topic().as();
                topic.setName(prop.getName());

                ModelNode propValue = prop.getValue();
                String jndi = propValue.get("entries").asList().get(0).asString();
                topic.setJndiName(jndi);

                topics.add(topic);
            }
        }

        return topics;
    }
}
