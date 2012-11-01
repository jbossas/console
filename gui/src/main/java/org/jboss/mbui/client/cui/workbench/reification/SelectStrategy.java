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

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tables.DefaultPager;
import org.jboss.mbui.client.aui.aim.InteractionUnit;
import org.jboss.mbui.client.aui.aim.Select;
import org.jboss.mbui.client.cui.Context;
import org.jboss.mbui.client.cui.ReificationStrategy;

/**
 * @author Harald Pehl
 * @date 11/01/2012
 */
public class SelectStrategy implements ReificationStrategy<ReificationWidget>
{
    @Override
    public ReificationWidget reify(final InteractionUnit interactionUnit, final Context context)
    {
        DefaultCellTableAdapter adapter = null;
        if (interactionUnit != null)
        {
            adapter = new DefaultCellTableAdapter();
        }
        return adapter;
    }

    @Override
    public boolean appliesTo(final InteractionUnit interactionUnit)
    {
        return interactionUnit instanceof Select;
    }


    class DefaultCellTableAdapter implements ReificationWidget
    {
        final VerticalPanel panel;
        final DefaultCellTable<?> table;

        DefaultCellTableAdapter()
        {
            panel = new VerticalPanel();
            table = new DefaultCellTable<Object>(4);
            table.addColumn(new Column<String, String>(new TextCell())
            {
                @Override
                public String getValue(String object)
                {
                    return object;
                }
            }, "Name");

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
        public void add(final ReificationWidget widget, final InteractionUnit interactionUnit,
                final InteractionUnit parent)
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
