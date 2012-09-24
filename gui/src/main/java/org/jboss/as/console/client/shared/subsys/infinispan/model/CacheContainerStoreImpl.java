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

import static org.jboss.dmr.client.ModelDescriptionConstants.OP;

import javax.inject.Inject;

import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.shared.model.ResponseWrapper;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.widgets.forms.AddressBinding;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.as.console.client.widgets.forms.BeanMetaData;
import org.jboss.dmr.client.ModelNode;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author Guillaume Grossetie
 * @date 9/22/12
 */
public class CacheContainerStoreImpl implements CacheContainerStore {

    private DispatchAsync dispatcher;
    private ApplicationMetaData metaData;

    private BeanMetaData cacheContainerMetaData;
    private Baseadress baseadress;

    @Inject
    public CacheContainerStoreImpl(
            DispatchAsync dispatcher,
            ApplicationMetaData propertyMetaData,
            Baseadress baseadress) {
        this.dispatcher = dispatcher;
        this.metaData = propertyMetaData;
        this.baseadress = baseadress;

        this.cacheContainerMetaData = metaData.getBeanMetaData(CacheContainer.class);
    }


    @Override
    public void clearCaches(String cacheContainerName, final AsyncCallback<ResponseWrapper<Boolean>> callback) {
        AddressBinding address = cacheContainerMetaData.getAddress();
        ModelNode operation = address.asResource(baseadress.getAdress(), cacheContainerName);
        operation.get(OP).set("clear-caches");

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
