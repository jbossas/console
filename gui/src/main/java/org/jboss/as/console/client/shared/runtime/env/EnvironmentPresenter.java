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
package org.jboss.as.console.client.shared.runtime.env;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.Proxy;
import org.jboss.as.console.client.core.BootstrapContext;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.core.SuspendableView;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.jboss.as.console.client.shared.runtime.RuntimeBaseAddress;
import org.jboss.as.console.client.shared.state.ServerSelectionChanged;
import org.jboss.as.console.client.shared.subsys.RevealStrategy;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.Property;

import java.util.ArrayList;
import java.util.List;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * @author Harald Pehl
 * @date 15/10/12
 */
public class EnvironmentPresenter extends Presenter<EnvironmentPresenter.MyView,
        EnvironmentPresenter.MyProxy> implements ServerSelectionChanged.ChangeListener
{
    @ProxyCodeSplit
    @NameToken(NameTokens.EnvironmentPresenter)
    public interface MyProxy extends Proxy<EnvironmentPresenter>, Place
    {

    }


    public interface MyView extends SuspendableView
    {
        void setPresenter(EnvironmentPresenter environmentPresenter);

        void setEnvironment(List<PropertyRecord> environment);

        void clearEnvironment();
    }


    private final DispatchAsync dispatcher;
    private final BeanFactory factory;
    private final RevealStrategy revealStrategy;
    private final BootstrapContext bootstrap;


    @Inject
    public EnvironmentPresenter(final EventBus eventBus, final MyView view,
            final MyProxy proxy, final DispatchAsync dispatcher, final BeanFactory factory,
            final RevealStrategy revealStrategy, final BootstrapContext bootstrap)
    {
        super(eventBus, view, proxy);
        this.dispatcher = dispatcher;
        this.factory = factory;
        this.revealStrategy = revealStrategy;
        this.bootstrap = bootstrap;
    }

    @Override
    protected void onBind()
    {
        super.onBind();
        getView().setPresenter(this);
        getEventBus().addHandler(ServerSelectionChanged.TYPE, this);
    }

    @Override
    protected void revealInParent()
    {
        revealStrategy.revealInRuntimeParent(this);
    }

    @Override
    protected void onReset()
    {
        super.onReset();
        refresh();
    }


    @Override
    public void onServerSelectionChanged(boolean isRunning) {
        if(isVisible()) refresh();
    }

    public void refresh()
    {

        ModelNode operation = new ModelNode();
        operation.get(ADDRESS).set(RuntimeBaseAddress.get());
        operation.get(ADDRESS).add("core-service", "platform-mbean");
        operation.get(ADDRESS).add("type", "runtime");
        operation.get(OP).set(READ_ATTRIBUTE_OPERATION);
        operation.get(NAME).set("system-properties");

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>()
        {
            @Override
            public void onSuccess(DMRResponse result)
            {
                ModelNode response = result.get();
                List<Property> properties = response.get(RESULT).asPropertyList();
                List<PropertyRecord> environment = new ArrayList<PropertyRecord>(properties.size());
                for (Property property : properties)
                {
                    PropertyRecord model = factory.property().as();
                    model.setKey(property.getName());
                    model.setValue(property.getValue().asString());
                    environment.add(model);
                }
                getView().setEnvironment(environment);
            }
        });
    }
}
