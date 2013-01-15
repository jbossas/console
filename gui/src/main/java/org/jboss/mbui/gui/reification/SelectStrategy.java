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
package org.jboss.mbui.gui.reification;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import org.jboss.mbui.model.structure.InteractionUnit;
import org.jboss.mbui.model.structure.Select;
import org.jboss.mbui.model.mapping.MappingType;
import org.jboss.mbui.model.mapping.as7.ResourceAttribute;
import org.jboss.mbui.model.mapping.as7.ResourceMapping;
import org.jboss.mbui.gui.Context;
import org.jboss.mbui.gui.ReificationStrategy;
import org.jboss.mbui.gui.widgets.ModelNodeCellTable;
import org.jboss.ballroom.client.widgets.tables.DefaultPager;

import java.util.List;

/**
 * @author Harald Pehl
 * @date 11/01/2012
 */
public class SelectStrategy implements ReificationStrategy<ReificationWidget>
{
    @Override
    public ReificationWidget reify(final InteractionUnit interactionUnit, final Context context)
    {
        ModelNodeCellTableAdapter adapter = null;
        if (interactionUnit != null)
        {
            adapter = new ModelNodeCellTableAdapter(interactionUnit);
        }
        return adapter;
    }

    @Override
    public boolean appliesTo(final InteractionUnit interactionUnit)
    {
        return interactionUnit instanceof Select;
    }


    class ModelNodeCellTableAdapter implements ReificationWidget
    {
        final VerticalPanel panel;
        final ModelNodeCellTable table;
        final InteractionUnit interactionUnit;

        ModelNodeCellTableAdapter(final InteractionUnit interactionUnit)
        {
            this.panel = new VerticalPanel();
            this.table = new ModelNodeCellTable(5);
            this.interactionUnit = interactionUnit;

            ResourceMapping resourceMapping = (ResourceMapping)
                    this.interactionUnit.getMapping(MappingType.RESOURCE);

            List<ResourceAttribute> attributes = resourceMapping.getAttributes();
            for (ResourceAttribute attribute : attributes)
            {
                final String label = attribute.getLabel() != null ? attribute.getLabel() : attribute.getName();
                table.addColumn(new Column<String, String>(new TextCell())
                {
                    @Override
                    public String getValue(String object)
                    {
                        return object;
                    }
                }, label);
            }

            ListDataProvider<String> dataProvider = new ListDataProvider<String>();
            dataProvider.addDataDisplay(table);

            DefaultPager pager = new DefaultPager();
            pager.setDisplay(table);

            panel.setStyleName("fill-layout-width");
            panel.getElement().setAttribute("style", "padding-top:15px;");
            panel.add(table);
            panel.add(pager);
        }

        @Override
        public void add(final ReificationWidget widget)
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public Widget asWidget()
        {
            return panel;
        }
    }
}
