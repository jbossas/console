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
package org.jboss.as.console.client.shared.runtime.ext;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.Proxy;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.core.SuspendableView;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.shared.state.ServerSelectionEvent;
import org.jboss.as.console.client.shared.subsys.RevealStrategy;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.Property;

import java.util.ArrayList;
import java.util.List;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;


/**
 * @author Harald Pehl
 * @date 10/16/2012
 */
public class ExtensionPresenter  extends Presenter<ExtensionPresenter.MyView,
        ExtensionPresenter.MyProxy>
{
    @ProxyCodeSplit
    @NameToken(NameTokens.ExtensionsPresenter)
    public interface MyProxy extends Proxy<ExtensionPresenter>, Place
    {

    }


    public interface MyView extends SuspendableView
    {
        void setPresenter(ExtensionPresenter extensionPresenter);

        void setExtensions(List<Extension> extensions);
    }


    private final DispatchAsync dispatcher;
    private final BeanFactory factory;
    private final RevealStrategy revealStrategy;


    @Inject
    public ExtensionPresenter(final EventBus eventBus, final MyView view,
            final MyProxy proxy, final DispatchAsync dispatcher, final BeanFactory factory,
            final RevealStrategy revealStrategy)
    {
        super(eventBus, view, proxy);
        this.dispatcher = dispatcher;
        this.factory = factory;
        this.revealStrategy = revealStrategy;
    }

    @Override
    protected void onBind()
    {
        super.onBind();
        getView().setPresenter(this);
        getEventBus().addHandler(ServerSelectionEvent.TYPE, this);
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

    public void refresh()
    {
        ModelNode fetchExtensions = new ModelNode();
        fetchExtensions.get(OP).set(READ_CHILDREN_RESOURCES_OPERATION);
        fetchExtensions.get(ADDRESS).setEmptyList();
        fetchExtensions.get(CHILD_TYPE).set("extension");
        fetchExtensions.get(RECURSIVE).set(true);

        dispatcher.execute(new DMRAction(fetchExtensions), new SimpleCallback<DMRResponse>()
        {
            @Override
            public void onSuccess(DMRResponse result)
            {
                ModelNode response = result.get();

                List<Property> properties = response.get(RESULT).asPropertyList();
                List<Extension> extensions = new ArrayList<Extension>(properties.size());

                for (Property property : properties)
                {
                    ModelNode model = property.getValue().asObject();
                    Extension extensionBean = factory.extension().as();
                    extensionBean.setName(property.getName());
                    extensionBean.setModule(model.get("module").asString());

                    Property subsystem = model.get("subsystem").asPropertyList().get(0);
                    extensionBean.setSubsystem(subsystem.getName());

                    String major = subsystem.getValue().get("management-major-version").asString();
                    String minor = subsystem.getValue().get("management-minor-version").asString();
                    String micro = subsystem.getValue().get("management-micro-version").asString();

                    extensionBean.setVersion(major+"."+minor+"."+micro);

                    extensions.add(extensionBean);
                }

                getView().setExtensions(extensions);
            }
        });
    }
}
