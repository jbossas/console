package org.jboss.mbui.gui.behaviour;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.Event;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;
import org.jboss.mbui.gui.behaviour.as7.ActivationProcedure;
import org.jboss.mbui.gui.behaviour.as7.BehaviourMap;
import org.jboss.mbui.gui.behaviour.as7.NavigationProcedure;
import org.jboss.mbui.gui.behaviour.as7.SelectStatementProcedure;
import org.jboss.mbui.model.Dialog;
import org.jboss.mbui.model.behaviour.Resource;
import org.jboss.mbui.model.behaviour.ResourceType;
import org.jboss.mbui.model.structure.InteractionUnit;
import org.jboss.mbui.model.structure.QName;

import java.util.Map;
import java.util.Set;

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
        StatementEvent.StatementHandler, BehaviourExecution {

    private static final String PROJECT_NAMESPACE = "org.jboss.as";
    final static SystemEvent RESET = new SystemEvent(new QName(PROJECT_NAMESPACE, "reset"));
    final static SystemEvent BIND = new SystemEvent(new QName(PROJECT_NAMESPACE, "bind"));
    final static SystemEvent REVEAL = new SystemEvent(new QName(PROJECT_NAMESPACE, "reveal"));

    // a bus scoped to this coordinator and the associated models
    private EventBus bus;
    private BehaviourMap<Procedure> procedures = new BehaviourMap<Procedure>();
    private Dialog dialog;
    private StatementScope statementScope;
    private final NavigationDelegate navigationDelegate;

    @Inject
    public InteractionCoordinator(Dialog dialog, StatementContext parentContext, NavigationDelegate navigationDelegate) {
        this.dialog = dialog;
        this.bus = new SimpleEventBus();
        this.navigationDelegate = navigationDelegate;
        this.statementScope = new StatementScope(dialog, parentContext);

        // coordinator handles all events except presentation & system events
        bus.addHandler(InteractionEvent.TYPE, this);
        bus.addHandler(NavigationEvent.TYPE, this);
        bus.addHandler(StatementEvent.TYPE, this);

        // global procedures
        procedures.add(new SelectStatementProcedure(this));
        procedures.add(new ActivationProcedure(this));
        procedures.add(new NavigationProcedure(this));
    }

    public StatementScope getStatementScope() {
        return statementScope;
    }

    public EventBus getLocalBus()
    {
        return this.bus;
    }

    /**
     * Procedures of same kind (same ID) can coexist if they can be further distinguished.<br/>
     * A typical example stock procedures (save, load, etc) that are registered for different origins (interaction units).
     *
     * @param procedure
     */
    @Override
    public void addProcedure(Procedure procedure)
    {

        // TODO: verification of behaviour model
        // known behaviour -> accept
        // unknown behaviour -> issue warning

        // provide context
        procedure.setCoordinator(this);
        procedure.setStatementScope(statementScope);

        procedures.add(procedure);
    }

    @Override
    public Map<QName, Set<Procedure>> listProcedures() {
        return procedures.list();
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
        bus.fireEvent(BIND);
    }

    @Override
    public void onReset() {
        bus.fireEvent(RESET);
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

        final Set<Procedure> collection = procedures.get(id);
        Procedure execution = null;

        if(collection!=null)
        {
            for(Procedure consumer : collection) {
                Resource<ResourceType> resource = new Resource<ResourceType>(id, ResourceType.Interaction);
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
        else if(execution.getPrecondition().isMet(getStatementContext(source)))   // guarded
        {
            try {
                execution.getCommand().execute(InteractionCoordinator.this.dialog, event.getPayload());
            } catch (Throwable e) {
                Log.error("Failed to execute procedure "+execution, e);
            }
        }

    }

    /**
     * Resolve the statement context for an interaction unit
     * @param interactionUnitId
     * @return
     */
    private StatementContext getStatementContext(QName interactionUnitId) {
        return statementScope.getContext(interactionUnitId);
    }

    @Override
    public boolean accepts(NavigationEvent event) {
        return true;
    }


    /**
     * Find and activate another IU.
     * Can delegate to another context (i.e. gwtp placemanager) or handle it internally (same dialog)
     *
     * @param event
     */
    @Override
    public void onNavigationEvent(NavigationEvent event) {

        QName source = (QName)event.getSource();
        QName target = event.getTarget();

        InteractionUnit targetUnit = dialog.findUnit(target);
        if(targetUnit!=null)  // local to dialog
        {
            String suffix = target.getSuffix();
            if(suffix !=null) // relative, local (#prev, #next)
            {
                if(NavigationEvent.RELATION.next.equals(suffix))
                {

                }
                else if(NavigationEvent.RELATION.prev.equals(suffix))
                {

                }
            }
            else // absolute, local
            {
                Procedure activateProcedure = procedures.getSingle(ActivationProcedure.ID);
                activateProcedure.getCommand().execute(dialog, targetUnit.getId());
            }
        }
        else // absolute, external
        {
            navigationDelegate.onNavigation(dialog.getId(), target); // TODO: dialog || unit as source?
        }

    }


    @Override
    public boolean accepts(StatementEvent event) {
        return true; // all statement are processed by the coordinator
    }

    @Override
    public void onStatementEvent(StatementEvent event) {

        Log.debug("StatementEvent " + event.getKey() + "=" + event.getValue());

        Procedure stmtProcedure = procedures.getSingle(SelectStatementProcedure.ID);
        stmtProcedure.getCommand().execute(dialog, event);

    }

    @Override
    public void setStatement(QName sourceId, String key, String value) {

        statementScope.setStatement(sourceId, key, value);
    }

    @Override
    public void clearStatement(QName sourceId, String key, String value) {
        statementScope.clearStatement(sourceId, key);
    }
}
