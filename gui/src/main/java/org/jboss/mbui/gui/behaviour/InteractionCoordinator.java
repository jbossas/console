package org.jboss.mbui.gui.behaviour;

import com.google.gwt.core.client.Scheduler;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.Event;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;
import org.jboss.mbui.model.structure.QName;

import java.util.HashMap;
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
public class InteractionCoordinator implements FrameworkContract, InteractionEvent.Handler {

    private static final String PROJECT_NAMESPACE = "org.jboss.as";

    // a bus scoped to this coordinator and the associated models
    private EventBus bus;
    private Map<QName, Procedure> behaviours = new HashMap<QName, Procedure>();

    @Inject
    public InteractionCoordinator() {
        this.bus = new SimpleEventBus();

        // transitions (initiated by the behaviour model)
        bus.addHandler(InteractionEvent.TYPE, this);
    }

    public EventBus getLocalBus()
    {
        return this.bus;
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

    public void registerProcedure(Procedure procedure)
    {
        behaviours.put(procedure.getId(), procedure);
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

    //  ----- Transitions ------

    @Override
    public boolean accepts(InteractionEvent event) {
        return true; // TODO guard clause?
    }

    @Override
    public void onTransitionEvent(final InteractionEvent event) {
        QName trigger = event.getId();
        Object source = event.getSource();

        final Procedure execution = behaviours.get(trigger);

        if(execution!=null && execution.doesMatch(trigger, source))
        {

            Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                @Override
                public void execute() {
                    execution.getCommand().execute(event.getPayload());
                }
            });
        }
        else
        {
            System.out.println("No behaviour for " +event);
        }

    }


}
