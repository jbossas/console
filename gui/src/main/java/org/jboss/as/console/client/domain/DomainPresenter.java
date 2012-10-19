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
package org.jboss.as.console.client.domain;

import com.google.gwt.event.shared.GwtEvent;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
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
import org.jboss.as.console.client.core.DomainGateKeeper;
import org.jboss.as.console.client.core.Header;
import org.jboss.as.console.client.core.MainLayoutPresenter;
import org.jboss.as.console.client.core.NameTokens;


/**
 * @author Heiko Braun
 * @date 2/11/11
 */
public class DomainPresenter
        extends Presenter<DomainPresenter.MyView, DomainPresenter.MyProxy>
{
    @ContentSlot
    public static final GwtEvent.Type<RevealContentHandler<?>> TYPE_MainContent =
            new GwtEvent.Type<RevealContentHandler<?>>();

    @ProxyCodeSplit
    @NameToken(NameTokens.DomainPresenter)
    @UseGatekeeper(DomainGateKeeper.class)
    public interface MyProxy extends Proxy<DomainPresenter>, Place
    {
    }


    public interface MyView extends View
    {
    }


    private final PlaceManager placeManager;
    private final Header header;
    private String lastSubPlace;


    private boolean hasBeenRevealed;

    @Inject
    public DomainPresenter(EventBus eventBus, MyView view, MyProxy proxy, PlaceManager placeManager,
            Header header)
    {
        super(eventBus, view, proxy);
        this.placeManager = placeManager;
        this.header = header;
    }

    @Override
    protected void onReset()
    {
        header.highlight(NameTokens.DomainPresenter);

        String currentToken = placeManager.getCurrentPlaceRequest().getNameToken();
        if (!currentToken.equals(getProxy().getNameToken()))
        {
            lastSubPlace = currentToken;
        }
        else if (lastSubPlace != null)
        {
            placeManager.revealPlace(new PlaceRequest(lastSubPlace));
        }

        // first request, select default contents
        if (!hasBeenRevealed && NameTokens.DomainPresenter.equals(currentToken))
        {
            placeManager.revealPlace(new PlaceRequest(NameTokens.Topology));
            hasBeenRevealed = true;
        }
    }

    @Override
    protected void revealInParent()
    {
        RevealContentEvent.fire(this, MainLayoutPresenter.TYPE_MainContent, this);
    }
}
