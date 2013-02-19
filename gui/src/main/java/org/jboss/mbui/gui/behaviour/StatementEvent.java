package org.jboss.mbui.gui.behaviour;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HasHandlers;
import org.jboss.mbui.model.structure.QName;

/**
 * A statement event alters the global dialog state.
 *
 * @author Heiko Braun
 * @date 11/15/12
 */
public class StatementEvent extends GwtEvent<StatementEvent.StatementHandler> {

    public static final Type TYPE = new Type<StatementHandler>();

    private QName id;
    private final String key;
    private final String value;


    public StatementEvent(QName id, String key, String value) {
        super();
        this.id = id;
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public QName getId() {
        return id;
    }

    @Override
    public Type<StatementHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(StatementHandler listener) {
        if(listener.accepts(this))
            listener.onStatementEvent(this);
    }

    public interface StatementHandler extends EventHandler {
        boolean accepts(StatementEvent event);
        void onStatementEvent(StatementEvent event);
    }

    public static void fire(HasHandlers source, StatementEvent eventInstance) {
        source.fireEvent(eventInstance);
    }
}
