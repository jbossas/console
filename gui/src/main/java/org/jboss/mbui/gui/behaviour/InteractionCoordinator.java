package org.jboss.mbui.gui.behaviour;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.Event;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;
import org.jboss.mbui.model.behaviour.Behaviour;
import org.jboss.mbui.model.behaviour.BehaviourResolution;
import org.jboss.mbui.model.behaviour.Resource;
import org.jboss.mbui.model.behaviour.ResourceType;
import org.jboss.mbui.model.Dialog;
import org.jboss.mbui.model.structure.QName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A coordinator acts as the middleman between a framework (i.e. GWTP), the structure (interface model)
 * and the behaviour (interaction model). <p/>
 *
 * It wires the {@link org.jboss.mbui.model.behaviour.Resource} input/output of interaction units to certain behaviour and vice versa.
 * It's available at reification time to interaction units and provides an API to register {@link Procedure}'s.
 *
 *
 * @author Heiko Braun
 * @date 11/15/12
 */
public class InteractionCoordinator implements FrameworkContract,
        InteractionEvent.InteractionHandler, NavigationEvent.NavigationHandler,
        StatementEvent.StatementHandler {

    private static final String PROJECT_NAMESPACE = "org.jboss.as";

    // a bus scoped to this coordinator and the associated models
    private EventBus bus;
    private Map<QName, List<Procedure>> procedures = new HashMap<QName, List<Procedure>>();
    private Dialog dialog;
    private StatementRegistry statements = new StatementRegistry();
    private StatementContext parentContext;
    private final StatementContext statementContext;

    @Inject
    public InteractionCoordinator(Dialog dialog, StatementContext parentContext) {
        this.dialog = dialog;
        this.bus = new SimpleEventBus();

        // coordinator handles all events except presentation & system events
        bus.addHandler(InteractionEvent.TYPE, this);
        bus.addHandler(NavigationEvent.TYPE, this);
        bus.addHandler(StatementEvent.TYPE, this);

        this.parentContext = parentContext;

        // simple parent delegation mechanism to resolve statement values
        this.statementContext = new StatementContext() {
            @Override
            public String resolve(String key) {
                String resolvedValue = null;

                // child
                resolvedValue = statements.get(key);

                // parent
                if (null == resolvedValue && InteractionCoordinator.this.parentContext != null)
                    resolvedValue = InteractionCoordinator.this.parentContext.resolve(key);

                return resolvedValue;
            }

            @Override
            public String[] resolveTuple(String key) {
                if (InteractionCoordinator.this.parentContext != null)
                    return InteractionCoordinator.this.parentContext.resolveTuple(key);
                return null;
            }
        };
    }

    public StatementContext getStatementContext() {
        return statementContext;
    }

    public EventBus getLocalBus()
    {
        return this.bus;
    }

    public void registerProcedure(Procedure procedure)
    {

        // TODO: verification of behaviour model

        List<Procedure> collection = procedures.get(procedure.getId());
        if(null==collection)
        {
            collection = new ArrayList<Procedure>();
            procedures.put(procedure.getId(), collection);
        }

        // provide context
        procedure.setCoordinator(this);
        procedure.setStatementContext(statementContext);

        collection.add(procedure);
    }


    /**
     * Command entry point
     *
     * @param event
     */
    public void fireEvent(final Event<?> event)
    {
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                bus.fireEventFromSource(event, this);
            }
        });

    }

    //  ----- System events ------
    @Override
    public void onBind() {
        bus.fireEvent(new SystemEvent(new QName(PROJECT_NAMESPACE, "bind")));
    }

    @Override
    public void onReveal() {
        bus.fireEvent(new SystemEvent(new QName(PROJECT_NAMESPACE, "reveal")));

    }

    @Override
    public void onReset() {
        bus.fireEvent(new SystemEvent(new QName(PROJECT_NAMESPACE, "reset")));
    }

    //  ----- Event handling ------

    @Override
    public boolean accepts(InteractionEvent event) {
        return true;
    }

    /**
     * Find the corresponding procedures and execute it.
     *
     * @param event
     */
    @Override
    public void onInteractionEvent(final InteractionEvent event) {
        QName id = event.getId();
        QName source = (QName)event.getSource();

        final List<Procedure> collection = procedures.get(id);
        Procedure execution = null;

        if(collection!=null)
        {
            for(Procedure consumer : collection) {
                Resource<ResourceType> resource = new Resource<ResourceType>(id, ResourceType.Event);
                resource.setSource(source);

                if(consumer.doesConsume(resource))
                {
                    execution = consumer;
                    break;
                }
            }
        }

        if(null==execution)
        {
            Window.alert("No procedure for " + event);
            Log.warn("No procedure for " + event);
        }
        else if(execution.getPrecondition().isMet(statementContext))   // guarded
        {
            try {
                execution.getCommand().execute(InteractionCoordinator.this.dialog, event.getPayload());
            } catch (Throwable e) {
                Log.error("Failed to execute procedure "+execution, e);
            }
        }

    }

    @Override
    public boolean accepts(NavigationEvent event) {
        return true;
    }


    /**
     * Find and activate another IU.
     * Can delegate to another context (gwtp placemanager) or handle it internally (same dialog, i.e. window)
     *
     * @param event
     */
    @Override
    public void onNavigationEvent(NavigationEvent event) {
        // TODO
    }


    @Override
    public boolean accepts(StatementEvent event) {
        return true; // all statement are processed by the coordinator
    }

    @Override
    public void onStatementEvent(StatementEvent event) {

        Log.debug("StatementEvent " + event.getKey() + "=" + event.getValue());

        if(event.getValue()!=null)
            statements.put(event.getKey(), event.getValue());
        else
            statements.remove(event.getKey());

        // when statement change, the system will be reset
        onReset();

        // diagnose
        statements.dump();
    }
}
