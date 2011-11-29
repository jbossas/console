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

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.Proxy;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.domain.events.StaleModelEvent;
import org.jboss.as.console.client.shared.subsys.RevealStrategy;


/**
 * The Presenter for Logging
 * @author Stan Silvert ssilvert@redhat.com (C) 2011 Red Hat Inc.
 * @date 10/27/2011
 */
public class LoggingPresenter extends Presenter<LoggingPresenter.MyView, LoggingPresenter.MyProxy> {

    private RevealStrategy revealStrategy;

    @ProxyCodeSplit
    @NameToken(NameTokens.Logger)
    public interface MyProxy extends Proxy<LoggingPresenter>, Place {
    }

    public interface MyView extends View {
        void initialLoad();
        void setPage(int page);
    }

    @Inject
    public LoggingPresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            RevealStrategy revealStrategy) {
        super(eventBus, view, proxy);

        this.revealStrategy = revealStrategy;
    }

    @Override
    public void prepareFromRequest(PlaceRequest request) {

        String page= request.getParameter("page", null);

        if("handler".equals(page))
        {
            getView().setPage(1);
        }
        else
        {
            getView().setPage(0);
        }
    }

    @Override
    protected void onBind() {
        super.onBind();
    }

    @Override
    protected void onReset() {
        super.onReset();
        getView().initialLoad();
    }

    @Override
    protected void revealInParent() {
        revealStrategy.revealInParent(this);
    }
    
}
