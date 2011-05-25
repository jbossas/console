/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jboss.as.console.client.shared.subsys.messaging;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.domain.profiles.ProfileMgmtPresenter;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.shared.model.ModelAdapter;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.messaging.model.ConnectionFactory;
import org.jboss.as.console.client.shared.subsys.messaging.model.JMSEndpoint;
import org.jboss.as.console.client.shared.subsys.messaging.model.Queue;
import org.jboss.as.console.client.standalone.ServerMgmtApplicationPresenter;
import org.jboss.as.console.client.widgets.DefaultWindow;
import org.jboss.as.console.client.widgets.forms.PropertyBinding;
import org.jboss.as.console.client.widgets.forms.PropertyMetaData;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.Property;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * @author Heiko Braun
 * @date 3/29/11
 */
public class JMSPresenter extends Presenter<JMSPresenter.MyView, JMSPresenter.MyProxy> {

    private final PlaceManager placeManager;
    private DispatchAsync dispatcher;
    private BeanFactory factory;
    private PropertyMetaData propertyMetaData;
    private DefaultWindow window;


    @ProxyCodeSplit
    @NameToken(NameTokens.JMSPresenter)
    public interface MyProxy extends Proxy<JMSPresenter>, Place {
    }

    public interface MyView extends View {
        void setPresenter(JMSPresenter presenter);
        void setQueues(List<Queue> queues);
        void setTopics(List<JMSEndpoint> topics);
        void setConnectionFactories(List<ConnectionFactory> factories);
        void enableEditQueue(boolean b);
        void enableEditTopic(boolean b);
    }

