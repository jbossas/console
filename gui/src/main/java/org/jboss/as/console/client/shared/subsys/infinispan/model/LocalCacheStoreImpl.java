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

package org.jboss.as.console.client.shared.subsys.infinispan.model;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.jboss.dmr.client.dispatch.DispatchAsync;
import org.jboss.dmr.client.dispatch.impl.DMRAction;
import org.jboss.dmr.client.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.shared.model.ResponseWrapper;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.widgets.forms.AddressBinding;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.as.console.client.widgets.forms.BeanMetaData;
import org.jboss.dmr.client.ModelNode;

import javax.inject.Inject;

import static org.jboss.dmr.client.ModelDescriptionConstants.OP;

/**
 * @author Guillaume Grossetie
 * @date 9/23/12
 */
public class LocalCacheStoreImpl implements LocalCacheStore {

    private DispatchAsync dispatcher;
    private ApplicationMetaData metaData;

    private BeanMetaData cacheMetaData;
    private Baseadress baseadress;

    @Inject
    public LocalCacheStoreImpl(
            DispatchAsync dispatcher,
            ApplicationMetaData propertyMetaData,
            Baseadress baseadress) {
        this.dispatcher = dispatcher;
        this.metaData = propertyMetaData;
        this.baseadress = baseadress;

        this.cacheMetaData = metaData.getBeanMetaData(LocalCache.class);
    }

    @Override
    public void clearCache(String cacheContainerName, String cacheName, final AsyncCallback<ResponseWrapper<Boolean>> callback) {
        AddressBinding address = cacheMetaData.getAddress();
        ModelNode operation = address.asResource(baseadress.getAdress(), cacheContainerName, cacheName);
        operation.get(OP).set("clear");

        dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {

            @Override
            public void onFailure(Throwable throwable) {
                callback.onFailure(throwable);
            }

            @Override
            public void onSuccess(DMRResponse response) {
                ModelNode result = response.get();

                ResponseWrapper<Boolean> wrapped = new ResponseWrapper<Boolean>(
                        !result.isFailure(),result
                );

                callback.onSuccess(wrapped);
            }
        });
    }
}
