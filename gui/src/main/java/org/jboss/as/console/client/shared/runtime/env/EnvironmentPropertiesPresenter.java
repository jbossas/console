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

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.Proxy;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.core.SuspendableView;
import org.jboss.as.console.client.domain.model.ServerInstance;
import org.jboss.as.console.client.shared.state.ServerSelectionEvent;
import org.jboss.as.console.client.shared.subsys.RevealStrategy;

/**
 * @author Harald Pehl
 * @date 15/10/12
 */
public class EnvironmentPropertiesPresenter extends Presenter<EnvironmentPropertiesPresenter.MyView,
        EnvironmentPropertiesPresenter.MyProxy>
        implements ServerSelectionEvent.ServerSelectionListener
{
    private final RevealStrategy revealStrategy;


    @ProxyCodeSplit
    @NameToken(NameTokens.EnvironmentPropertiesPresenter)
    public interface MyProxy extends Proxy<EnvironmentPropertiesPresenter>, Place
    {

    }
    public interface MyView extends SuspendableView
    {
        void setPresenter(EnvironmentPropertiesPresenter environmentPropertiesPresenter);
    }


    @Inject
    public EnvironmentPropertiesPresenter(final EventBus eventBus, final MyView view,
            final MyProxy proxy, final RevealStrategy revealStrategy)
    {
        super(eventBus, view, proxy);
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
    protected void onReset()
    {
        super.onReset();
        refreshEnvironmentProperties();
    }

    private void refreshEnvironmentProperties()
    {

    }

    @Override
    protected void revealInParent()
    {
        revealStrategy.revealInRuntimeParent(this);
    }

    @Override
    public void onServerSelection(final String hostName, final ServerInstance server,
            final ServerSelectionEvent.Source source)
    {
    }
}