    @Inject
    public JMSPresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager, DispatchAsync dispatcher,
            BeanFactory factory, PropertyMetaData propertyMetaData) {
        super(eventBus, view, proxy);

        this.placeManager = placeManager;
        this.dispatcher = dispatcher;
        this.factory = factory;
        this.propertyMetaData  = propertyMetaData;
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
    }


    @Override
    protected void onReset() {
        super.onReset();
        loadJMSConfig();
    }

    @Override
    protected void revealInParent() {
        if(Console.isStandalone())
            RevealContentEvent.fire(getEventBus(), ServerMgmtApplicationPresenter.TYPE_MainContent, this);
        else
            RevealContentEvent.fire(getEventBus(), ProfileMgmtPresenter.TYPE_MainContent, this);
    }

    void loadJMSConfig() {

        ModelNode operation = new ModelNode();
        operation.get(OP).set(READ_RESOURCE_OPERATION);
        operation.get(RECURSIVE).set(Boolean.TRUE);
        operation.get(ADDRESS).set(Baseadress.get());
        operation.get(ADDRESS).add("subsystem", "jms");

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = ModelNode.fromBase64(result.getResponseText());
                ModelNode payload = response.get("result").asObject();

                parseFactories(payload);
                parseQueues(payload);
                parseTopics(payload);
            }
        });

    }

    private void parseQueues(ModelNode response) {

        List<Property> propList = response.get("queue").asPropertyList();
        List<Queue> queues = new ArrayList<Queue>(propList.size());

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

        getView().setQueues(queues);
    }

    private void parseTopics(ModelNode response) {
        List<Property> propList = response.get("topic").asPropertyList();
        List<JMSEndpoint> topics = new ArrayList<JMSEndpoint>(propList.size());

        for(Property prop : propList)
        {
            JMSEndpoint topic = factory.topic().as();
            topic.setName(prop.getName());

            ModelNode propValue = prop.getValue();
            String jndi = propValue.get("entries").asList().get(0).asString();
            topic.setJndiName(jndi);

            topics.add(topic);
        }

        getView().setTopics(topics);

    }

    private void parseFactories(ModelNode response) {
        try {

            // factories
            List<Property> factories = response.get("connection-factory").asPropertyList();
            List<ConnectionFactory> factoryModels = new ArrayList<ConnectionFactory>(factories.size());

            for(Property factoryProp : factories)
            {
                String name = factoryProp.getName();

                ModelNode factoryValue = factoryProp.getValue();
                String jndi = factoryValue.get("entries").asList().get(0).asString();

                ConnectionFactory factoryModel = factory.connectionFactory().as();
                factoryModel.setName(name);
                factoryModel.setJndiName(jndi);

                factoryModels.add(factoryModel);
            }


            getView().setConnectionFactories(factoryModels);

        } catch (Throwable e) {
            Console.error("Failed to parse response: " + e.getMessage());
        }
    }


    public void onEditQueue() {
        getView().enableEditQueue(true);
    }

    // TODO: https://issues.jboss.org/browse/AS7-756
    public void onSaveQueue(final String name, Map<String, Object> changedValues) {
        getView().enableEditQueue(false);

        if(changedValues.isEmpty()) return;

        ModelNode proto = new ModelNode();
        proto.get(OP).set(WRITE_ATTRIBUTE_OPERATION);
        proto.get(ADDRESS).set(Baseadress.get());
        proto.get(ADDRESS).add("subsystem", "jms");
        proto.get(ADDRESS).add("queue", name);

        List<PropertyBinding> bindings = propertyMetaData.getBindingsForType(Queue.class);
        ModelNode operation  = ModelAdapter.detypedFromChangeset(proto, changedValues, bindings);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {

            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = ModelNode.fromBase64(result.getResponseText());
                boolean successful = response.get(OUTCOME).asString().equals(SUCCESS);
                if(successful)
                    Console.info("Updated queue "+name);
                else
                    Console.error("Failed to update queue " + name, response.toString());

            }
        });

    }

    public void onCreateQueue(final Queue entity) {

        closeDialogue();

        ModelNode queue = new ModelNode();
        queue.get(OP).set(ADD);
        queue.get(ADDRESS).set(Baseadress.get());
        queue.get(ADDRESS).add("subsystem", "jms");
        queue.get(ADDRESS).add("queue", entity.getName());

        queue.get("entries").setEmptyList();
        queue.get("entries").add(entity.getJndiName());

        queue.get("durable").set(entity.isDurable());

        if(entity.getSelector()!=null)
            queue.get("selector").set(entity.getSelector());

        dispatcher.execute(new DMRAction(queue), new SimpleCallback<DMRResponse>() {

            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = ModelNode.fromBase64(result.getResponseText());
                boolean successful = response.get(OUTCOME).asString().equals(SUCCESS);
                if(successful)
                    Console.info("Created queue "+entity.getName());
                else
                    Console.error("Failed to create queue " + entity.getName(), response.toString());

                Console.schedule(new Command() {
                    @Override
                    public void execute() {
                        loadJMSConfig();
                    }
                });

            }
        });

    }

    public void onDeleteQueue(final Queue entity) {
        ModelNode operation = new ModelNode();
        operation.get(OP).set(REMOVE);
        operation.get(ADDRESS).set(Baseadress.get());
        operation.get(ADDRESS).add("subsystem", "jms");
        operation.get(ADDRESS).add("queue", entity.getName());

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {

            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = ModelNode.fromBase64(result.getResponseText());
                boolean successful = response.get(OUTCOME).asString().equals(SUCCESS);
                if(successful)
                    Console.info("Removed queue "+entity.getName());
                else
                    Console.error("Failed to remove queue " + entity.getName(), response.toString());

                Console.schedule(new Command() {
                    @Override
                    public void execute() {
                        loadJMSConfig();
                    }
                });
            }
        });
    }

    public void launchNewQueueDialogue() {
        window = new DefaultWindow("Create JMS Queue ");
        window.setWidth(320);
        window.setHeight(240);
        window.addCloseHandler(new CloseHandler<PopupPanel>() {
            @Override
            public void onClose(CloseEvent<PopupPanel> event) {

            }
        });

        window.setWidget(
                new NewQueueWizard(this).asWidget()
        );

        window.setGlassEnabled(true);
        window.center();
    }

    public void onDeleteTopic(final JMSEndpoint entity) {
        ModelNode operation = new ModelNode();
        operation.get(OP).set(REMOVE);
        operation.get(ADDRESS).set(Baseadress.get());
        operation.get(ADDRESS).add("subsystem", "jms");
        operation.get(ADDRESS).add("topic", entity.getName());

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {

            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = ModelNode.fromBase64(result.getResponseText());
                boolean successful = response.get(OUTCOME).asString().equals(SUCCESS);
                if(successful)
                    Console.info("Removed topic "+entity.getName());
                else
                    Console.error("Failed to remove topic " + entity.getName(), response.toString());

                Console.schedule(new Command() {
                    @Override
                    public void execute() {
                        loadJMSConfig();
                    }
                });
            }
        });
    }

    public void onEditTopic() {
        getView().enableEditTopic(true);
    }

    public void onSaveTopic(final String name, Map<String, Object> changedValues) {
        getView().enableEditTopic(false);

        if(changedValues.isEmpty()) return;

        ModelNode proto = new ModelNode();
        proto.get(OP).set(WRITE_ATTRIBUTE_OPERATION);
        proto.get(ADDRESS).set(Baseadress.get());
        proto.get(ADDRESS).add("subsystem", "jms");
        proto.get(ADDRESS).add("topic", name);

        List<PropertyBinding> bindings = propertyMetaData.getBindingsForType(JMSEndpoint.class);
        ModelNode operation  = ModelAdapter.detypedFromChangeset(proto, changedValues, bindings);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {

            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = ModelNode.fromBase64(result.getResponseText());
                boolean successful = response.get(OUTCOME).asString().equals(SUCCESS);
                if(successful)
                    Console.info("Updated topic "+name);
                else
                    Console.error("Failed to update topic " + name, response.toString());

            }
        });
    }

    public void launchNewTopicDialogue() {
        window = new DefaultWindow("Create JMS Topic ");
        window.setWidth(320);
        window.setHeight(240);
        window.addCloseHandler(new CloseHandler<PopupPanel>() {
            @Override
            public void onClose(CloseEvent<PopupPanel> event) {

            }
        });

        window.setWidget(
                new NewTopicWizard(this).asWidget()
        );

        window.setGlassEnabled(true);
        window.center();
    }

    public void closeDialogue() {
        window.hide();
    }

    public void onCreateTopic(final JMSEndpoint entity) {
        closeDialogue();

        ModelNode queue = new ModelNode();
        queue.get(OP).set(ADD);
        queue.get(ADDRESS).set(Baseadress.get());
        queue.get(ADDRESS).add("subsystem", "jms");
        queue.get(ADDRESS).add("topic", entity.getName());

        queue.get("entries").setEmptyList();
        queue.get("entries").add(entity.getJndiName());

        dispatcher.execute(new DMRAction(queue), new SimpleCallback<DMRResponse>() {

            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = ModelNode.fromBase64(result.getResponseText());
                boolean successful = response.get(OUTCOME).asString().equals(SUCCESS);
                if(successful)
                    Console.info("Created topic "+entity.getName());
                else
                    Console.error("Failed to create topic " + entity.getName(), response.toString());

                Console.schedule(new Command() {
                    @Override
                    public void execute() {
                        loadJMSConfig();
                    }
                });

            }
        });
    }


}
