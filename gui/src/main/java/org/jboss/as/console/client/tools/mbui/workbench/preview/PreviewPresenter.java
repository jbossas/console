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
package org.jboss.as.console.client.tools.mbui.workbench.preview;

import static org.jboss.as.console.client.tools.mbui.workbench.NameTokens.preview;

import java.util.HashMap;
import java.util.Map;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import org.jboss.as.console.client.Console;
import org.jboss.dmr.client.dispatch.DispatchAsync;
import org.jboss.as.console.client.tools.mbui.workbench.ApplicationPresenter;
import org.jboss.as.console.client.tools.mbui.workbench.ReifyEvent;
import org.jboss.as.console.client.tools.mbui.workbench.ResetEvent;
import org.jboss.as.console.client.tools.mbui.workbench.repository.DataSourceSample;
import org.jboss.as.console.client.tools.mbui.workbench.repository.Sample;
import org.jboss.as.console.client.tools.mbui.workbench.repository.SecurityDomainsSample;
import org.jboss.as.console.client.tools.mbui.workbench.repository.TransactionSample;
import org.jboss.gwt.flow.client.Async;
import org.jboss.gwt.flow.client.Control;
import org.jboss.gwt.flow.client.Function;
import org.jboss.gwt.flow.client.Outcome;
import org.jboss.mbui.gui.behaviour.InteractionCoordinator;
import org.jboss.mbui.gui.behaviour.as7.CoreGUIContext;
import org.jboss.mbui.gui.reification.Context;
import org.jboss.mbui.gui.reification.ContextKey;
import org.jboss.mbui.gui.reification.pipeline.BuildUserInterfaceStep;
import org.jboss.mbui.gui.reification.pipeline.ImplicitBehaviourStep;
import org.jboss.mbui.gui.reification.pipeline.IntegrityStep;
import org.jboss.mbui.gui.reification.pipeline.ReificationPipeline;
import org.jboss.mbui.gui.reification.preparation.ReadOperationDescriptions;
import org.jboss.mbui.gui.reification.preparation.ReadResourceDescription;
import org.jboss.mbui.gui.reification.preparation.ReificationPreperation;
import org.jboss.mbui.gui.reification.strategy.ReificationWidget;
import org.jboss.mbui.model.Dialog;

/**
 * @author Harald Pehl
 * @author Heiko Braun
 * @date 10/30/2012
 */
