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

package org.jboss.as.console.client.shared.sockets;

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
import org.jboss.dmr.client.ModelNode;

import java.util.ArrayList;
import java.util.List;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * @author Heiko Braun
 * @date 4/6/11
 */
public class SocketBindingPresenter extends Presenter<SocketBindingPresenter.MyView, SocketBindingPresenter.MyProxy> {

    private final PlaceManager placeManager;
    private DispatchAsync dispatcher;
    private BeanFactory factory;

    @ProxyCodeSplit
    @NameToken(NameTokens.SocketBindingPresenter)
    public interface MyProxy extends Proxy<SocketBindingPresenter>, Place {
    }

    public interface MyView extends View {
        void setPresenter(SocketBindingPresenter presenter);
        void updateGroups(List<String> groups);

        void setBindings(String groupName, List<SocketBinding> bindings);
    }

    @Inject
    public SocketBindingPresenter(
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
        loadBindingGroups();
    }

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(getEventBus(), ProfileMgmtPresenter.TYPE_MainContent, this);
    }


    private void loadBindingGroups()
    {
        // :read-children-names(child-type=socket-binding-group)

        ModelNode operation = new ModelNode();
        operation.get(ADDRESS).setEmptyList();
        operation.get(OP).set(READ_CHILDREN_NAMES_OPERATION);
        operation.get(CHILD_TYPE).set("socket-binding-group");

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {

            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = ModelNode.fromBase64(result.getResponseText());
                List<ModelNode> payload = response.get("result").asList();

                List<String> groups = new ArrayList<String>();
                for(ModelNode group : payload)
                {
                    groups.add(group.asString());
                }

                getView().updateGroups(groups);
            }
        });
    }

    public void onFilterGroup(String groupName) {
        loadBindings(groupName);
    }

    private void loadBindings(final String groupName) {

        // /socket-binding-group=standard-sockets:read-resource(recursive=true)
        ModelNode operation = new ModelNode();
        operation.get(ADDRESS).add("socket-binding-group", groupName);
        operation.get(OP).set(READ_RESOURCE_OPERATION);
        operation.get(RECURSIVE).set(true);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {

            @Override
            public void onSuccess(DMRResponse result) {

                ModelNode response = ModelNode.fromBase64(result.getResponseText());
                ModelNode payload = response.get("result").asObject();

                List<ModelNode> socketDescriptions= payload.get("socket-binding").asList();

                List<SocketBinding> bindings= new ArrayList<SocketBinding>();
                for(ModelNode socket : socketDescriptions)
                {

                    ModelNode value = socket.asProperty().getValue();

                    //System.out.println(value.toJSONString());

                    SocketBinding sb = factory.socketBinding().as();

                    sb.setName(value.get("name").asString());
                    sb.setPort(value.get("port").asInt());
                    String interfaceValue = value.get("interface").isDefined() ?
                            value.get("interface").asString() : "not set";

                    sb.setInterface(interfaceValue);
                    // TODO: multicast properties
                    sb.setMultiCastAddress("not set");
                    sb.setMultiCastPort(-1);

                    bindings.add(sb);
                }

                getView().setBindings(groupName, bindings);

            }
        });

    }
}
