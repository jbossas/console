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

import com.allen_sauer.gwt.log.client.Log;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.tools.mbui.workbench.ApplicationPresenter;
import org.jboss.as.console.client.tools.mbui.workbench.ReifyEvent;
import org.jboss.as.console.client.tools.mbui.workbench.ResetEvent;
import org.jboss.as.console.client.tools.mbui.workbench.repository.DataSourceSample;
import org.jboss.as.console.client.tools.mbui.workbench.repository.Sample;
import org.jboss.as.console.client.tools.mbui.workbench.repository.TransactionSample;
import org.jboss.mbui.gui.behaviour.Integrity;
import org.jboss.mbui.gui.behaviour.InteractionCoordinator;
import org.jboss.mbui.gui.behaviour.as7.CoreGUIContext;
import org.jboss.mbui.gui.behaviour.as7.CoreGUIContract;
import org.jboss.mbui.gui.behaviour.as7.ImplictBehaviour;
import org.jboss.mbui.gui.reification.Context;
import org.jboss.mbui.gui.reification.ReificationPipeline;
import org.jboss.mbui.gui.reification.strategy.ContextKey;
import org.jboss.mbui.gui.reification.strategy.ReificationWidget;
import org.jboss.mbui.model.structure.InteractionUnit;

import java.util.HashMap;
import java.util.Map;

import static org.jboss.as.console.client.tools.mbui.workbench.NameTokens.preview;

/**
 *
 * @author Harald Pehl
 * @author Heiko Braun
 *
 * @date 10/30/2012
 */
public class PreviewPresenter extends Presenter<PreviewPresenter.MyView, PreviewPresenter.MyProxy>
        implements ReifyEvent.ReifyHandler, ResetEvent.Handler
{

    private Map<String, InteractionCoordinator> coordinators = new HashMap<String, InteractionCoordinator>();
    private String selectedSample = null;
    private final ReificationPipeline reificationPipeline;
    private DispatchAsync dispatcher;
    private HashMap<String, ReificationWidget> cachedWidgets = new HashMap<String, ReificationWidget>();

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
            final DispatchAsync dispatcher)
    {
        super(eventBus, view, proxy);
        this.reificationPipeline = reificationPipeline;

        this.dispatcher = dispatcher;

        // these would be created/stored differently. This is just an example
        final TransactionSample transactionSample = new TransactionSample();
        final DataSourceSample dataSourceSample = new DataSourceSample();

        // context
        CoreGUIContext statementContext = new CoreGUIContext(
                Console.MODULES.getCurrentSelectedProfile(),
                Console.MODULES.getCurrentUser()
        );

        final InteractionCoordinator txCoordinator = new InteractionCoordinator(transactionSample.getDialog(), statementContext);
        final InteractionCoordinator dsCoordinator = new InteractionCoordinator(dataSourceSample.getDialog(), statementContext);

        coordinators.put(transactionSample.getName(), txCoordinator);
        coordinators.put(dataSourceSample.getName(), dsCoordinator);

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
        // TODO: dialog models would need to be stored for later retrieval in a real world app
        final Sample sample = event.getSample();
        selectedSample = sample.getName();


        if(cachedWidgets.get(selectedSample)==null)
        {

            // Step1: reification of the structure

            InteractionUnit interactionUnit = sample.getDialog().getInterfaceModel();
            final Context context = new Context();

            // make the coordinator bus available to the model components
            context.set(ContextKey.COORDINATOR, getActiveCoordinator().getLocalBus());
            context.set(ContextKey.STATEMENTS, getActiveCoordinator().getStatementContext());

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
                            cachedWidgets.put(selectedSample, widget);
                            getView().show(widget);

                            // Step 2: Parse model and register default behaviour

                            new ImplictBehaviour(sample.getDialog(), new CoreGUIContract()).register(getActiveCoordinator());

                            // Step 3: Verify integrity

                            //Integrity.check(sample.getDialog().getInterfaceModel(), getActiveCoordinator().listProcedures());

                        }
                    }
                    else
                    {
                        Log.error("Reification failed");
                    }
                }
            });


        }
        else
        {
            getView().show(cachedWidgets.get(selectedSample));
        }

    }

    // in a real this would be wired Presenter.onReset()
    @Override
    public void doReset() {
        getActiveCoordinator().onReset();
    }
}
