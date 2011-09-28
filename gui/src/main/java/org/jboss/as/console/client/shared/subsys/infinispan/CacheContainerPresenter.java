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
package org.jboss.as.console.client.shared.subsys.infinispan;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.Proxy;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.subsys.RevealStrategy;
import org.jboss.as.console.client.shared.viewframework.FrameworkView;
import org.jboss.as.console.client.shared.viewframework.SubsystemOpFactory;



/**
 * The Presenter for Deployment Scanners
 * @author Stan Silvert
 * @date 9/15/11
 */
public class CacheContainerPresenter extends Presenter<CacheContainerPresenter.MyView, CacheContainerPresenter.MyProxy> {

    private SubsystemOpFactory opFactory = new SubsystemOpFactory("deployment-scanner", "scanner");
    private final PlaceManager placeManager;
    private RevealStrategy revealStrategy;
    private CacheContainerBridge bridge = (CacheContainerBridge)InfinispanData.CACHE_CONTAINER.getBridge();

    @ProxyCodeSplit
    @NameToken(NameTokens.CacheContainerPresenter)
    public interface MyProxy extends Proxy<CacheContainerPresenter>, Place {
    }

    public interface MyView extends FrameworkView {
    }

    @Inject
    public CacheContainerPresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager, DispatchAsync dispatcher,
            BeanFactory beanFactory, RevealStrategy revealStrategy) {
        super(eventBus, view, proxy);

        this.placeManager = placeManager;
        this.revealStrategy = revealStrategy;
        bridge.setAttributes(InfinispanData.CACHE_CONTAINER.getAttributes());
        bridge.setDispatcher(dispatcher);
        bridge.setView(view);
        bridge.setOpFactory(new SubsystemOpFactory("infinispan", "cache-container"));
        bridge.setBeanFactory(beanFactory);
    }

    @Override
    protected void onBind() {
        super.onBind();
    }

    @Override
    protected void onReset() {
        super.onReset();
        bridge.loadEntities(null);
    }

    @Override
    protected void revealInParent() {
        revealStrategy.revealInParent(this);
    }
    
}
