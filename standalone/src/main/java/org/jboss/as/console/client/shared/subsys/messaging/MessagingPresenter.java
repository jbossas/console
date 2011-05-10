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

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.domain.profiles.ProfileMgmtPresenter;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.shared.subsys.messaging.model.AddressingPattern;
import org.jboss.as.console.client.shared.subsys.messaging.model.MessagingProvider;
import org.jboss.as.console.client.shared.subsys.messaging.model.SecurityPattern;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.Property;

import java.util.ArrayList;
import java.util.List;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * @author Heiko Braun
 * @date 5/10/11
 */
public class MessagingPresenter extends Presenter<MessagingPresenter.MyView, MessagingPresenter.MyProxy> {

    private final PlaceManager placeManager;
    private DispatchAsync dispatcher;
    private BeanFactory factory;

    @ProxyCodeSplit
    @NameToken(NameTokens.MessagingPresenter)
    public interface MyProxy extends Proxy<MessagingPresenter>, Place {
    }

    public interface MyView extends View {
        void setPresenter(MessagingPresenter presenter);
        void setProviderDetails(MessagingProvider provider);
    }

    @Inject
    public MessagingPresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager, DispatchAsync dispatcher,
            BeanFactory factory) {
        super(eventBus, view, proxy);

        this.placeManager = placeManager;
        this.dispatcher = dispatcher;
        this.factory = factory;
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
    }

    private void loadProviderDetails() {
        ModelNode operation = new ModelNode();
        operation.get(OP).set(READ_RESOURCE_OPERATION);
        operation.get(ADDRESS).add("profile", "default"); // todo
        operation.get(ADDRESS).add("subsystem", "messaging");


        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = ModelNode.fromBase64(result.getResponseText());
                MessagingProvider provider = parseResponse(response);
                getView().setProviderDetails(provider);

            }
        });
    }

    private MessagingProvider parseResponse(ModelNode response) {
        ModelNode model = response.get("result").asObject();

        System.out.println(model.toString());

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
        RevealContentEvent.fire(getEventBus(), ProfileMgmtPresenter.TYPE_MainContent, this);
    }
}
