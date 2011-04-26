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
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.widgets.LoadingOverlay;

/**
 * @author Heiko Braun
 * @date 2/4/11
 */
public class MainLayoutPresenter
        extends Presenter<MainLayoutPresenter.MainLayoutView,
        MainLayoutPresenter.MainLayoutProxy> {

    boolean revealDefault = true;
    private BootstrapContext bootstrap;

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
            MainLayoutProxy proxy, BootstrapContext bootstrap) {
        super(eventBus, view, proxy);
        this.bootstrap = bootstrap;

    }

    @Override
    public void prepareFromRequest(PlaceRequest request) {
        super.prepareFromRequest(request);
        if(revealDefault && request.getNameToken().equals(NameTokens.mainLayout))
        {
            revealDefault = false;
            Console.MODULES.getPlaceManager().revealPlace(
                    bootstrap.getDefaultPlace()
            );
        }
    }

    @Override
    protected void onReset() {
        super.onReset();
        LoadingOverlay.hide();
    }

    @Override
    protected void revealInParent() {
        RevealRootLayoutContentEvent.fire(this, this);
    }
}
