package org.jboss.mbui.gui.behaviour;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HasHandlers;
import org.jboss.mbui.model.structure.QName;

/**
 * @author Heiko Braun
 * @date 11/15/12
 */
public class SystemEvent extends GwtEvent<SystemEvent.Handler> {

    public static final Type TYPE = new Type<Handler>();

    public enum Kind {FRAMEWORK, PLATFORM, USER}

    private QName id;
    private Kind kind;
    private Object payload;

    public SystemEvent(QName id, Kind kind) {
        super();
        this.id = id;
        this.kind = kind;
    }

    public QName getId() {
        return id;
    }

    @Override
    public Type<Handler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(Handler listener) {
        if(listener.accepts(kind))
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
