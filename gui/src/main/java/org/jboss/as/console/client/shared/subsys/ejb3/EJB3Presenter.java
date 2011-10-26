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
package org.jboss.as.console.client.shared.subsys.ejb3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.Proxy;

import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.RevealStrategy;
import org.jboss.as.console.client.shared.subsys.ejb3.model.StrictMaxBeanPool;
import org.jboss.as.console.client.shared.subsys.ejb3.model.ThreadPool;
import org.jboss.as.console.client.shared.viewframework.FrameworkView;
import org.jboss.as.console.client.widgets.forms.AddressBinding;
import org.jboss.as.console.client.widgets.forms.BeanMetaData;
import org.jboss.as.console.client.widgets.forms.PropertyMetaData;
import org.jboss.dmr.client.ModelDescriptionConstants;
import org.jboss.dmr.client.ModelNode;

/**
 * @author David Bosschaert
 */
public class EJB3Presenter extends Presenter<EJB3Presenter.MyView, EJB3Presenter.MyProxy>{
    private final DispatchAsync dispatcher;
    private final RevealStrategy revealStrategy;
    private final BeanMetaData slsbMetaData;

    @ProxyCodeSplit
    @NameToken(NameTokens.EJB3Presenter)
    public interface MyProxy extends Proxy<EJB3Presenter>, Place {
    }

    public interface MyView extends View, FrameworkView {
        void loadBeanPools();
        void loadThreadPools();
        void loadServices();
        void setBeanPoolNames(List<String> poolNames);
        void setPoolTimeoutUnits(Collection<String> units, String defaultUnit);
        void setThreadPoolNames(List<String> threadPoolNames);
        void setPresenter(EJB3Presenter ejb3Presenter);
    }

    @Inject
    public EJB3Presenter(EventBus eventBus, MyView view, MyProxy proxy,
        DispatchAsync dispatcher, PropertyMetaData propertyMetaData, RevealStrategy revealStrategy) {
        super(eventBus, view, proxy);

        this.dispatcher = dispatcher;
        this.revealStrategy = revealStrategy;
        this.slsbMetaData = propertyMetaData.getBeanMetaData(StrictMaxBeanPool.class);
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
    }

    @Override
    protected void onReset() {
        super.onReset();
        loadBeanPoolTimeoutUnits();
    }

    @Override
    protected void revealInParent() {
        revealStrategy.revealInParent(this);
    }

    private void loadBeanPoolTimeoutUnits() {
        AddressBinding address = slsbMetaData.getAddress();
        ModelNode operation = address.asResource(Baseadress.get(), "*");
        operation.get(ModelDescriptionConstants.OP).set(ModelDescriptionConstants.READ_RESOURCE_DESCRIPTION_OPERATION);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = ModelNode.fromBase64(result.getResponseText());
                List<ModelNode> res = response.get(ModelDescriptionConstants.RESULT).asList();
                if (res.size() > 0) {
                    ModelNode attrDesc = res.get(0).get(ModelDescriptionConstants.RESULT,
                            ModelDescriptionConstants.ATTRIBUTES, "timeout-unit");

                    List<String> values = new ArrayList<String>();
                    for (ModelNode allowed : attrDesc.get(ModelDescriptionConstants.ALLOWED).asList()) {
                        values.add(allowed.asString());
                    }
                    String defVal = attrDesc.get(ModelDescriptionConstants.DEFAULT).asString();
                    getView().setPoolTimeoutUnits(values, defVal);
                }

                getView().loadBeanPools();
                getView().loadThreadPools();
            }
        });
    }

    // Invoked once the bean pools have been loaded
    void propagateBeanPoolNames(List<StrictMaxBeanPool> entityList) {
        List<String> poolNames = new ArrayList<String>();
        for (StrictMaxBeanPool bp : entityList) {
            poolNames.add(bp.getName());
        }
        getView().setBeanPoolNames(poolNames);
        getView().initialLoad();
    }

    // Invoked once the thread pools are loaded
    public void propagateThreadPoolNames(List<ThreadPool> entityList) {
        List<String> poolNames = new ArrayList<String>();
        for (ThreadPool tp : entityList) {
            poolNames.add(tp.getName());
        }
        getView().setThreadPoolNames(poolNames);
        getView().loadServices();
    }
}
