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
package org.jboss.as.console.client.shared.subsys.ejb.pool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.Proxy;

import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.shared.dispatch.impl.SimpleDMRResponseHandler;
import org.jboss.as.console.client.shared.model.ModelAdapter;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.RevealStrategy;
import org.jboss.as.console.client.shared.subsys.ejb.pool.model.EJBPool;
import org.jboss.as.console.client.shared.subsys.ejb.pool.wizard.NewPoolWizard;
import org.jboss.as.console.client.widgets.forms.PropertyBinding;
import org.jboss.as.console.client.widgets.forms.PropertyMetaData;
import org.jboss.ballroom.client.widgets.forms.ChoiceItem;
import org.jboss.ballroom.client.widgets.window.DefaultWindow;
import org.jboss.dmr.client.ModelDescriptionConstants;
import org.jboss.dmr.client.ModelNode;

/**
 * @author David Bosschaert
 */
public class BeanPoolsPresenter extends Presenter<BeanPoolsPresenter.MyView, BeanPoolsPresenter.MyProxy> {
    public static final String SUBSYSTEM_NAME = "ejb3";
    public static final String POOL_NAME = "strict-max-bean-instance-pool"; // Currently only 1 pool type exists

    private final DispatchAsync dispatcher;
    private final BeanFactory factory;
    private final PropertyMetaData propertyMetaData;
    private final RevealStrategy revealStrategy;
    private DefaultWindow window;

    @ProxyCodeSplit
    @NameToken(NameTokens.BeanPoolsPresenter)
    public interface MyProxy extends Proxy<BeanPoolsPresenter>, Place {
    }

    public interface MyView extends View {
        void setPresenter(BeanPoolsPresenter presenter);

        void updatePools(List<EJBPool> pools);

        void enablePoolDetails(boolean b);
    }

    @Inject
    public BeanPoolsPresenter(EventBus eventBus, MyView view, MyProxy proxy,
            DispatchAsync dispatcher, BeanFactory factory,  PropertyMetaData propertyMetaData,
            RevealStrategy revealStrategy) {
        super(eventBus, view, proxy);

        this.dispatcher = dispatcher;
        this.factory = factory;
        this.propertyMetaData = propertyMetaData;
        this.revealStrategy = revealStrategy;
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
    }

    @Override
    protected void onReset() {
        super.onReset();
        loadDetails();
    }

    @Override
    protected void revealInParent() {
        revealStrategy.revealInParent(this);
    }

