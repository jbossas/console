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

package org.jboss.as.console.client.core;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;
import com.gwtplatform.mvp.client.proxy.RevealRootLayoutContentEvent;
import org.jboss.as.console.client.domain.events.HostSelectionEvent;
import org.jboss.as.console.client.domain.model.ServerInstance;
import org.jboss.as.console.client.shared.state.CurrentHostSelection;
import org.jboss.as.console.client.shared.state.CurrentServerSelection;
import org.jboss.as.console.client.shared.state.ServerSelectionEvent;
import org.jboss.ballroom.client.util.LoadingOverlay;

/**
 * @author Heiko Braun
 * @date 2/4/11
 */
public class MainLayoutPresenter
        extends Presenter<MainLayoutPresenter.MainLayoutView,
        MainLayoutPresenter.MainLayoutProxy>
        implements ServerSelectionEvent.ServerSelectionListener, HostSelectionEvent.HostSelectionListener   {

    boolean revealDefault = true;
    private BootstrapContext bootstrap;
    private CurrentServerSelection serverSelection;
    private CurrentHostSelection hostSelection;

    public interface MainLayoutView extends View {
    }

    @ContentSlot
    public static final GwtEvent.Type<RevealContentHandler<?>> TYPE_MainContent = new GwtEvent.Type<RevealContentHandler<?>>();

    @ProxyCodeSplit
    @NameToken(NameTokens.mainLayout)
    public interface MainLayoutProxy extends ProxyPlace<MainLayoutPresenter> {}

    @Inject
    public MainLayoutPresenter(
            EventBus eventBus,
            MainLayoutView view,
            MainLayoutProxy proxy, BootstrapContext bootstrap,
            CurrentServerSelection serverSelection, CurrentHostSelection hostSelection) {
        super(eventBus, view, proxy);
        this.bootstrap = bootstrap;
        this.hostSelection = hostSelection;
        this.serverSelection = serverSelection;

    }

    @Override
    protected void onBind() {
        super.onBind();
        getEventBus().addHandler(HostSelectionEvent.TYPE, this);
        getEventBus().addHandler(ServerSelectionEvent.TYPE, this);
    }

    @Override
    public void onHostSelection(String hostName) {
        hostSelection.setName(hostName);
    }

    @Override
    public void onServerSelection(String hostName, ServerInstance server) {
        serverSelection.setHost(hostName);
        serverSelection.setServer(server);
    }

    @Override
    protected void revealInParent() {
        RevealRootLayoutContentEvent.fire(this, this);
    }
}
