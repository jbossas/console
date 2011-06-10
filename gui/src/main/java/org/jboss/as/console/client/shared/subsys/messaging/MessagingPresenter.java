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
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.Proxy;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.shared.model.ModelAdapter;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.RevealStrategy;
import org.jboss.as.console.client.shared.subsys.messaging.model.AddressingPattern;
import org.jboss.as.console.client.shared.subsys.messaging.model.ConnectionFactory;
import org.jboss.as.console.client.shared.subsys.messaging.model.JMSEndpoint;
import org.jboss.as.console.client.shared.subsys.messaging.model.MessagingProvider;
import org.jboss.as.console.client.shared.subsys.messaging.model.Queue;
import org.jboss.as.console.client.shared.subsys.messaging.model.SecurityPattern;
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
 * @date 5/10/11
 */
public class MessagingPresenter extends Presenter<MessagingPresenter.MyView, MessagingPresenter.MyProxy> {

    private final PlaceManager placeManager;
    private DispatchAsync dispatcher;
    private BeanFactory factory;
    private MessagingProvider providerEntity;
    private DefaultWindow window = null;
    private RevealStrategy revealStrategy;
    private PropertyMetaData propertyMetaData;

    @ProxyCodeSplit
    @NameToken(NameTokens.MessagingPresenter)
    public interface MyProxy extends Proxy<MessagingPresenter>, Place {
    }

    public interface MyView extends View {

        // Messaging Provider
        void setPresenter(MessagingPresenter presenter);
        void setProviderDetails(MessagingProvider provider);
        void editSecDetails(boolean b);
        void editAddrDetails(boolean b);

    }

    public interface JMSView {
        void setQueues(List<Queue> queues);
        void setTopics(List<JMSEndpoint> topics);
        void setConnectionFactories(List<ConnectionFactory> factories);
        void enableEditQueue(boolean b);
        void enableEditTopic(boolean b);
    }

