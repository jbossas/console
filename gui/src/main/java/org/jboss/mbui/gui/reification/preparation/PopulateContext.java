package org.jboss.mbui.gui.reification.preparation;

import com.google.web.bindery.event.shared.EventBus;
import org.jboss.mbui.gui.behaviour.InteractionCoordinator;
import org.jboss.mbui.gui.behaviour.StatementContext;
import org.jboss.mbui.gui.reification.Context;
import org.jboss.mbui.gui.reification.ContextKey;
import org.jboss.mbui.model.Dialog;

/**
 * @author Harald Pehl
 * @date 02/22/2013
 */
public class PopulateContext extends ReificationPreperation
{
    final EventBus eventBus;
    final InteractionCoordinator coordinator;
    final StatementContext statementContext;

    public PopulateContext(final EventBus eventBus, final InteractionCoordinator coordinator,
            final StatementContext statementContext)
    {
        super("populate context");
        this.eventBus = eventBus;
        this.coordinator = coordinator;
        this.statementContext = statementContext;
    }

    @Override
    public void prepare(final Dialog dialog, final Context context)
    {
        context.set(ContextKey.EVENTBUS, eventBus);
        context.set(ContextKey.COORDINATOR, coordinator);
        context.set(ContextKey.STATEMENTS, statementContext);
    }

    @Override
    public void prepareAsync(final Dialog dialog, final Context context, final Callback callback)
    {
        throw new UnsupportedOperationException("Only sync preperation is suported");
    }
}
