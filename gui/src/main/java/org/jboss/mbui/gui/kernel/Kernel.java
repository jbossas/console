package org.jboss.mbui.gui.kernel;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.tools.mbui.workbench.repository.SampleRepository;
import org.jboss.gwt.flow.client.Async;
import org.jboss.gwt.flow.client.Control;
import org.jboss.gwt.flow.client.Function;
import org.jboss.gwt.flow.client.Outcome;
import org.jboss.mbui.gui.behaviour.InteractionCoordinator;
import org.jboss.mbui.gui.behaviour.NavigationDelegate;
import org.jboss.mbui.gui.behaviour.StatementContext;
import org.jboss.mbui.gui.reification.Context;
import org.jboss.mbui.gui.reification.ContextKey;
import org.jboss.mbui.gui.reification.pipeline.BuildUserInterfaceStep;
import org.jboss.mbui.gui.reification.pipeline.ImplicitBehaviourStep;
import org.jboss.mbui.gui.reification.pipeline.IntegrityStep;
import org.jboss.mbui.gui.reification.pipeline.ReificationPipeline;
import org.jboss.mbui.gui.reification.pipeline.StatementContextStep;
import org.jboss.mbui.gui.reification.pipeline.UniqueIdCheckStep;
import org.jboss.mbui.gui.reification.preparation.ReadOperationDescriptions;
import org.jboss.mbui.gui.reification.preparation.ReadResourceDescription;
import org.jboss.mbui.gui.reification.preparation.ReificationPreperation;
import org.jboss.mbui.gui.reification.strategy.ReificationWidget;
import org.jboss.mbui.model.Dialog;
import org.jboss.mbui.model.structure.QName;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Heiko Braun
 * @date 3/22/13
 */
public class Kernel implements NavigationDelegate {

    private SampleRepository repository;
    private final StatementContext globalContext;

    private Map<String, InteractionCoordinator> coordinators = new HashMap<String, InteractionCoordinator>();
    private Map<String, ReificationWidget> cachedWidgets = new HashMap<String, ReificationWidget>();
    private String activeDialog;
    private final Framework framework;

    public Kernel(DialogRepository repository, Framework framework, StatementContext globalContext) {
        this.repository = (SampleRepository)repository;
        this.globalContext = globalContext;
        this.framework = framework;
    }

    /**
     * Absolute navigation
     * @param source
     * @param dialog
     */
    @Override
    public void onNavigation(QName source, QName dialog) {


    }

    public void reify(final String name, final AsyncCallback<Widget> callback) {


        // passivate current instance before switching
        if(getActiveCoordinator()!=null)
            passivate();

        activeDialog = name;

        if (null == cachedWidgets.get(name) )
        {

            // fetch dialog meta data
            final Dialog dialog  =  repository.getDialog(name);

            // create coordinator instance
            final InteractionCoordinator coordinator = new InteractionCoordinator(
                    dialog, globalContext, this
            );
            coordinators.put(name, coordinator);

            // top level interaction unit & context
            final Context context = new Context();

            // build reification pipeline
            Function<Context> prepareContext = new Function<Context>() {
                @Override
                public void execute(Control<Context> control) {
                    context.set(ContextKey.EVENTBUS, coordinator.getLocalBus());
                    context.set(ContextKey.COORDINATOR, coordinator);

                    control.proceed();
                }
            };

            Function<Context> statementShim = new Function<Context>() {
                @Override
                public void execute(Control<Context> control) {
                    new StatementContextStep().execute(dialog,context);
                    control.proceed();
                }
            };

            Function<Context> readOperationMetaData = new Function<Context>() {
                @Override
                public void execute(final Control<Context> control) {
                    ReadOperationDescriptions operationMetaData = new ReadOperationDescriptions(framework.getDispatcher());
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
                    ReificationPreperation readResourceDescription = new ReadResourceDescription(framework.getDispatcher());
                    readResourceDescription.prepareAsync(dialog, context, new ReificationPreperation.Callback()
                    {
                        @Override
                        public void onSuccess()
                        {
                            Log.info("Successfully retrieved resource meta data");

                            // setup & start the reification pipeline
                            ReificationPipeline pipeline = new ReificationPipeline(
                                    new UniqueIdCheckStep(),
                                    new BuildUserInterfaceStep(),
                                    new ImplicitBehaviourStep(framework.getDispatcher()),
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

                    cachedWidgets.put(name, widget);
                    callback.onSuccess(widget.asWidget());
                }
            };

            // execute pipeline
            new Async<Context>().waterfall(
                    context, outcome,
                    prepareContext, statementShim, readOperationMetaData, readResourceMetaData
            );
        }
        else
        {
            callback.onSuccess(cachedWidgets.get(name).asWidget());
        }

    }

    public void activate() {
        assert activeDialog != null : "Active dialog required";
        getActiveCoordinator().activate();

    }

    public void reset() {
        assert activeDialog != null : "Active dialog required";
        getActiveCoordinator().reset();
    }

    private InteractionCoordinator getActiveCoordinator() {
        return coordinators.get(activeDialog);
    }

    public void passivate() {
        getActiveCoordinator().passivate();
    }
}