    @Inject
    public MessagingPresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager, DispatchAsync dispatcher,
            BeanFactory factory, RevealStrategy revealStrategy,
            PropertyMetaData propertyMetaData) {
        super(eventBus, view, proxy);

        this.placeManager = placeManager;
        this.dispatcher = dispatcher;
        this.factory = factory;
        this.revealStrategy = revealStrategy;
        this.propertyMetaData = propertyMetaData;
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
    }


    @Override
    protected void onReset() {
        super.onReset();
        loadProviderDetails();
        loadJMSConfig();
    }

    private void loadProviderDetails() {
        ModelNode operation = new ModelNode();
        operation.get(OP).set(READ_RESOURCE_OPERATION);
        operation.get(ADDRESS).set(Baseadress.get());
        operation.get(ADDRESS).add("subsystem", "messaging");


        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {

            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = ModelNode.fromBase64(result.getResponseText());
                MessagingProvider provider = parseResponse(response);

                providerEntity = provider;
                getView().setProviderDetails(provider);

            }
        });
    }

    private MessagingProvider parseResponse(ModelNode response) {
        ModelNode model = response.get("result").asObject();

        MessagingProvider provider = factory.messagingProvider().as();
        provider.setName("HornetQ"); // TODO: can this be retrieved incl. version?

        provider.setPersistenceEnabled(model.get("persistence-enabled").asBoolean());

        // security
        List<Property> secProps = model.get("security-setting").asPropertyList();
        List<SecurityPattern> secPatterns = new ArrayList<SecurityPattern>(secProps.size());
        for(Property prop : secProps)
        {
            SecurityPattern pattern = factory.messagingSecurity().as();
            pattern.setPattern(prop.getName());

            Property principalProp= prop.getValue().asProperty();
            pattern.setPrincipal(principalProp.getName());

            ModelNode propValue = principalProp.getValue().asObject();
            pattern.setSend(propValue.get("send").asBoolean());
            pattern.setConsume(propValue.get("consume").asBoolean());
            pattern.setManage(propValue.get("manage").asBoolean());
            pattern.setCreateDurableQueue(propValue.get("createDurableQueue").asBoolean());
            pattern.setDeleteDurableQueue(propValue.get("deleteDurableQueue").asBoolean());
            pattern.setCreateNonDurableQueue(propValue.get("createNonDurableQueue").asBoolean());
            pattern.setDeleteNonDurableQueue(propValue.get("deleteNonDurableQueue").asBoolean());

            secPatterns.add(pattern);
        }

        provider.setSecurityPatterns(secPatterns);


        // addressing
        List<Property> addrProps = model.get("address-setting").asPropertyList();
        List<AddressingPattern> addrPatterns = new ArrayList<AddressingPattern>(addrProps.size());
        for(Property prop : addrProps)
        {
            AddressingPattern pattern = factory.messagingAddress().as();
            pattern.setPattern(prop.getName());

            ModelNode propValue = prop.getValue().asObject();
            pattern.setDeadLetterQueue(propValue.get("dead-letter-address").asString());
            pattern.setExpiryQueue(propValue.get("expiry-address").asString());
            pattern.setRedeliveryDelay(propValue.get("redelivery-delay").asInt());

            addrPatterns.add(pattern);
        }

        provider.setAddressPatterns(addrPatterns);


        // socket binding ref
        List<Property> connectorPropList = model.get("connector").asPropertyList();
        for(Property connectorProp : connectorPropList)
        {
            if("netty".equals(connectorProp.getName()))
            {
                String socketBinding = connectorProp.getValue().asObject().get("socket-binding").asString();
                provider.setConnectorBinding(socketBinding);
            }
        }

        List<Property> acceptorPropList = model.get("acceptor").asPropertyList();
        for(Property acceptorProp : acceptorPropList)
        {
            if("netty".equals(acceptorProp.getName()))
            {
                String socketBinding = acceptorProp.getValue().asObject().get("socket-binding").asString();
                provider.setAcceptorBinding(socketBinding);
            }
        }

        return provider;
    }

    @Override
    protected void revealInParent() {
        revealStrategy.revealInParent(this);
    }

    public void launchNewSecDialogue() {
        window = new DefaultWindow("Security Pattern");
        window.setWidth(480);
        window.setHeight(360);
        window.addCloseHandler(new CloseHandler<PopupPanel>() {
            @Override
            public void onClose(CloseEvent<PopupPanel> event) {

            }
        });

        window.setWidget(
                new NewSecurityPatternWizard(this, providerEntity).asWidget()
        );

        window.setGlassEnabled(true);
        window.center();
    }

    public void onCreateSecPattern(SecurityPattern securityPattern) {
        closeDialogue();
    }

    public void onEditSecDetails() {
        getView().editSecDetails(true);
    }

    public void onSaveSecDetails(Map<String, Object> changedValues) {
        getView().editSecDetails(false);
    }

    public void onDeleteSecDetails(SecurityPattern pattern) {
        getView().editSecDetails(false);
    }

    public void onEditAddressDetails(AddressingPattern addressingPattern) {
        getView().editAddrDetails(true);
    }

    public void onDeleteAddressDetails(AddressingPattern addressingPattern) {

    }

    public void launchNewAddrDialogue() {
        window = new DefaultWindow("Addressing Pattern");
        window.setWidth(480);
        window.setHeight(360);
        window.addCloseHandler(new CloseHandler<PopupPanel>() {
            @Override
            public void onClose(CloseEvent<PopupPanel> event) {

            }
        });

        window.setWidget(
                new NewAddressPatternWizard(this, providerEntity).asWidget()
        );

        window.setGlassEnabled(true);
        window.center();
    }

    public void onSaveAddressDetails(Map<String, Object> changedValues) {
        getView().editAddrDetails(false);
    }

    public void onCreateAddressPattern(SecurityPattern addrPattern) {

    }


    // JMS
    void loadJMSConfig() {

        ModelNode operation = new ModelNode();
        operation.get(OP).set(READ_RESOURCE_OPERATION);
        operation.get(RECURSIVE).set(Boolean.TRUE);
        operation.get(ADDRESS).set(Baseadress.get());
        operation.get(ADDRESS).add("subsystem", "messaging");

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = ModelNode.fromBase64(result.getResponseText());
                ModelNode payload = response.get("result").asObject();

                System.out.println(payload);

                parseFactories(payload);
                parseQueues(payload);
                parseTopics(payload);
            }
        });

    }

    private void parseQueues(ModelNode response) {

        List<Property> propList = response.get("jms-queue").asPropertyList();
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

        getJMSView().setQueues(queues);
    }

    private void parseTopics(ModelNode response) {
        List<Property> propList = response.get("jms-topic").asPropertyList();
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

        getJMSView().setTopics(topics);

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


            getJMSView().setConnectionFactories(factoryModels);

        } catch (Throwable e) {
            Console.error("Failed to parse response: " + e.getMessage());
        }
    }


    public void onEditQueue() {
        getJMSView().enableEditQueue(true);
    }

    // TODO: https://issues.jboss.org/browse/AS7-756
    public void onSaveQueue(final String name, Map<String, Object> changedValues) {
        getJMSView().enableEditQueue(false);

        if(changedValues.isEmpty()) return;

        ModelNode proto = new ModelNode();
        proto.get(OP).set(WRITE_ATTRIBUTE_OPERATION);
        proto.get(ADDRESS).set(Baseadress.get());
        proto.get(ADDRESS).add("subsystem", "messaging");
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
        queue.get(ADDRESS).add("subsystem", "messaging");
        queue.get(ADDRESS).add("queue", entity.getName());

        queue.get("entries").setEmptyList();
        queue.get("entries").add(entity.getJndiName());

        queue.get("durable").set(entity.isDurable());

        if(entity.getSelector()!=null)
            queue.get("selector").set(entity.getSelector());

        System.out.println(queue);

        dispatcher.execute(new DMRAction(queue), new AsyncCallback<DMRResponse>() {

            @Override
            public void onFailure(Throwable caught) {
                Console.error("Failed to create queue", caught.getMessage());
            }

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
        operation.get(ADDRESS).add("subsystem", "messaging");
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
        operation.get(ADDRESS).add("subsystem", "messaging");
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
        getJMSView().enableEditTopic(true);
    }

    public void onSaveTopic(final String name, Map<String, Object> changedValues) {
        getJMSView().enableEditTopic(false);

        if(changedValues.isEmpty()) return;

        ModelNode proto = new ModelNode();
        proto.get(OP).set(WRITE_ATTRIBUTE_OPERATION);
        proto.get(ADDRESS).set(Baseadress.get());
        proto.get(ADDRESS).add("subsystem", "messaging");
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
        queue.get(ADDRESS).add("subsystem", "messaging");
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

    private JMSView getJMSView() {
        return (JMSView)getView();
    }
}
