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

package org.jboss.as.console.client.system;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import org.jboss.as.console.client.core.MainLayoutPresenter;
import org.jboss.as.console.client.core.NameTokens;

/**
 * @author Heiko Braun
 * @date 1/31/11
 */
public class SystemApplicationPresenter extends Presenter<SystemApplicationPresenter.SystemAppView,
        SystemApplicationPresenter.SystemAppProxy> {

    private EventBus eventBus;

    public interface SystemAppView extends View {
    }

    @ProxyCodeSplit
    @NameToken(NameTokens.systemApp)
    public interface SystemAppProxy extends ProxyPlace<SystemApplicationPresenter> {}

    @Inject
    public SystemApplicationPresenter(EventBus eventBus, SystemAppView view, SystemAppProxy proxy) {
        super(eventBus, view, proxy);
        this.eventBus = eventBus;
    }

    @Override
    protected void revealInParent() {
        // reveal in main layout
        RevealContentEvent.fire(eventBus, MainLayoutPresenter.TYPE_MainContent, this);
    }

}
