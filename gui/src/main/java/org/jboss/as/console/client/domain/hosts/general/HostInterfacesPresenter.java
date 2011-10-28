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

package org.jboss.as.console.client.domain.hosts.general;

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
import org.jboss.as.console.client.shared.general.model.Interface;
import org.jboss.as.console.client.shared.general.model.LoadInterfacesCmd;
import org.jboss.as.console.client.domain.hosts.CurrentHostSelection;
import org.jboss.as.console.client.domain.hosts.HostMgmtPresenter;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.dmr.client.ModelNode;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 5/18/11
 */
public class HostInterfacesPresenter extends Presenter<HostInterfacesPresenter.MyView, HostInterfacesPresenter.MyProxy> {

    private final PlaceManager placeManager;
    private LoadInterfacesCmd loadInterfacesCmd;
    private DispatchAsync dispatcher;
    private BeanFactory factory;
    private CurrentHostSelection currentHost;
    private ApplicationMetaData metaData;

    @ProxyCodeSplit
    @NameToken(NameTokens.HostInterfacesPresenter)
    public interface MyProxy extends Proxy<HostInterfacesPresenter>, Place {
    }

    public interface MyView extends View {
        void setPresenter(HostInterfacesPresenter presenter);
        void setInterfaces(List<Interface> interfaces);
    }

    @Inject
    public HostInterfacesPresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager, CurrentHostSelection currentHost,
            DispatchAsync dispatcher, BeanFactory factory, ApplicationMetaData metaData
    ) {
        super(eventBus, view, proxy);

        this.placeManager = placeManager;
        this.dispatcher = dispatcher;
        this.factory = factory;
        this.currentHost = currentHost;
        this.metaData = metaData;
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
    }


    @Override
    protected void onReset() {
        super.onReset();
        loadInterfaces();
    }

    private void loadInterfaces() {


        ModelNode address = new ModelNode();
        address.add("host", currentHost.getName());

        LoadInterfacesCmd loadInterfacesCmd = new LoadInterfacesCmd(dispatcher, address, metaData);

        loadInterfacesCmd.execute(new SimpleCallback<List<Interface>>() {
            @Override
            public void onSuccess(List<Interface> result) {
                getView().setInterfaces(result);
            }
        });

    }

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(getEventBus(), HostMgmtPresenter.TYPE_MainContent, this);
    }
}
