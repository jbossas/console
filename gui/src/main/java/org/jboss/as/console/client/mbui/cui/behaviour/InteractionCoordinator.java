package org.jboss.as.console.client.mbui.cui.behaviour;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;
import org.jboss.as.console.client.mbui.aui.aim.QName;

import java.util.HashMap;
import java.util.Map;

/**
 * A coordinator acts as the middleman between an MVP presenter (framework), the structure (interface model)
 * and the behaviour (interaction model). <p/>
 *
 * It translates framework events (presenter lifecyle) into {@link SystemEvent}'s
 * and realizes the actual business logic as a {@link TransitionEvent.Handler}.
 *
 * @author Heiko Braun
 * @date 11/15/12
 */
public class InteractionCoordinator implements FrameworkContract, TransitionEvent.Handler {

    // a bus scoped to this coordinator and the associated models
    private EventBus bus;
    private Map<QName, BehaviourExecution> behaviours = new HashMap<QName, BehaviourExecution>();

    @Inject
    public InteractionCoordinator() {
        this.bus = new SimpleEventBus();

        // transitions (initiated by the behaviour model)
        bus.addHandler(TransitionEvent.TYPE, this);
    }

    public EventBus getLocalBus()
    {
        return this.bus;
    }

    public void perform(BehaviourExecution execution)
    {
        behaviours.put(execution.getTriggerId(), execution);
    }

    //  ----- System events ------
    @Override
    public void onBind() {
        bus.fireEvent(new SystemEvent(SystemEvent.Kind.BIND));
    }

    @Override
    public void onReveal() {
        bus.fireEvent(new SystemEvent(SystemEvent.Kind.REVEAL));

    }

    @Override
    public void onReset() {
        bus.fireEvent(new SystemEvent(SystemEvent.Kind.RESET));
    }

    //  ----- Transitions ------

    @Override
    public boolean accepts(TransitionEvent.Kind kind) {
        return (kind==TransitionEvent.Kind.FUNCTION_CALL);
    }

    @Override
    public void onTransitionEvent(TransitionEvent event) {
        QName trigger = event.getId();
        Object source = event.getSource();

        BehaviourExecution execution = behaviours.get(trigger);

        if(execution!=null && execution.doesMatch(trigger, source))
            execution.getCommand().execute();
        else
            System.out.println("No behaviour for " +event);

    }


}