    private void loadDetails() {
        ModelNode operation = createOperation(ModelDescriptionConstants.READ_CHILDREN_RESOURCES_OPERATION);
        operation.get(ModelDescriptionConstants.CHILD_TYPE).set(POOL_NAME);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = ModelNode.fromBase64(result.getResponseText());
                ModelNode model = response.get(ModelDescriptionConstants.RESULT);

                List<EJBPool> pools = new ArrayList<EJBPool>();
                for (String name : model.keys()) {
                    ModelNode details = model.get(name);
                    EJBPool pool = factory.ejbPool().as();
                    pool.setName(details.get("name").asString());
                    pool.setMaxPoolSize(details.get("max-pool-size").asInt()); // TODO Should we obtain the default or is it provided?
                    pool.setTimeout(details.get("timeout").asLong());
                    pool.setTimeoutUnit(details.get("timeout-unit").asString());
                    pools.add(pool);
                }
                getView().updatePools(pools);
            }
        });
    }

    public void launchNewPoolWizard() {
        window = new DefaultWindow("Add Pool");
        window.setWidth(400);
        window.setHeight(300);
        window.setWidget(new NewPoolWizard(this).asWidget());
        window.setGlassEnabled(true);
        window.center();
    }

    public void closeDialogue() {
        if (window != null)
            window.hide();
    }

    public void onAddPool(EJBPool pool) {
        closeDialogue();

        ModelNode operation = createOperation(ModelDescriptionConstants.ADD);
        operation.get(ModelDescriptionConstants.ADDRESS).add(POOL_NAME, pool.getName());
        operation.get("max-pool-size").set(pool.getMaxPoolSize());
        operation.get("timeout").set(pool.getTimeout());
        operation.get("timeout-unit").set(pool.getTimeoutUnit());
        // TODO the above may be done automatically using some utility class that exists I think

        dispatcher.execute(new DMRAction(operation),
            new SimpleDMRResponseHandler(ModelDescriptionConstants.ADD, POOL_NAME, pool.getName(),
                new Command() {
                    @Override
                    public void execute() {
                        loadDetails();
                    }
                }));
    }

    public void onEditPool(EJBPool editedEntity) {
        // The pool attributes aren't editable yet, that needs to be done in the detyped API first
        // getView().enablePoolDetails(true);
    }

    public void onSavePool(String name, Map<String, Object> changedValues) {
        getView().enablePoolDetails(false);
        if (changedValues.size() > 0) {
            ModelNode proto = createOperation(ModelDescriptionConstants.WRITE_ATTRIBUTE_OPERATION);
            proto.get(ModelDescriptionConstants.ADDRESS).add(POOL_NAME, name);

            List<PropertyBinding> bindings = propertyMetaData.getBindingsForType(EJBPool.class);
            ModelNode operation = ModelAdapter.detypedFromChangeset(proto, changedValues, bindings);

            dispatcher.execute(new DMRAction(operation),
                new SimpleDMRResponseHandler(ModelDescriptionConstants.WRITE_ATTRIBUTE_OPERATION, POOL_NAME, name,
                    new Command() {
                        @Override
                        public void execute() {
                            loadDetails();
                        }
                    }));
        }

    }

    public void onDeletePool(String name) {
        ModelNode operation = createOperation(ModelDescriptionConstants.REMOVE);
        operation.get(ModelDescriptionConstants.ADDRESS).add(POOL_NAME, name);

        dispatcher.execute(new DMRAction(operation),
            new SimpleDMRResponseHandler(ModelDescriptionConstants.REMOVE, POOL_NAME, name,
                new Command() {
                    @Override
                    public void execute() {
                        loadDetails();
                    }
                }));
    }

    private ModelNode createOperation(String operator) {
        ModelNode operation = new ModelNode();
        operation.get(ModelDescriptionConstants.OP).set(operator);
        operation.get(ModelDescriptionConstants.ADDRESS).set(Baseadress.get());
        operation.get(ModelDescriptionConstants.ADDRESS).add(ModelDescriptionConstants.SUBSYSTEM, SUBSYSTEM_NAME);
        return operation;
    }

    public void populateTimeoutUnits(final ChoiceItem<String> timeoutItem) {
        ModelNode operation = new ModelNode();
        operation.get(ModelDescriptionConstants.OP).set(ModelDescriptionConstants.READ_RESOURCE_DESCRIPTION_OPERATION);
        operation.get(ModelDescriptionConstants.ADDRESS).set(Baseadress.get());
        operation.get(ModelDescriptionConstants.ADDRESS).add(ModelDescriptionConstants.SUBSYSTEM, BeanPoolsPresenter.SUBSYSTEM_NAME);
        operation.get(ModelDescriptionConstants.ADDRESS).add(BeanPoolsPresenter.POOL_NAME, "*");

        dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {
            @Override
            public void onFailure(Throwable caught) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = ModelNode.fromBase64(result.getResponseText());
                boolean success = response.get(ModelDescriptionConstants.OUTCOME).asString().equals(ModelDescriptionConstants.SUCCESS);
                if (!success) {
                    // TODO log error
                    return;
                }
                List<ModelNode> res = response.get(ModelDescriptionConstants.RESULT).asList();
                if (res.size() > 0) {
                    ModelNode desc = res.get(0).get(ModelDescriptionConstants.RESULT);
                    ModelNode attrs = desc.get("attributes");
                    ModelNode attr = attrs.get("timeout-unit");

                    List<String> values = new ArrayList<String>();
                    values.add(attr.get("allowed").asString()); // TODO cater for STRING-LIST type?
                    String defVal = attr.get("default").asString();

                    timeoutItem.setChoices(values, defVal);
                }
            }
        });
    }
}
