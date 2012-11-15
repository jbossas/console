package org.jboss.as.console.client.mbui.cui.behaviour;

/**
 * @author Heiko Braun
 * @date 11/15/12
 */

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HasHandlers;

/**
 * @author Heiko Braun
 * @date 11/15/12
 */
public class TransitionEvent extends GwtEvent<TransitionEvent.Handler> {

    public static final Type TYPE = new Type<Handler>();
    private Kind kind;

    public enum Kind {FUNCTION_CALL, NAVIGATION, STATEMENT}

    public Object payload;

    public TransitionEvent(Kind kind) {
        super();
        this.kind = kind;
    }

    public Object getPayload() {
        return payload;
    }

    @Override
    public Type<Handler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(Handler listener) {
        listener.onTransitionEvent(this);
    }

    public interface Handler extends EventHandler {
        boolean accepts(Kind kind);
        void onTransitionEvent(TransitionEvent event);
    }

    public static void fire(HasHandlers source, TransitionEvent eventInstance) {
        source.fireEvent(eventInstance);
    }
}

