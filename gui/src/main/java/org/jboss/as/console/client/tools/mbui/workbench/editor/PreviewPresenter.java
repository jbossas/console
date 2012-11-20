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
package org.jboss.as.console.client.tools.mbui.workbench.editor;

import com.allen_sauer.gwt.log.client.Log;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.mbui.aui.aim.InteractionUnit;
import org.jboss.as.console.client.mbui.aui.aim.QName;
import org.jboss.as.console.client.mbui.cui.Context;
import org.jboss.as.console.client.mbui.cui.behaviour.BehaviourExecution;
import org.jboss.as.console.client.mbui.cui.behaviour.DataDrivenCommand;
import org.jboss.as.console.client.mbui.cui.behaviour.InteractionCoordinator;
import org.jboss.as.console.client.mbui.cui.behaviour.StatementEvent;
import org.jboss.as.console.client.mbui.cui.reification.ContextKey;
import org.jboss.as.console.client.mbui.cui.reification.ReificationWidget;
import org.jboss.as.console.client.mbui.cui.reification.pipeline.ReificationPipeline;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.tools.mbui.workbench.ApplicationPresenter;
import org.jboss.as.console.client.tools.mbui.workbench.ReifyEvent;
import org.jboss.as.console.client.tools.mbui.workbench.ResetEvent;
import org.jboss.as.console.client.tools.mbui.workbench.repository.DataSourceSample;
import org.jboss.as.console.client.tools.mbui.workbench.repository.Sample;
import org.jboss.as.console.client.tools.mbui.workbench.repository.TransactionSample;
import org.jboss.dmr.client.ModelNode;

import java.util.HashMap;
import java.util.Map;

import static org.jboss.as.console.client.tools.mbui.workbench.NameTokens.preview;
import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 *
 * @author Harald Pehl
 * @date 10/30/2012
 */
public class PreviewPresenter extends Presenter<PreviewPresenter.MyView, PreviewPresenter.MyProxy>
        implements ReifyEvent.ReifyHandler, ResetEvent.Handler
{

    private Map<String, InteractionCoordinator> coordinators = new HashMap<String, InteractionCoordinator>();
    private String selectedSample = null;
    private final ReificationPipeline reificationPipeline;
    private DispatchAsync dispatcher;

    public interface MyView extends View
    {
        void show(ReificationWidget interactionUnit);
    }

    @ProxyStandard
    @NameToken(preview)
    public interface MyProxy extends ProxyPlace<PreviewPresenter>
    {
    }

    @Inject
    public PreviewPresenter(
            final EventBus eventBus, final MyView view,
            final MyProxy proxy, final ReificationPipeline reificationPipeline,
            DispatchAsync dispatcher)
    {
        super(eventBus, view, proxy);
        this.reificationPipeline = reificationPipeline;
        this.dispatcher = dispatcher;

        final InteractionCoordinator txCoordinator = new InteractionCoordinator();
        final InteractionCoordinator dsCoordinator = new InteractionCoordinator();

        coordinators.put(new TransactionSample().getName(), txCoordinator);
        coordinators.put(new DataSourceSample().getName(), dsCoordinator);

        // setup behaviour hooks

        final QName basicAttributes = new QName("org.jboss.transactions", "basicAttributes");
        BehaviourExecution saveBasicAttributes = new BehaviourExecution(
                new QName("org.jboss.as", "save"),
                basicAttributes,
                new DataDrivenCommand<HashMap>() {
                    @Override
                    public void execute(HashMap payload) {
                        // load tx resource
                        System.out.println("save basic attributes");
                        System.out.println(payload);
                    }
                }
        );

        BehaviourExecution loadBasicAttributes = new BehaviourExecution(
                new QName("org.jboss.as", "load"),
                basicAttributes,
                new DataDrivenCommand() {
                    @Override
                    public void execute(Object payload) {
                        // load tx resource
                        System.out.println("load basic attributes");

                        ModelNode operation = new ModelNode();
                        operation.get(OP).set(READ_RESOURCE_OPERATION);
                        operation.get(ADDRESS).add("profile", "full");
                        operation.get(ADDRESS).add("subsystem", "transactions");
                        operation.get(INCLUDE_RUNTIME).set(true);

                        getDispatcher().execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {

                            // when load is finished update view
                            @Override
                            public void onSuccess(DMRResponse dmrResponse) {
                                ModelNode response = dmrResponse.get();

                                StatementEvent statement = new StatementEvent(
                                        QName.valueOf("org.jboss.as:form-update"),
                                        StatementEvent.Kind.UPDATE
                                );

                                statement.setTarget(basicAttributes);
                                statement.setPayload(response.get(RESULT));

                                txCoordinator.fireEvent(statement);
                            }
                        });
                    }
                }
        );

        txCoordinator.perform(saveBasicAttributes);
        txCoordinator.perform(loadBasicAttributes);
    }

    public DispatchAsync getDispatcher() {
        return dispatcher;
    }

    private InteractionCoordinator getActiveCoordinator()
    {
        if(null==selectedSample)
            throw new RuntimeException("No sample selected (requires reification/onBind)");

        return coordinators.get(selectedSample);
    }

    @Override
    protected void onBind() {
        super.onBind();
        getEventBus().addHandler(ReifyEvent.getType(), this);
        getEventBus().addHandler(ResetEvent.TYPE, this);
    }

    @Override
    protected void revealInParent()
    {
        RevealContentEvent.fire(this, ApplicationPresenter.TYPE_SetMainContent, this);
    }

    // in real this would be wired to Presenter.onBind()
    @Override
    public void onReify(final ReifyEvent event)
    {
        Sample sample = event.getSample();
        selectedSample = sample.getName();

        InteractionUnit interactionUnit = sample.build();
        final Context context = new Context();

        // make the coordinator bus available to the model components
        context.set(ContextKey.COORDINATOR, getActiveCoordinator().getLocalBus());

        reificationPipeline.execute(interactionUnit, context, new SimpleCallback<Boolean>()
        {
            @Override
            public void onSuccess(final Boolean successful)
            {
                if (successful)
                {
                    ReificationWidget widget = context.get(ContextKey.WIDGET);
                    if (widget != null)
                    {
                        getView().show(widget);
                    }
                }
                else
                {
                    Log.error("Reification failed");
                }
            }
        });
    }

    // in a real this would be wired Presenter.onReset()
    @Override
    public void doReset() {
        getActiveCoordinator().onReset();
    }
}
