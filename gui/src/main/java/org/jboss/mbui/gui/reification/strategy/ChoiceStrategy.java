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
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import org.jboss.as.console.client.widgets.pages.PagedView;
import org.jboss.mbui.gui.behaviour.NavigationEvent;
import org.jboss.mbui.gui.behaviour.SystemEvent;
import org.jboss.mbui.gui.reification.ContextKey;
import org.jboss.mbui.model.behaviour.Resource;
import org.jboss.mbui.model.behaviour.ResourceType;
import org.jboss.mbui.model.structure.Container;
import org.jboss.mbui.model.structure.InteractionUnit;
import org.jboss.mbui.gui.reification.Context;
import org.jboss.as.console.client.widgets.tabs.DefaultTabLayoutPanel;
import org.jboss.mbui.model.structure.QName;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.jboss.mbui.model.structure.TemporalOperator.Choice;

/**
 * Strategy for a container with temporal operator == Choice.
 *
 * @author Harald Pehl
 * @author Heiko Braun
 * @date 11/01/2012
 */
public class ChoiceStrategy implements ReificationStrategy<ReificationWidget>
{
    private EventBus eventBus;

    @Override
    public boolean prepare(InteractionUnit interactionUnit, Context context) {
        eventBus = context.get(ContextKey.EVENTBUS);
        return eventBus !=null;
    }

    @Override
    public ReificationWidget reify(final InteractionUnit interactionUnit, final Context context)
    {
        return new TabPanelAdapter(interactionUnit);
    }

    @Override
    public boolean appliesTo(final InteractionUnit interactionUnit)
    {
        return (interactionUnit instanceof Container) && (((Container) interactionUnit)
                .getTemporalOperator() == Choice);
    }


    class TabPanelAdapter  implements ReificationWidget
    {
        private TabPanelContract delegate;
        private InteractionUnit interactionUnit;
        private Map<Integer, QName> index2tab = new HashMap<Integer, QName>();

        TabPanelAdapter(final InteractionUnit interactionUnit)
        {
            this.interactionUnit = interactionUnit;
            int depth = getNestingDepth(this.interactionUnit);

            switch (depth)
            {
                case 0:
                    this.delegate = createTopLevelTabPanel(interactionUnit, eventBus);
                    break;
                case 1:
                    this.delegate = createPages(interactionUnit, eventBus);
                    break;
                default:
                    this.delegate = createDefaultTabPanel(interactionUnit, eventBus);
                    break;
            }

        }

        private TabPanelContract createTopLevelTabPanel(final InteractionUnit interactionUnit, final EventBus eventBus) {
            final DefaultTabLayoutPanel tabLayoutpanel = new DefaultTabLayoutPanel(40, Style.Unit.PX);
            tabLayoutpanel.addStyleName("default-tabpanel");

            tabLayoutpanel.addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {
                @Override
                public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {

                    QName targetTab = index2tab.get(event.getItem());

                    if(targetTab!=null)
                    {
                        eventBus.fireEventFromSource(
                                new NavigationEvent(
                                        NavigationEvent.ID,
                                        targetTab
                                ), interactionUnit.getId() //source
                        );

                    }
                    event.cancel();
                }
            });

            TabPanelContract tabPanelContract = new TabPanelContract() {
                @Override
                public void add(InteractionUnit unit, Widget widget) {
                    final VerticalPanel vpanel = new VerticalPanel();
                    vpanel.setStyleName("rhs-content-panel");
                    vpanel.add(widget);

                    ScrollPanel scroll = new ScrollPanel(vpanel);
                    tabLayoutpanel.add(scroll, unit.getName());

                    // register tab2index mapping
                    index2tab.put(tabLayoutpanel.getWidgetCount() - 1, unit.getId());
                }

                @Override
                public Widget as() {
                    return tabLayoutpanel;
                }
            };


            // activation listener
            eventBus.addHandler(SystemEvent.TYPE,
                    new SystemEvent.Handler() {
                        @Override
                        public boolean accepts(SystemEvent event) {

                            return event.getId().equals(SystemEvent.ACTIVATE_ID)
                                    && index2tab.containsValue(event.getPayload()
                            );
                        }

                        @Override
                        public void onSystemEvent(SystemEvent event) {
                            QName id = (QName) event.getPayload();

                            Set<Integer> keys = index2tab.keySet();
                            for (Integer key : keys) {
                                if (index2tab.get(key).equals(id)) {
                                    tabLayoutpanel.selectTab(key, false);
                                    break;
                                }
                            }
                        }
                    }
            );


            // complement model
            Resource<ResourceType> navigation = new Resource<ResourceType>(NavigationEvent.ID, ResourceType.Navigation);
            Resource<ResourceType> activation = new Resource<ResourceType>(SystemEvent.ACTIVATE_ID, ResourceType.System);

            getInteractionUnit().setOutputs(navigation);
            getInteractionUnit().setInputs(activation);

            return tabPanelContract;
        }

        private TabPanelContract createPages(InteractionUnit interactionUnit, EventBus eventBus) {
            final PagedView pagedView = new PagedView(true);

            return new TabPanelContract() {
                @Override
                public void add(InteractionUnit unit, Widget widget) {
                    pagedView.addPage(unit.getName(), widget);

                    // register tab2index mapping
                    index2tab.put(pagedView.getPageCount()-1, unit.getId());
                }

                @Override
                public Widget as() {
                    Widget widget = pagedView.asWidget();

                    /*widget.addAttachHandler(new AttachEvent.Handler() {
                        @Override
                        public void onAttachOrDetach(AttachEvent attachEvent) {
                            if(pagedView.getPageCount()>0)
                                pagedView.showPage(0);
                        }
                    });*/

                    pagedView.showPage(0);

                    return widget;
                }
            };
        }

        private TabPanelContract createDefaultTabPanel(InteractionUnit interactionUnit, EventBus eventBus) {
            final TabPanel tabPanel = new TabPanel();
            tabPanel.setStyleName("default-tabpanel");

            tabPanel.addAttachHandler(new AttachEvent.Handler() {
                @Override
                public void onAttachOrDetach(AttachEvent attachEvent) {
                    if(tabPanel.getWidgetCount()>0)
                        tabPanel.selectTab(0);
                }
            });

            return new TabPanelContract() {
                @Override
                public void add(InteractionUnit unit, Widget widget) {
                    tabPanel.add(widget, unit.getName());
                }

                @Override
                public Widget as() {
                    return tabPanel;
                }
            };
        }

        private int getNestingDepth(InteractionUnit unit) {
            int depth = 0;
            InteractionUnit parent = unit.getParent();
            while (parent != null)
            {
                depth++;
                parent = parent.getParent();
            }
            return depth;
        }

        @Override
        public InteractionUnit getInteractionUnit() {
            return interactionUnit;
        }

        @Override
        public void add(final ReificationWidget widget)
        {
            if (widget != null)
            {
                System.out.println("Add "+widget.getInteractionUnit() +" to " + getInteractionUnit());
                delegate.add(widget.getInteractionUnit(), widget.asWidget());
            }
        }

        @Override
        public Widget asWidget()
        {
            return delegate.as();
        }
    }
}