public class PreviewPresenter extends Presenter<PreviewPresenter.MyView, PreviewPresenter.MyProxy>
        implements ReifyEvent.ReifyHandler, ResetEvent.Handler
{
    private Map<String, InteractionCoordinator> coordinators = new HashMap<String, InteractionCoordinator>();
    private String selectedSample = null;
    private DispatchAsync dispatcher;
    private HashMap<String, ReificationWidget> cachedWidgets = new HashMap<String, ReificationWidget>();
    @Inject
    public PreviewPresenter(final EventBus eventBus, final MyView view, final MyProxy proxy,
                            final DispatchAsync dispatcher)
    {
        super(eventBus, view, proxy);
        this.dispatcher = dispatcher;

        // these would be created/stored differently. This is just an example
        final TransactionSample transactionSample = new TransactionSample();
        final DataSourceSample dataSourceSample = new DataSourceSample();
        final SecurityDomainsSample securityDomainsSample = new SecurityDomainsSample();

        // context
        CoreGUIContext statementContext = new CoreGUIContext(
                Console.MODULES.getCurrentSelectedProfile(),
                Console.MODULES.getCurrentUser()
        );

        final InteractionCoordinator txCoordinator = new InteractionCoordinator(transactionSample.getDialog(),
                statementContext);
        final InteractionCoordinator dsCoordinator = new InteractionCoordinator(dataSourceSample.getDialog(),
                statementContext);
        final InteractionCoordinator secCoordinator = new InteractionCoordinator(securityDomainsSample.getDialog(),
                statementContext);

        coordinators.put(transactionSample.getName(), txCoordinator);
        coordinators.put(dataSourceSample.getName(), dsCoordinator);
        coordinators.put(securityDomainsSample.getName(), secCoordinator);
    }

    private InteractionCoordinator getActiveCoordinator()
    {
        if (null == selectedSample)
        {
            throw new RuntimeException("No sample selected (requires reification/onBind)");
        }
        return coordinators.get(selectedSample);
    }

    @Override
    protected void onBind()
    {
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
        // TODO: dialog models would need to be stored for later retrieval in a real world app
        final Sample sample = event.getSample();
        selectedSample = sample.getName();

        if (cachedWidgets.get(selectedSample) == null)
        {
            // top level interaction unit & context
            final Dialog dialog = sample.getDialog();
            final Context context = new Context();

            // build reification pipeline
            Function<Context> prepareContext = new Function<Context>() {
                @Override
                public void execute(Control<Context> control) {
                    context.set(ContextKey.EVENTBUS, getActiveCoordinator().getLocalBus());
                    context.set(ContextKey.COORDINATOR, getActiveCoordinator());
                    context.set(ContextKey.STATEMENTS, getActiveCoordinator().getStatementContext());

                    control.proceed();
                }
            };

            Function<Context> readOperationMetaData = new Function<Context>() {
                @Override
                public void execute(final Control<Context> control) {
                    ReadOperationDescriptions operationMetaData = new ReadOperationDescriptions(dispatcher);
                    operationMetaData.prepareAsync(dialog, context, new ReificationPreperation.Callback()
                    {
                        @Override
                        public void onError(Throwable caught) {
                            Log.error("Reification failed: " + caught.getMessage());
                            control.abort();
                        }

                        @Override
                        public void onSuccess() {
                            Log.info("Successfully retrieved operation meta data");
                            control.proceed();
                        }
                    });
                }
            };

            Function<Context> readResourceMetaData = new Function<Context>() {
                @Override
                public void execute(final Control<Context> control) {
                    ReificationPreperation readResourceDescription = new ReadResourceDescription(dispatcher);
                    readResourceDescription.prepareAsync(dialog, context, new ReificationPreperation.Callback()
                    {
                        @Override
                        public void onSuccess()
                        {
                            Log.info("Successfully retrieved resource meta data");

                            // setup & start the reification pipeline
                            ReificationPipeline pipeline = new ReificationPipeline(
                                    new BuildUserInterfaceStep(),
                                    new ImplicitBehaviourStep(dispatcher),
                                    new IntegrityStep());
                            pipeline.execute(dialog, context);

                            control.proceed();

                        }

                        @Override
                        public void onError(final Throwable caught)
                        {
                            Log.error("Reification failed: " + caught.getMessage());
                            control.abort();
                        }
                    });
                }
            };

            Outcome<Context> outcome = new Outcome<Context>() {
                @Override
                public void onFailure(final Context context) {
                    Window.alert("Reification failed");
                }

                @Override
                public void onSuccess(final Context context) {
                    // show result
                    ReificationWidget widget = context.get(ContextKey.WIDGET);
                    assert widget !=null;


                    cachedWidgets.put(selectedSample, widget);
                    getView().show(widget);
                }
            };

            // execute pipeline
            new Async<Context>().waterfall(
                    context, outcome,
                    prepareContext, readOperationMetaData, readResourceMetaData
            );
        }
        else
        {
            getView().show(cachedWidgets.get(selectedSample));
        }
    }



    // 3.) Retrieve resource meta data
    private void proceedWithResourceDescriptions(final Dialog dialog, final Context context) {

        ReificationPreperation readResourceDescription = new ReadResourceDescription(dispatcher);
        readResourceDescription.prepareAsync(dialog, context, new ReificationPreperation.Callback()
        {
            @Override
            public void onSuccess()
            {

                Log.info("Successfully retrieved resource meta data");

                // setup & start the reification pipeline
                ReificationPipeline pipeline = new ReificationPipeline(
                        new BuildUserInterfaceStep(),
                        new ImplicitBehaviourStep(dispatcher),
                        new IntegrityStep());
                pipeline.execute(dialog, context);

                // show result
                ReificationWidget widget = context.get(ContextKey.WIDGET);
                assert widget !=null;

                cachedWidgets.put(selectedSample, widget);
                getView().show(widget);
            }

            @Override
            public void onError(final Throwable caught)
            {
                Log.error("Reification failed: " + caught.getMessage());
            }
        });
    }

    // in a real this would be wired Presenter.onReset()
    @Override
    public void doReset()
    {
        getActiveCoordinator().onReset();
    }

    public interface MyView extends View
    {
        void show(ReificationWidget interactionUnit);
    }

    @ProxyStandard
    @NameToken(preview)
    public interface MyProxy extends ProxyPlace<PreviewPresenter>
    {
    }
}
