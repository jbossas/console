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
import org.jboss.as.console.client.shared.model.ResponseWrapper;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.RevealStrategy;
import org.jboss.as.console.client.shared.subsys.messaging.model.AddressingPattern;
import org.jboss.as.console.client.shared.subsys.messaging.model.ConnectionFactory;
import org.jboss.as.console.client.shared.subsys.messaging.model.JMSEndpoint;
import org.jboss.as.console.client.shared.subsys.messaging.model.MessagingProvider;
import org.jboss.as.console.client.shared.subsys.messaging.model.Queue;
import org.jboss.as.console.client.shared.subsys.messaging.model.SecurityPattern;
import org.jboss.as.console.client.widgets.forms.PropertyBinding;
import org.jboss.as.console.client.widgets.forms.PropertyMetaData;
import org.jboss.ballroom.client.widgets.window.DefaultWindow;
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

    public String getCurrentServer() {
        return "default";
    }

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

        void setSecurityConfig(List<SecurityPattern> secPatterns);
        void setAddressingConfig(List<AddressingPattern> addrPatterns);
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
        loadSecurityConfig();
        loadAddressingConfig();
        loadJMSConfig();
    }

    private void loadProviderDetails() {
        ModelNode operation = new ModelNode();
        operation.get(OP).set(READ_RESOURCE_OPERATION);
        operation.get(RECURSIVE).set(Boolean.TRUE);
        operation.get(ADDRESS).set(Baseadress.get());
        operation.get(ADDRESS).add("subsystem", "messaging");
        operation.get(ADDRESS).add("hornetq-server", getCurrentServer());

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {

            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = ModelNode.fromBase64(result.getResponseText());
                ModelNode model = response.get("result").asObject();

                MessagingProvider provider = factory.messagingProvider().as();
                provider.setName(getCurrentServer());

                provider.setPersistenceEnabled(model.get("persistence-enabled").asBoolean());
                provider.setSecurityEnabled(model.get("security-enabled").asBoolean());
                provider.setMessageCounterEnabled(model.get("message-counter-enabled").asBoolean());

                // socket binding ref
                if(model.hasDefined("connector"))
                {
                    List<Property> connectorPropList = model.get("connector").asPropertyList();
                    for(Property connectorProp : connectorPropList)
                    {
                        if("netty".equals(connectorProp.getName()))
                        {
                            String socketBinding = connectorProp.getValue().asObject().get("socket-binding").asString();
                            provider.setConnectorBinding(socketBinding);
                        }
                    }
                }

                if(model.hasDefined("acceptor"))
                {
                    List<Property> acceptorPropList = model.get("acceptor").asPropertyList();
                    for(Property acceptorProp : acceptorPropList)
                    {
                        if("netty".equals(acceptorProp.getName()))
                        {
                            String socketBinding = acceptorProp.getValue().asObject().get("socket-binding").asString();
                            provider.setAcceptorBinding(socketBinding);
                        }
                    }
                }

                providerEntity = provider;
                getView().setProviderDetails(provider);

            }
        });
    }

    private void loadSecurityConfig() {

        //  /subsystem=messaging/hornetq-server=default:read-children-resources(child-type=security-setting)
        ModelNode operation = new ModelNode();
        operation.get(OP).set(READ_CHILDREN_RESOURCES_OPERATION);
        operation.get(RECURSIVE).set(Boolean.TRUE);
        operation.get(ADDRESS).set(Baseadress.get());
        operation.get(ADDRESS).add("subsystem", "messaging");
        operation.get(ADDRESS).add("hornetq-server", getCurrentServer());
        operation.get(CHILD_TYPE).set("security-setting");

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {

            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = ModelNode.fromBase64(result.getResponseText());

                List<Property> payload = response.get(RESULT).asPropertyList();
                List<SecurityPattern> secPatterns = new ArrayList<SecurityPattern>();

                for(Property prop : payload)
                {
                    String pattern = prop.getName();
                    List<Property> roles = prop.getValue().asPropertyList();

                    for(Property role : roles) {
                        if("role".equals(role.getName()) && role.getValue().isDefined())
                        {
                            List<Property> permList = role.getValue().asPropertyList();

                            for(Property perm : permList)
                            {
                                ModelNode permValue = perm.getValue().asObject();
                                SecurityPattern model = factory.messagingSecurity().as();
                                model.setPattern(pattern);
                                model.setRole(perm.getName());

                                model.setSend(permValue.get("send").asBoolean());
                                model.setConsume(permValue.get("consume").asBoolean());
                                model.setManage(permValue.get("manage").asBoolean());
                                model.setCreateDurableQueue(permValue.get("create-durable-queue").asBoolean());
                                model.setDeleteDurableQueue(permValue.get("delete-durable-queue").asBoolean());
                                model.setCreateNonDurableQueue(permValue.get("create-non-durable-queue").asBoolean());
                                model.setDeleteNonDurableQueue(permValue.get("delete-non-durable-queue").asBoolean());

                                secPatterns.add(model);
                            }
                        }
                    }

                }

                getView().setSecurityConfig(secPatterns);

            }
        });
    }


    private void loadAddressingConfig() {


        ModelNode operation = new ModelNode();
        operation.get(OP).set(READ_CHILDREN_RESOURCES_OPERATION);
        operation.get(RECURSIVE).set(Boolean.TRUE);
        operation.get(ADDRESS).set(Baseadress.get());
        operation.get(ADDRESS).add("subsystem", "messaging");
        operation.get(ADDRESS).add("hornetq-server", getCurrentServer());
        operation.get(CHILD_TYPE).set("address-setting");

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {

            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = ModelNode.fromBase64(result.getResponseText());

                List<AddressingPattern> addrPatterns = new ArrayList<AddressingPattern>();
                List<Property> payload = response.get(RESULT).asPropertyList();

                for(Property prop : payload)
                {
                    String pattern = prop.getName();
                    ModelNode value = prop.getValue().asObject();

                    AddressingPattern model = factory.messagingAddress().as();
                    model.setPattern(pattern);
                    model.setDeadLetterQueue(value.get("dead-letter-address").asString());
                    model.setExpiryQueue(value.get("expiry-address").asString());
                    model.setRedeliveryDelay(value.get("redelivery-delay").asInt());
                    model.setMaxDelivery(value.get("max-delivery-attempts").asInt());

                    addrPatterns.add(model);

                }


                getView().setAddressingConfig(addrPatterns);

            }
        });
    }

    @Override
    protected void revealInParent() {
        revealStrategy.revealInParent(this);
    }

    public void launchNewSecDialogue() {
        window = new DefaultWindow("New Security Setting");
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

    // TODO: https://issues.jboss.org/browse/AS7-1892
    public void onCreateSecPattern(final SecurityPattern pattern) {
        closeDialogue();

        ModelNode operation = new ModelNode();
        operation.get(OP).set(ADD);
        operation.get(ADDRESS).set(Baseadress.get());
        operation.get(ADDRESS).add("subsystem", "messaging");
        operation.get(ADDRESS).add("hornetq-server", getCurrentServer());
        operation.get(ADDRESS).add("security-setting", pattern.getPattern());
        operation.get(ADDRESS).add("role", pattern.getRole());

        operation.get("send").set(pattern.isSend());
        operation.get("consume").set(pattern.isConsume());
        operation.get("manage").set(pattern.isManage());

        System.out.println(operation);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {

            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = ModelNode.fromBase64(result.getResponseText());
                boolean successful = response.get(OUTCOME).asString().equals(SUCCESS);
                if(successful)
                    Console.info("Added security setting ");
                else
                    Console.error("Failed to add security setting" + pattern.getPattern(), response.toString());

                loadSecurityConfig();
            }
        });
    }

    public void onSaveSecDetails(final SecurityPattern pattern, Map<String, Object> changedValues) {
        ModelNode proto = new ModelNode();
        proto.get(OP).set(WRITE_ATTRIBUTE_OPERATION);
        proto.get(ADDRESS).set(Baseadress.get());
        proto.get(ADDRESS).add("subsystem", "messaging");
        proto.get(ADDRESS).add("hornetq-server", getCurrentServer());
        proto.get(ADDRESS).add("security-setting", pattern.getPattern());
        proto.get(ADDRESS).add("role", pattern.getRole());

        List<PropertyBinding> bindings = propertyMetaData.getBindingsForType(SecurityPattern.class);
        ModelNode operation  = ModelAdapter.detypedFromChangeset(proto, changedValues, bindings);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {

            @Override
            public void onSuccess(DMRResponse result) {
                ResponseWrapper<Boolean> response = ModelAdapter.wrapBooleanResponse(result);
                if(response.getUnderlying())
                    Console.info("Updated security setting "+pattern);
                else
                    Console.error("Failed to update security setting " + pattern, response.getResponse().toString());

                loadSecurityConfig();
            }
        });
    }

    public void onDeleteSecDetails(final SecurityPattern pattern) {
        ModelNode operation = new ModelNode();
        operation.get(OP).set(REMOVE);
        operation.get(ADDRESS).set(Baseadress.get());
        operation.get(ADDRESS).add("subsystem", "messaging");
        operation.get(ADDRESS).add("hornetq-server", getCurrentServer());
        operation.get(ADDRESS).add("security-setting", pattern.getPattern());
        operation.get(ADDRESS).add("role", pattern.getRole());

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {

            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = ModelNode.fromBase64(result.getResponseText());
                boolean successful = response.get(OUTCOME).asString().equals(SUCCESS);
                if(successful)
                    Console.info("Removed security setting ");
                else
                    Console.error("Failed to remove security setting" + pattern.getPattern(), response.toString());

                loadSecurityConfig();
            }
        });
    }

    public void onDeleteAddressDetails(final AddressingPattern addressingPattern) {
        ModelNode operation = new ModelNode();
        operation.get(OP).set(REMOVE);
        operation.get(ADDRESS).set(Baseadress.get());
        operation.get(ADDRESS).add("subsystem", "messaging");
        operation.get(ADDRESS).add("hornetq-server", getCurrentServer());
        operation.get(ADDRESS).add("address-setting", addressingPattern.getPattern());

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {

            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = ModelNode.fromBase64(result.getResponseText());
                boolean successful = response.get(OUTCOME).asString().equals(SUCCESS);
                if(successful)
                    Console.info("Removed address setting ");
                else
                    Console.error("Failed to remove address setting" + addressingPattern.getPattern(), response.toString());

                loadAddressingConfig();
            }
        });
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

    public void onSaveAddressDetails(final AddressingPattern entity, Map<String, Object> changedValues) {
        ModelNode proto = new ModelNode();
        proto.get(OP).set(WRITE_ATTRIBUTE_OPERATION);
        proto.get(ADDRESS).set(Baseadress.get());
        proto.get(ADDRESS).add("subsystem", "messaging");
        proto.get(ADDRESS).add("hornetq-server", getCurrentServer());
        proto.get(ADDRESS).add("address-setting", entity.getPattern());

        List<PropertyBinding> bindings = propertyMetaData.getBindingsForType(AddressingPattern.class);
        ModelNode operation  = ModelAdapter.detypedFromChangeset(proto, changedValues, bindings);

        System.out.println(operation);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {

            @Override
            public void onSuccess(DMRResponse result) {
                ResponseWrapper<Boolean> response = ModelAdapter.wrapBooleanResponse(result);
                if(response.getUnderlying())
                    Console.info("Updated address setting "+entity.getPattern());
                else
                    Console.error("Failed to update address setting " + entity.getPattern(), response.getResponse().toString());

                loadAddressingConfig();
            }
        });
    }

    public void onCreateAddressPattern(final AddressingPattern address) {
        closeDialogue();

        ModelNode operation = new ModelNode();
        operation.get(OP).set(ADD);
        operation.get(ADDRESS).set(Baseadress.get());
        operation.get(ADDRESS).add("subsystem", "messaging");
        operation.get(ADDRESS).add("hornetq-server", getCurrentServer());
        operation.get(ADDRESS).add("address-setting", address.getPattern());

        operation.get("dead-letter-address").set(address.getDeadLetterQueue());
        operation.get("expiry-address").set(address.getExpiryQueue());
        operation.get("max-delivery-attempts").set(address.getMaxDelivery());
        operation.get("redelivery-delay").set(address.getRedeliveryDelay());

        System.out.println(operation);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {

            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = ModelNode.fromBase64(result.getResponseText());
                boolean successful = response.get(OUTCOME).asString().equals(SUCCESS);
                if(successful)
                    Console.info("Added address setting ");
                else
                    Console.error("Failed to add address setting" + address.getPattern(), response.toString());

                loadAddressingConfig();
            }
        });
    }


    // JMS
    void loadJMSConfig() {

        ModelNode operation = new ModelNode();
        operation.get(OP).set(READ_RESOURCE_OPERATION);
        operation.get(RECURSIVE).set(Boolean.TRUE);
        operation.get(ADDRESS).set(Baseadress.get());
        operation.get(ADDRESS).add("subsystem", "messaging");
        operation.get(ADDRESS).add("hornetq-server", getCurrentServer());

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

    public void onSaveQueue(final String name, Map<String, Object> changedValues) {
        getJMSView().enableEditQueue(false);

        if(changedValues.isEmpty()) return;

        ModelNode proto = new ModelNode();
        proto.get(OP).set(WRITE_ATTRIBUTE_OPERATION);
        proto.get(ADDRESS).set(Baseadress.get());
        proto.get(ADDRESS).add("subsystem", "messaging");
        proto.get(ADDRESS).add("hornetq-server", getCurrentServer());
        proto.get(ADDRESS).add("jms-queue", name);

        // selector hack
        //if(changedValues.containsKey("selector") && changedValues.get("selector").equals(""))
        //    changedValues.put("selector", "undefined");

        List<PropertyBinding> bindings = propertyMetaData.getBindingsForType(Queue.class);
        ModelNode operation  = ModelAdapter.detypedFromChangeset(proto, changedValues, bindings);

        System.out.println(operation);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {

            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = ModelNode.fromBase64(result.getResponseText());
                boolean successful = response.get(OUTCOME).asString().equals(SUCCESS);
                if(successful)
                    Console.info("Updated queue "+name);
                else
                    Console.error("Failed to update queue " + name, response.toString());

                loadJMSConfig();
            }
        });

    }

    public void onCreateQueue(final Queue entity) {

        closeDialogue();

        ModelNode queue = new ModelNode();
        queue.get(OP).set(ADD);
        queue.get(ADDRESS).set(Baseadress.get());
        queue.get(ADDRESS).add("subsystem", "messaging");
        queue.get(ADDRESS).add("hornetq-server", getCurrentServer());
        queue.get(ADDRESS).add("jms-queue", entity.getName());

        queue.get("entries").setEmptyList();
        queue.get("entries").add(entity.getJndiName());

        queue.get("durable").set(entity.isDurable());

        if(entity.getSelector()!=null && !entity.getSelector().equals(""))
            queue.get("selector").set(entity.getSelector());

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
        operation.get(ADDRESS).add("hornetq-server", getCurrentServer());
        operation.get(ADDRESS).add("jms-queue", entity.getName());

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {

            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = ModelNode.fromBase64(result.getResponseText());
                boolean successful = response.get(OUTCOME).asString().equals(SUCCESS);
                if(successful)
                    Console.info("Removed queue "+entity.getName());
                else
                    Console.error("Failed to remove queue " + entity.getName(), response.toString());

                loadJMSConfig();

            }
        });
    }

    public void launchNewQueueDialogue() {
        window = new DefaultWindow("Create JMS Queue ");
        window.setWidth(480);
        window.setHeight(360);
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
        operation.get(ADDRESS).add("hornetq-server", getCurrentServer());
        operation.get(ADDRESS).add("jms-topic", entity.getName());

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {

            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = ModelNode.fromBase64(result.getResponseText());
                boolean successful = response.get(OUTCOME).asString().equals(SUCCESS);
                if(successful)
                    Console.info("Removed topic "+entity.getName());
                else
                    Console.error("Failed to remove topic " + entity.getName(), response.toString());

               loadJMSConfig();
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
        proto.get(ADDRESS).add("hornetq-server", getCurrentServer());
        proto.get(ADDRESS).add("jms-topic", name);

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

                loadJMSConfig();
            }
        });
    }

    public void launchNewTopicDialogue() {
        window = new DefaultWindow("Create JMS Topic ");
        window.setWidth(480);
        window.setHeight(360);
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

        ModelNode topic = new ModelNode();
        topic.get(OP).set(ADD);
        topic.get(ADDRESS).set(Baseadress.get());
        topic.get(ADDRESS).add("subsystem", "messaging");
        topic.get(ADDRESS).add("hornetq-server", getCurrentServer());
        topic.get(ADDRESS).add("jms-topic", entity.getName());

        topic.get("entries").setEmptyList();
        topic.get("entries").add(entity.getJndiName());

        dispatcher.execute(new DMRAction(topic), new SimpleCallback<DMRResponse>() {

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

    public void onSaveProviderConfig(Map<String, Object> changeset) {

        ModelNode proto = new ModelNode();
        proto.get(OP).set(WRITE_ATTRIBUTE_OPERATION);
        proto.get(ADDRESS).set(Baseadress.get());
        proto.get(ADDRESS).add("subsystem", "messaging");
        proto.get(ADDRESS).add("hornetq-server", getCurrentServer());

        List<PropertyBinding> bindings = propertyMetaData.getBindingsForType(MessagingProvider.class);
        ModelNode operation  = ModelAdapter.detypedFromChangeset(proto, changeset, bindings);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {

            @Override
            public void onSuccess(DMRResponse result) {
                ResponseWrapper<Boolean> response = ModelAdapter.wrapBooleanResponse(result);
                if(response.getUnderlying())
                    Console.info("Updated provider settings "+getCurrentServer());
                else
                    Console.error("Failed to update provider settings " + getCurrentServer(), response.getResponse().toString());

                loadProviderDetails();
            }
        });

    }
}
