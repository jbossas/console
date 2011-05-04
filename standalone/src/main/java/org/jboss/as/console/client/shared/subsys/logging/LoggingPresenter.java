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

package org.jboss.as.console.client.shared.subsys.logging;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;
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
import org.jboss.as.console.client.domain.profiles.ProfileMgmtPresenter;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.shared.subsys.logging.model.LoggingHandler;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.Property;

import java.util.ArrayList;
import java.util.List;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;


/**
 * @author Stan Silvert
 * @date 3/29/11
 */
public class LoggingPresenter extends Presenter<LoggingPresenter.MyView, LoggingPresenter.MyProxy> {

    private final PlaceManager placeManager;
    private DispatchAsync dispatcher;

    private BeanFactory factory = GWT.create(BeanFactory.class);

    @ProxyCodeSplit
    @NameToken(NameTokens.LoggingPresenter)
    public interface MyProxy extends Proxy<LoggingPresenter>, Place {
    }

    public interface MyView extends View {
        void setPresenter(LoggingPresenter presenter);

        void updateLoggingHandlers(List<LoggingHandler> handlerss);
    }

    @Inject
    public LoggingPresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager, DispatchAsync dispatcher) {
        super(eventBus, view, proxy);

        this.placeManager = placeManager;
        this.dispatcher = dispatcher;
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
    }


    @Override
    protected void onReset() {
        super.onReset();
        loadLogging();
    }

    @Override
    protected void revealInParent() {
         RevealContentEvent.fire(getEventBus(), ProfileMgmtPresenter.TYPE_MainContent, this);
    }

    void loadLogging() {

        // /profile=default/subsystem=logging:read-children-resources(child-type=handler)

        ModelNode operation = new ModelNode();
        operation.get(OP).set(READ_CHILDREN_RESOURCES_OPERATION);
        operation.get(ADDRESS).add("profile", "default"); // TODO: selected profile
        operation.get(ADDRESS).add("subsystem", "logging");
        operation.get(CHILD_TYPE).set("handler");

        dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {
            @Override
            public void onFailure(Throwable caught) {
                Log.error("Failed to load datasource", caught);
            }

            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response  = ModelNode.fromBase64(result.getResponseText());
                List<ModelNode> payload = response.get("result").asList();

                List<LoggingHandler> handlers = new ArrayList<LoggingHandler>(payload.size());
                for(ModelNode item : payload)
                {
                    // returned as type property (key=handler name)
                    Property property = item.asProperty();
                    ModelNode handler = property.getValue().asObject();
                    String name = property.getName();

                    try {
                        LoggingHandler model = factory.loggingHandler().as();
                        model.setName(name);
                        model.setAutoflush(handler.get("autoflush").asBoolean());
                        model.setEncoding(handler.get("encoding").asString());
                        model.setFormatter(handler.get("formatter").asString());
                        model.setType(handler.get("handler-type").asString());
                        model.setLevel(handler.get("level").asString());
                        model.setQueueLength(handler.get("queue-length").asString());

                        handlers.add(model);

                    } catch (IllegalArgumentException e) {
                        Log.error("Failed to parse data source representation", e);
                    }
                }

                // finally update view
                getView().updateLoggingHandlers(handlers);
            }
        });

    }
}
