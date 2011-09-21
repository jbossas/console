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
package org.jboss.as.console.client.shared.subsys.ejb;

import com.google.gwt.event.shared.EventBus;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.proxy.Proxy;

import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.RevealStrategy;
import org.jboss.as.console.client.shared.subsys.ejb.model.EJBCommonModel;
import org.jboss.dmr.client.ModelDescriptionConstants;
import org.jboss.dmr.client.ModelNode;

/**
 * @author David Bosschaert
 */
public abstract class EJBPresenterBase<E extends EJBCommonModel, V extends EJBViewBase<E>, P extends Proxy<?>> extends Presenter<V, P> {
    public static final String SUBSYSTEM_NAME = "ejb3";

    protected final DispatchAsync dispatcher;
    protected final BeanFactory factory;
    private final RevealStrategy revealStrategy;

    public EJBPresenterBase(EventBus eventBus, V view, P proxy, RevealStrategy revealStrategy,
            DispatchAsync dispatcher, BeanFactory factory) {
        super(eventBus, view, proxy);

        this.revealStrategy = revealStrategy;
        this.dispatcher = dispatcher;
        this.factory = factory;
    }

    @Override
    protected void revealInParent() {
        revealStrategy.revealInParent(this);
    }

    @Override
    protected void onReset() {
        super.onReset();
        loadDetails();
    }

    protected void loadDetails(final E providerEntity, final String defaultPoolName) {
        ModelNode operation = createOperation(ModelDescriptionConstants.READ_RESOURCE_OPERATION);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = ModelNode.fromBase64(result.getResponseText());
                final ModelNode model = response.get(ModelDescriptionConstants.RESULT);

                providerEntity.setDefaultPool(model.get(defaultPoolName).asString());

                ModelNode pools = model.get("strict-max-bean-instance-pool");
                providerEntity.setAvailablePools(pools.keys());

                getView().setProviderDetails(providerEntity);
            }
        });
    }

    protected ModelNode createOperation(String operator) {
        ModelNode operation = new ModelNode();
        operation.get(ModelDescriptionConstants.OP).set(operator);
        operation.get(ModelDescriptionConstants.ADDRESS).set(Baseadress.get());
        operation.get(ModelDescriptionConstants.ADDRESS).add(ModelDescriptionConstants.SUBSYSTEM, SUBSYSTEM_NAME);
        return operation;
    }

    protected abstract void loadDetails();
}
