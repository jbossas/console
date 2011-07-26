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
import org.jboss.as.console.client.shared.subsys.web.model.HttpConnector;
import org.jboss.as.console.client.shared.subsys.web.model.JSPContainerConfiguration;
import org.jboss.as.console.client.shared.subsys.web.model.VirtualServer;
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
 * @date 5/11/11
 */
public class WebPresenter extends Presenter<WebPresenter.MyView, WebPresenter.MyProxy> {

    private final PlaceManager placeManager;
    private BeanFactory factory;
    private DispatchAsync dispatcher;

    private DefaultWindow window;
    private PropertyMetaData propertyMetaData;

    private List<HttpConnector> connectors;
    private RevealStrategy revealStrategy;

    private List<VirtualServer> virtualServers;

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

        void setJSPConfig(JSPContainerConfiguration jspConfig);
    }

    @Inject
    public WebPresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager,
            BeanFactory factory, DispatchAsync dispatcher,
            PropertyMetaData propertyMetaData,
            RevealStrategy revealStrategy) {
        super(eventBus, view, proxy);

        this.placeManager = placeManager;
        this.factory = factory;
        this.dispatcher = dispatcher;
        this.propertyMetaData = propertyMetaData;
        this.revealStrategy = revealStrategy;

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

    @Override
    protected void revealInParent() {
        revealStrategy.revealInParent(this);
    }

    private void loadVirtualServer() {
        // /profile=default/subsystem=web:read-children-resources(child-type=virtual-server, recursive=true)
        ModelNode operation = new ModelNode();
        operation.get(OP).set(READ_CHILDREN_RESOURCES_OPERATION);
        operation.get(ADDRESS).set(Baseadress.get());
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

                virtualServers = servers;
                getView().setVirtualServers(servers);

            }
        });

    }

    private void loadJSPConfig() {

        // /profile=default/subsystem=web:read-resource

        ModelNode operation = new ModelNode();
        operation.get(OP).set(READ_RESOURCE_OPERATION);
        operation.get(ADDRESS).set(Baseadress.get());
        operation.get(ADDRESS).add("subsystem", "web");

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {

            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = ModelNode.fromBase64(result.getResponseText());

                ModelNode config = response.get(RESULT).asObject().get("configuration").asObject();
                ModelNode jspCfg  = config.get("jsp-configuration").asObject();
                ModelNode staticCfg = config.get("static-resources").asObject();

                JSPContainerConfiguration jspConfig = factory.jspConfig().as();
                jspConfig.setDisabled(jspCfg.get("disabled").asBoolean());
                jspConfig.setCheckInterval(jspCfg.get("check-interval").asInt());
                jspConfig.setDevelopment(jspCfg.get("development").asBoolean());
                jspConfig.setDisplaySource(jspCfg.get("display-source-fragment").asBoolean());
                jspConfig.setKeepGenerated(jspCfg.get("keep-generated").asBoolean());
                jspConfig.setListings(staticCfg.get("listings").asBoolean());
                jspConfig.setRecompile(jspCfg.get("recompile-on-fail").asBoolean());

                getView().setJSPConfig(jspConfig);
            }
        });

    }

    private void loadConnectors() {

        // /profile=default/subsystem=web:read-children-resources(child-type=connector, recursive=true)
        ModelNode operation = new ModelNode();
        operation.get(OP).set(READ_CHILDREN_RESOURCES_OPERATION);
        operation.get(ADDRESS).set(Baseadress.get());
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
                    else
                        connector.setEnabled(true); // the default value

                    connector.setScheme(propValue.get("scheme").asString());
                    connector.setSocketBinding(propValue.get("socket-binding").asString());
                    connector.setProtocol(propValue.get("protocol").asString());

                    connectors.add(connector);
                }

                setConnectors(connectors);
                getView().setConnectors(connectors);

            }
        });
    }

    private void setConnectors(List<HttpConnector> connectors) {
        this.connectors = connectors;
    }

    public void onEditConnector() {
        getView().enableEditConnector(true);
    }

    public void onSaveConnector(final String name, Map<String, Object> changedValues) {
        getView().enableEditConnector(false);

        if(changedValues.isEmpty()) return;

        ModelNode proto = new ModelNode();
        proto.get(OP).set(WRITE_ATTRIBUTE_OPERATION);
        proto.get(ADDRESS).set(Baseadress.get());
        proto.get(ADDRESS).add("subsystem", "web");
        proto.get(ADDRESS).add("connector", name);

        List<PropertyBinding> bindings = propertyMetaData.getBindingsForType(HttpConnector.class);
        ModelNode operation  = ModelAdapter.detypedFromChangeset(proto, changedValues, bindings);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {

            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = ModelNode.fromBase64(result.getResponseText());
                boolean successful = response.get(OUTCOME).asString().equals(SUCCESS);
                if(successful)
                    Console.info("Updated connector "+name);
                else
                    Console.error("Failed to update connector " + name, response.toString());

                loadConnectors();
            }
        });
    }

    public void onDeleteConnector(final String name) {
        ModelNode connector = new ModelNode();
        connector.get(OP).set(REMOVE);
        connector.get(ADDRESS).set(Baseadress.get());
        connector.get(ADDRESS).add("subsystem", "web");
        connector.get(ADDRESS).add("connector", name);

        dispatcher.execute(new DMRAction(connector), new SimpleCallback<DMRResponse>() {

            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = ModelNode.fromBase64(result.getResponseText());
                boolean successful = response.get(OUTCOME).asString().equals(SUCCESS);
                if(successful)
                    Console.info("Removed connector " + name);
                else
                    Console.error("Failed to remove connector " + name, response.toString());

                Console.schedule(new Command() {
                    @Override
                    public void execute() {
                        loadConnectors();
                    }
                });

            }
        });
    }

    public void launchConnectorDialogue() {
        window = new DefaultWindow("Create Connector");
        window.setWidth(480);
        window.setHeight(360);
        window.addCloseHandler(new CloseHandler<PopupPanel>() {
            @Override
            public void onClose(CloseEvent<PopupPanel> event) {

            }
        });

        window.setWidget(
                new NewConnectorWizard(this, connectors ).asWidget()
        );

        window.setGlassEnabled(true);
        window.center();
    }


    public void onCreateConnector(final HttpConnector entity) {
        closeDialogue();

        ModelNode connector = new ModelNode();
        connector.get(OP).set(ADD);
        connector.get(ADDRESS).set(Baseadress.get());
        connector.get(ADDRESS).add("subsystem", "web");
        connector.get(ADDRESS).add("connector", entity.getName());

        connector.get("protocol").set(entity.getProtocol());
        connector.get("scheme").set(entity.getScheme());
        connector.get("socket-binding").set(entity.getSocketBinding());
        connector.get("enabled").set(entity.isEnabled());

        dispatcher.execute(new DMRAction(connector), new SimpleCallback<DMRResponse>() {

            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = ModelNode.fromBase64(result.getResponseText());
                boolean successful = response.get(OUTCOME).asString().equals(SUCCESS);
                if(successful)
                    Console.info("Created connector " + entity.getName());
                else
                    Console.error("Failed to create connector " + entity.getName(), response.toString());

                Console.schedule(new Command() {
                    @Override
                    public void execute() {
                        loadConnectors();
                    }
                });

            }
        });
    }

    public void onEditVirtualServer() {
        getView().enableEditVirtualServer(true);
    }

    public void onCreateVirtualServer(final VirtualServer server) {
        closeDialogue();

        ModelNode operation = new ModelNode();
        operation.get(OP).set(ADD);
        operation.get(ADDRESS).set(Baseadress.get());
        operation.get(ADDRESS).add("subsystem", "web");
        operation.get(ADDRESS).add("virtual-server", server.getName());

        if(server.getAlias()!= null && server.getAlias().size()>0)
        {
            for(String alias : server.getAlias())
                operation.get("alias").add(alias);

        }

        if(server.getDefaultWebModule()!=null)
            operation.get("default-web-module").set(server.getDefaultWebModule());

        System.out.println(operation);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {

            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = ModelNode.fromBase64(result.getResponseText());
                boolean successful = response.get(OUTCOME).asString().equals(SUCCESS);
                if(successful)
                    Console.info("Created virtual server " + server.getName());
                else
                    Console.error("Failed to create virtual server" + server.getName(), response.toString());

                Console.schedule(new Command() {
                    @Override
                    public void execute() {
                        loadVirtualServer();
                    }
                });

            }
        });

    }

    public void onSaveVirtualServer(final String name, Map<String, Object> changedValues) {
        getView().enableEditVirtualServer(false);

        if(changedValues.isEmpty()) return;

        ModelNode proto = new ModelNode();
        proto.get(OP).set(WRITE_ATTRIBUTE_OPERATION);
        proto.get(ADDRESS).set(Baseadress.get());
        proto.get(ADDRESS).add("subsystem", "web");
        proto.get(ADDRESS).add("virtual-server", name);

        List<PropertyBinding> bindings = propertyMetaData.getBindingsForType(VirtualServer.class);
        ModelNode operation  = ModelAdapter.detypedFromChangeset(proto, changedValues, bindings);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {

            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = ModelNode.fromBase64(result.getResponseText());
                boolean successful = response.get(OUTCOME).asString().equals(SUCCESS);
                if(successful)
                    Console.info("Updated virtual server "+name);
                else
                    Console.error("Failed to update virtual server " + name, response.toString());

                loadConnectors();
            }
        });
    }

    public void onDeleteVirtualServer(final String name) {
        ModelNode operation = new ModelNode();
        operation.get(OP).set(REMOVE);
        operation.get(ADDRESS).set(Baseadress.get());
        operation.get(ADDRESS).add("subsystem", "web");
        operation.get(ADDRESS).add("virtual-server", name);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {

            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = ModelNode.fromBase64(result.getResponseText());
                boolean successful = response.get(OUTCOME).asString().equals(SUCCESS);
                if(successful)
                    Console.info("Success: Delete virtual server " + name);
                else
                    Console.error("Error: Failed to delete virtual server" + name, response.toString());

                Console.schedule(new Command() {
                    @Override
                    public void execute() {
                        loadVirtualServer();
                    }
                });

            }
        });

    }

    public void launchVirtualServerDialogue() {

        window = new DefaultWindow("Create Virtual Server");
        window.setWidth(480);
        window.setHeight(360);
        window.setWidget(
                new NewVirtualServerWizard(this,  virtualServers).asWidget()
        );

        window.setGlassEnabled(true);
        window.center();

    }

    public void onEditJSPConfig() {
        getView().enableJSPConfig(true);
        //TODO: implement
    }

    public void onSaveJSPConfig() {
        getView().enableJSPConfig(false);
        //TODO: implement
    }


    public void closeDialogue() {
        window.hide();
    }

}
