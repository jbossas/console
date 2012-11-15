package org.jboss.as.console.client.mbui.cui.behaviour;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HasHandlers;

/**
 * @author Heiko Braun
 * @date 11/15/12
 */
public class SystemEvent extends GwtEvent<SystemEvent.Handler> {

    public static final Type TYPE = new Type<Handler>();
    private Kind kind;

    public enum Kind {BIND, RESET, REVEAL}

    private Object payload;

    public SystemEvent(Kind kind) {
        super();
        this.kind = kind;
    }

    @Override
    public Type<Handler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(Handler listener) {
        listener.onSystemEvent(this);
    }

    public interface Handler extends EventHandler {
        boolean accepts(Kind kind);
        void onSystemEvent(SystemEvent event);
    }

    public static void fire(HasHandlers source, SystemEvent eventInstance) {
        source.fireEvent(eventInstance);
    }
}
