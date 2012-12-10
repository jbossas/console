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

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.Proxy;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.model.ResponseWrapper;
import org.jboss.as.console.client.shared.subsys.RevealStrategy;
import org.jboss.as.console.client.shared.subsys.infinispan.model.LocalCacheStore;
import org.jboss.as.console.client.shared.viewframework.FrameworkView;



/**
 * The Presenter for Local Caches
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2011 Red Hat Inc.
 */
public class LocalCachePresenter extends Presenter<LocalCachePresenter.MyView, LocalCachePresenter.MyProxy> {

    private RevealStrategy revealStrategy;
    private LocalCacheStore localCacheStore;

    @ProxyCodeSplit
    @NameToken(NameTokens.LocalCachePresenter)
    public interface MyProxy extends Proxy<LocalCachePresenter>, Place {
    }

    public interface MyView extends FrameworkView, View {
        void setPresenter(LocalCachePresenter presenter);
    }

    @Inject
    public LocalCachePresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            LocalCacheStore localCacheStore,
            RevealStrategy revealStrategy) {
        super(eventBus, view, proxy);

        this.revealStrategy = revealStrategy;
        this.localCacheStore = localCacheStore;
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
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

    public void clearCache(final String cacheContainerName, final String cacheName) {
        localCacheStore.clearCache(cacheContainerName, cacheName,
                new SimpleCallback<ResponseWrapper<Boolean>>() {
            @Override
            public void onSuccess(ResponseWrapper<Boolean> response) {
                if (response.getUnderlying())
                    Console.info(Console.MESSAGES.successful(
                            "Clear cache: " + cacheName + " for container: " + cacheContainerName));
                else
                    Console.error(Console.MESSAGES.failed(
                            "Clear cache: " + cacheName + " for container: " + cacheContainerName),
                            response.getResponse().toString());
            }
        });
    }

}
