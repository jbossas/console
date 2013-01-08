package org.jboss.as.console.client.mbui.cui.behaviour;

/**
 * @author Heiko Braun
 * @date 11/15/12
 */

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HasHandlers;
import org.jboss.as.console.client.mbui.aui.aim.QName;

/**
 * @author Heiko Braun
 * @date 11/15/12
 */
public class TransitionEvent extends GwtEvent<TransitionEvent.Handler> {

    public static final Type TYPE = new Type<Handler>();

    public enum Kind {FUNCTION_CALL, NAVIGATION}

    private QName id;
    private Kind kind;
    public Object payload;

    public TransitionEvent(QName id, Kind kind) {
        super();
        this.kind = kind;
        this.id = id;
    }

    public QName getId() {
        return id;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }

    public Kind getKind() {
        return kind;
    }

    @Override
    public Type<Handler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(Handler listener) {
        if(listener.accepts(this.kind))
            listener.onTransitionEvent(this);
    }

    public interface Handler extends EventHandler {
        boolean accepts(Kind kind);
        void onTransitionEvent(TransitionEvent event);
    }

    public static void fire(HasHandlers source, TransitionEvent eventInstance) {
        source.fireEvent(eventInstance);
    }

    @Override
    public String toString() {
        return "TransitionEvent{" +
                "kind=" + kind + " => "+getId()+
                ", source=" + getSource() +
                '}';
    }
}

