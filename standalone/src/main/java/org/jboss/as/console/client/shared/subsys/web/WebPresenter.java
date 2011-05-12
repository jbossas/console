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

package org.jboss.as.console.client.shared.subsys.web;

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
import org.jboss.as.console.client.domain.profiles.CurrentSelectedProfile;
import org.jboss.as.console.client.domain.profiles.ProfileMgmtPresenter;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.shared.subsys.web.model.HttpConnector;
import org.jboss.as.console.client.shared.subsys.web.model.VirtualServer;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.Property;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * @author Heiko Braun
 * @date 5/11/11
 */
public class WebPresenter extends Presenter<WebPresenter.MyView, WebPresenter.MyProxy> {

    private final PlaceManager placeManager;
    private BeanFactory factory;
    private DispatchAsync dispatcher;
    private CurrentSelectedProfile currentProfile;


    @ProxyCodeSplit
    @NameToken(NameTokens.WebPresenter)
    public interface MyProxy extends Proxy<WebPresenter>, Place {
    }

    public interface MyView extends View {
        void setPresenter(WebPresenter presenter);

        void setConnectors(List<HttpConnector> connectors);

        void enableEditConnector(boolean b);

        void setVirtualServers(List<VirtualServer> servers);

        void enableEditVirtualServer(boolean b);

        void enableJSPConfig(boolean b);
    }

    @Inject
    public WebPresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager,
            BeanFactory factory, DispatchAsync dispatcher,
            CurrentSelectedProfile currentProfile) {
        super(eventBus, view, proxy);

        this.placeManager = placeManager;
        this.factory = factory;
        this.dispatcher = dispatcher;
        this.currentProfile = currentProfile;
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
    }


    @Override
    protected void onReset() {
        super.onReset();

        loadJSPConfig();
        loadConnectors();
        loadVirtualServer();
    }

    private void loadVirtualServer() {
        // /profile=default/subsystem=web:read-children-resources(child-type=virtual-server, recursive=true)
        ModelNode operation = new ModelNode();
        operation.get(OP).set(READ_CHILDREN_RESOURCES_OPERATION);
        operation.get(ADDRESS).add("profile", currentProfile.getName());
        operation.get(ADDRESS).add("subsystem", "web");
        operation.get(CHILD_TYPE).set("virtual-server");
        operation.get(RECURSIVE).set(Boolean.TRUE);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = ModelNode.fromBase64(result.getResponseText());

                List<Property> propList = response.get(RESULT).asPropertyList();
                List<VirtualServer> servers = new ArrayList<VirtualServer>(propList.size());

                for(Property prop : propList)
                {
                    String name = prop.getName();
                    ModelNode propValue = prop.getValue();

                    VirtualServer server = factory.virtualServer().as();
                    server.setName(name);

                    List<String> aliases = new ArrayList<String>();
                    if(propValue.hasDefined("alias"))
                    {
                        List<ModelNode> aliasList = propValue.get("alias").asList();
                        for(ModelNode alias : aliasList)
                            aliases.add(alias.asString());
                    }

                    server.setAlias(aliases);

                    if(propValue.hasDefined("default-web-module"))
                        server.setDefaultWebModule(propValue.get("default-web-module").asString());

                    servers.add(server);
                }

                getView().setVirtualServers(servers);

            }
        });

    }

    private void loadJSPConfig() {
        // TODO: https://issues.jboss.org/browse/AS7-748
    }

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(getEventBus(), ProfileMgmtPresenter.TYPE_MainContent, this);
    }

    private void loadConnectors() {

        // /profile=default/subsystem=web:read-children-resources(child-type=connector, recursive=true)
        ModelNode operation = new ModelNode();
        operation.get(OP).set(READ_CHILDREN_RESOURCES_OPERATION);
        operation.get(ADDRESS).add("profile", currentProfile.getName());
        operation.get(ADDRESS).add("subsystem", "web");
        operation.get(CHILD_TYPE).set("connector");
        operation.get(RECURSIVE).set(Boolean.TRUE);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = ModelNode.fromBase64(result.getResponseText());

                List<Property> propList = response.get(RESULT).asPropertyList();
                List<HttpConnector> connectors = new ArrayList<HttpConnector>(propList.size());

                for(Property prop : propList)
                {
                    String name = prop.getName();
                    ModelNode propValue = prop.getValue();

                    HttpConnector connector = factory.httpConnector().as();
                    connector.setName(name);

                    // TODO: https://issues.jboss.org/browse/AS7-747
                    if(propValue.hasDefined("enabled"))
                        connector.setEnabled(propValue.get("enabled").asBoolean());

                    connector.setScheme(propValue.get("scheme").asString());
                    connector.setSocketBinding(propValue.get("socket-binding").asString());
                    connector.setProtocol(propValue.get("protocol").asString());

                    connectors.add(connector);
                }

                getView().setConnectors(connectors);

            }
        });
    }

    public void onEditConnector() {
        getView().enableEditConnector(true);
    }

    public void onSaveConnector(String name, Map<String, Object> changedValues) {
        getView().enableEditConnector(false);
    }

    public void onDeleteConnector(String name) {

    }

    public void launchConnectorDialogue() {

    }


    public void onEditVirtualServer() {
        getView().enableEditVirtualServer(true);
    }

    public void onSaveVirtualServer(String name, Map<String, Object> changedValues) {
        getView().enableEditVirtualServer(false);
    }

    public void onDeleteVirtualServer(String name) {

    }

    public void launchVirtualServerDialogue() {

    }

    public void onEditJSPConfig() {
        getView().enableJSPConfig(true);
    }

    public void onSaveJSPConfig() {
        getView().enableJSPConfig(false);
    }
}
