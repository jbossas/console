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

package org.jboss.as.console.client.domain.groups;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.UseGatekeeper;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.DomainGateKeeper;
import org.jboss.as.console.client.core.Header;
import org.jboss.as.console.client.core.MainLayoutPresenter;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.domain.events.StaleModelEvent;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.domain.model.ServerGroupStore;
import org.jboss.as.console.client.domain.model.SimpleCallback;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 2/28/11
 */
public class ServerGroupMgmtPresenter
        extends Presenter<ServerGroupMgmtPresenter.MyView, ServerGroupMgmtPresenter.MyProxy>
        implements StaleModelEvent.StaleModelListener{

    private final PlaceManager placeManager;
    private ServerGroupStore serverGroupStore;
    private boolean hasBeenRevealed;
    private Header header;

    @ProxyCodeSplit
    @NameToken(NameTokens.ServerGroupMgmtPresenter)
    @UseGatekeeper( DomainGateKeeper.class )
    public interface MyProxy extends Proxy<ServerGroupMgmtPresenter>, Place {

    }

    public interface MyView extends View {
        void setPresenter(ServerGroupMgmtPresenter presenter);

        void updateServerGroups(List<ServerGroupRecord> serverGroupRecords);
    }

    @ContentSlot
    public static final GwtEvent.Type<RevealContentHandler<?>> TYPE_MainContent = new GwtEvent.Type<RevealContentHandler<?>>();

    @Inject
    public ServerGroupMgmtPresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager,
            ServerGroupStore serverGroupStore, Header header) {
        super(eventBus, view, proxy);

        this.placeManager = placeManager;
        this.serverGroupStore = serverGroupStore;
        this.header = header;
    }

    @Override
    protected void onBind() {
        super.onBind();
        getEventBus().addHandler(StaleModelEvent.TYPE, this);
        getView().setPresenter(this);
    }

    @Override
    protected void onReveal() {
        super.onReveal();

        PlaceRequest currentPlaceRequest = placeManager.getCurrentPlaceRequest();
        if(!hasBeenRevealed &&
                NameTokens.ServerGroupMgmtPresenter.equals(currentPlaceRequest.getNameToken()))
        {
            hasBeenRevealed = true;

            // update LHS
            serverGroupStore.loadServerGroups(new SimpleCallback<List<ServerGroupRecord>>() {
                @Override
                public void onSuccess(List<ServerGroupRecord> result) {
                    getView().updateServerGroups(result);
                }
            });

            // forward default to ServerGroupPresenter, this presenter doesn't have a default view
            placeManager.revealRelativePlace(
                    new PlaceRequest(NameTokens.ServerGroupPresenter)
            );
        }
    }

    @Override
    public void prepareFromRequest(PlaceRequest request) {
        super.prepareFromRequest(request);
    }


    @Override
    protected void onReset() {
        super.onReset();

        header.highlight(NameTokens.ServerGroupMgmtPresenter);
    }

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(getEventBus(), MainLayoutPresenter.TYPE_MainContent, this);
    }

    @Override
    public void onStaleModel(String modelName) {
        if(StaleModelEvent.SERVER_GROUPS.equals(modelName))
        {
            serverGroupStore.loadServerGroups(new SimpleCallback<List<ServerGroupRecord>>() {
                @Override
                public void onSuccess(List<ServerGroupRecord> result) {
                    getView().updateServerGroups(result);
                }
            });
        }
    }
}
