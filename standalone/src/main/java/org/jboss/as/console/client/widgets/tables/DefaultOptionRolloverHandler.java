/*
 * JBoss, Home of Professional Open Source
 * Copyright <YEAR> Red Hat Inc. and/or its affiliates and other contributors
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

package org.jboss.as.console.client.widgets.tables;

import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.view.client.ListDataProvider;

/**
 * Treats the last cell as an OptionCell and displays it upon row rollover.
 *
 * @author Heiko Braun
 * @date 3/1/11
 */
public class DefaultOptionRolloverHandler implements DefaultCellTable.RowOverHandler {

    final ListDataProvider<?> provider;
    final DefaultCellTable<?> table;

    public DefaultOptionRolloverHandler(ListDataProvider<?> provider, DefaultCellTable<?> table) {
        this.provider = provider;
        this.table = table;
    }

    @Override
    public void onRowOver(int rowNum) {

        if(!table.isEnabled()) return;

        // toggle rollover tools
        if(rowNum<provider.getList().size())
        {
            TableCellElement rollOverItem = table.getRowElement(rowNum).getCells().getItem(2);
            rollOverItem.getFirstChildElement().getFirstChildElement().addClassName("row-tools-enabled");
        }
    }

    @Override
    public void onRowOut(int rowNum) {

        if(!table.isEnabled()) return;

        if(rowNum<provider.getList().size())
        {
            TableCellElement rollOverItem = table.getRowElement(rowNum).getCells().getItem(2);
            rollOverItem.getFirstChildElement().getFirstChildElement().removeClassName("row-tools-enabled");
        }
    }
}