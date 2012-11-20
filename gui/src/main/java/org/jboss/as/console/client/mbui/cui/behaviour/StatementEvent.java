package org.jboss.as.console.client.mbui.cui.behaviour;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HasHandlers;
import org.jboss.as.console.client.mbui.aui.aim.QName;

/**
 * @author Heiko Braun
 * @date 11/15/12
 */
public class StatementEvent extends GwtEvent<StatementEvent.Handler> {

    public static final Type TYPE = new Type<Handler>();

    public enum Kind {UPDATE}

    private QName id;
    private Kind kind;
    private Object payload;

    public StatementEvent(QName id, Kind kind) {
        super();
        this.id = id;
        this.kind = kind;
    }

    public Kind getKind() {
        return kind;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
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
            listener.onStatementEvent(this);
    }

    public interface Handler extends EventHandler {
        boolean accepts(Kind kind);
        void onStatementEvent(StatementEvent event);
    }

    public static void fire(HasHandlers source, StatementEvent eventInstance) {
        source.fireEvent(eventInstance);
    }
}
