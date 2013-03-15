/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package org.jboss.mbui.gui.reification.strategy;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import org.jboss.as.console.client.layout.SimpleLayout;
import org.jboss.ballroom.client.widgets.tabs.FakeTabPanel;
import org.jboss.mbui.gui.behaviour.SystemEvent;
import org.jboss.mbui.gui.reification.Context;
import org.jboss.mbui.gui.reification.ContextKey;
import org.jboss.mbui.model.behaviour.Resource;
import org.jboss.mbui.model.behaviour.ResourceType;
import org.jboss.mbui.model.structure.Container;
import org.jboss.mbui.model.structure.InteractionUnit;

import static org.jboss.mbui.model.structure.TemporalOperator.Deactivation;

/**
 * Strategy for a container with temporal operator == Deactivation.
 *
 * @author Harald Pehl
 * @author Heiko Braun
 * @date 11/01/2012
 */
public class DeactivationStrategy implements ReificationStrategy<ReificationWidget>
{

    private EventBus eventBus;


    @Override
    public boolean prepare(InteractionUnit interactionUnit, Context context) {

        eventBus = context.get(ContextKey.EVENTBUS);
        //assert eventBus!=null : "Coordinator bus is required to execute FormStrategy";
        return eventBus!=null;
    }

    @Override
    public ReificationWidget reify(final InteractionUnit interactionUnit, final Context context)
    {

        MyAdapter adapter = null;
        if (interactionUnit != null)
        {
            adapter = new MyAdapter(interactionUnit);
        }
        return adapter;
    }

    @Override
    public boolean appliesTo(final InteractionUnit interactionUnit)
    {
        return (interactionUnit instanceof Container) && (((Container) interactionUnit)
                .getTemporalOperator() == Deactivation);
    }


    class MyAdapter  implements ReificationWidget
    {
        final InteractionUnit interactionUnit;
        private DeckPanel deckPanel;
        private SimpleLayout layout;

        MyAdapter(final InteractionUnit interactionUnit)
        {

            this.interactionUnit = interactionUnit;

            this.deckPanel = new DeckPanel();

            layout = new SimpleLayout();

            layout.setTitle(interactionUnit.getName())
                    .setDescription("TBD")
                    .addContent("", deckPanel);


            // complement model
            //Resource<ResourceType> navigation = new Resource<ResourceType>(NAVIGATION_ID, ResourceType.Navigation);
            Resource<ResourceType> activation = new Resource<ResourceType>(SystemEvent.ACTIVATE_ID, ResourceType.System);

            //getInteractionUnit().setOutputs(navigation);
            getInteractionUnit().setInputs(activation);

        }

        @Override
        public InteractionUnit getInteractionUnit() {
            return interactionUnit;
        }

        @Override
        public void add(final ReificationWidget widget)
        {
            assert deckPanel.getWidgetCount()<2 : "Operator.Deactivation only supports two child units";
            deckPanel.add(widget.asWidget());
        }

        @Override
        public Widget asWidget()
        {
            deckPanel.showWidget(0);

            return this.layout.build();
        }
    }
}
