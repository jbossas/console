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
package org.jboss.mbui.client.cui.workbench.reification;

import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.mbui.client.aui.aim.Container;
import org.jboss.mbui.client.aui.aim.InteractionUnit;
import org.jboss.mbui.client.cui.Context;
import org.jboss.mbui.client.cui.ReificationStrategy;

import static org.jboss.mbui.client.aui.aim.TemporalOperator.Choice;

/**
 * Strategy for a container with temporal operator == Choice.
 *
 * @author Harald Pehl
 * @date 11/01/2012
 */
public class ChoiceStrategy implements ReificationStrategy<ReificationWidget>
{
    @Override
    public ReificationWidget reify(final InteractionUnit interactionUnit, final Context context)
    {
        TabPanelAdapter adapter = null;
        if (interactionUnit != null)
        {
            adapter = new TabPanelAdapter();
        }
        return adapter;
    }

    @Override
    public boolean appliesTo(final InteractionUnit interactionUnit)
    {
        return (interactionUnit instanceof Container) && (((Container) interactionUnit)
                .getTemporalOperator() == Choice);
    }


    class TabPanelAdapter  implements ReificationWidget
    {
        final TabPanel tabPanel;

        TabPanelAdapter()
        {
            tabPanel = new TabPanel();
            tabPanel.setStyleName("default-tabpanel");

            tabPanel.addAttachHandler(new AttachEvent.Handler() {
                @Override
                public void onAttachOrDetach(AttachEvent attachEvent) {
                    if(tabPanel.getWidgetCount()>0)
                        tabPanel.selectTab(0);
                }
            });
        }

        @Override
        public void add(final ReificationWidget widget, final InteractionUnit interactionUnit,
                final InteractionUnit parent)
        {
            if (widget != null)
            {
                tabPanel.add(widget, interactionUnit.getName());
            }
        }

        @Override
        public Widget asWidget()
        {
            return tabPanel;
        }
    }
}
