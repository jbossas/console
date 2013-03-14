package org.jboss.mbui.gui.reification.strategy;

import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import org.jboss.mbui.gui.reification.Context;
import org.jboss.mbui.gui.reification.ContextKey;
import org.jboss.mbui.model.structure.InteractionUnit;
import org.jboss.mbui.model.structure.as7.ToolStrip;

/**
 * @author Heiko Braun
 * @date 2/26/13
 */
public class ToolStripStrategy implements ReificationStrategy<ReificationWidget> {

    @Override
    public boolean prepare(InteractionUnit interactionUnit, Context context) {
        return true;
    }


    @Override
    public ReificationWidget reify(InteractionUnit interactionUnit, Context context) {
        ToolStripAdapter adapter = null;
        if (interactionUnit != null)
        {
            EventBus eventBus = context.get(ContextKey.EVENTBUS);
            assert eventBus!=null : "Event bus is required to execute ToolStripStrategy";

            adapter = new ToolStripAdapter(interactionUnit, eventBus);
        }
        return adapter;
    }

    @Override
    public boolean appliesTo(InteractionUnit interactionUnit) {
        return interactionUnit instanceof ToolStrip;
    }

    class ToolStripAdapter implements ReificationWidget
    {
        private final InteractionUnit unit;
        private final EventBus eventBus;
        private final org.jboss.ballroom.client.widgets.tools.ToolStrip tools;

        public ToolStripAdapter(InteractionUnit interactionUnit, EventBus eventBus) {
            this.unit = interactionUnit;
            this.eventBus = eventBus;

            this.tools = new org.jboss.ballroom.client.widgets.tools.ToolStrip();
        }

        @Override
        public InteractionUnit getInteractionUnit() {
            return unit;
        }

        @Override
        public void add(ReificationWidget widget) {

            //System.out.println("Add "+ widget.getInteractionUnit() + " to " + unit);
            tools.addToolWidgetRight(widget.asWidget());
        }

        @Override
        public Widget asWidget() {
            return tools.asWidget();
        }
    }
}
