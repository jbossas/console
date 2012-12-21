package org.jboss.as.console.client.mbui.cui.behaviour;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HasHandlers;

/**
 * @author Heiko Braun
 * @date 11/15/12
 */
public class InteractionEvent extends GwtEvent<InteractionEvent.Handler> {

    public static final Type TYPE = new Type<Handler>();

    public InteractionEvent() {
        super();
    }

    @Override
    public Type<Handler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(Handler listener) {
        listener.onInteractionEvent(this);
    }

    public interface Handler extends EventHandler {
        void onInteractionEvent(InteractionEvent event);
    }

    public static void fire(HasHandlers source, InteractionEvent eventInstance) {
        source.fireEvent(eventInstance);
    }

}

