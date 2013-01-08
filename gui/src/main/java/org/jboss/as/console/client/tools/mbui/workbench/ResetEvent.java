package org.jboss.as.console.client.tools.mbui.workbench;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HasHandlers;

/**
 * @author Heiko Braun
 * @date 11/15/12
 */
public class ResetEvent extends GwtEvent<ResetEvent.Handler> {

    public static final Type TYPE = new Type<Handler>();

    public ResetEvent() {
        super();

    }
    @Override
    public Type<Handler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(Handler listener) {
        listener.doReset();
    }

    public interface Handler extends EventHandler {

        void doReset();
    }

    public static void fire(HasHandlers source, ResetEvent eventInstance) {
        source.fireEvent(eventInstance);
    }
}