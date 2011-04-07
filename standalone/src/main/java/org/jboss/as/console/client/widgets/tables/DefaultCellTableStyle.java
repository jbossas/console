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

import com.google.gwt.user.cellview.client.CellTable;

public class DefaultCellTableStyle implements CellTable.Style {

    public String cellTableCell() {
        return "cellTableCell";
    }

    @Override
    public String cellTableEvenRow() {
        return "cellTableEvenRow"; 
    }

    @Override
    public String cellTableEvenRowCell() {
        return "cellTableEvenRowCell";
    }

    @Override
    public String cellTableFirstColumn() {
        return "cellTableFirstColumn";
    }

    @Override
    public String cellTableFirstColumnFooter() {
        return "cellTableFirstColumnFooter";
    }

    @Override
    public String cellTableFirstColumnHeader() {
        return "cellTableFirstColumnHeader";
    }

    @Override
    public String cellTableFooter() {
        return "cellTableFooter";
    }

    @Override
    public String cellTableHeader() {
        return "cellTableHeader";
    }

    @Override
    public String cellTableHoveredRow() {
        return "cellTableHoveredRow";
    }

    @Override
    public String cellTableHoveredRowCell() {
        return "cellTableHoveredRowCell";
    }

    @Override
    public String cellTableKeyboardSelectedCell() {
        return "cellTableKeyboardSelectedCell";
    }

    @Override
    public String cellTableKeyboardSelectedRow() {
        return "cellTableKeyboardSelectedRow";
    }

    @Override
    public String cellTableKeyboardSelectedRowCell() {
        return "cellTableKeyboardSelectedRowCell";
    }

    @Override
    public String cellTableLastColumn() {
        return "cellTableLastColumn";
    }

    @Override
    public String cellTableLastColumnFooter() {
        return "cellTableLastColumnFooter";
    }

    @Override
    public String cellTableLastColumnHeader() {
        return "cellTableLastColumnHeader";
    }

    @Override
    public String cellTableLoading() {
        return "cellTableLoading";
    }

    @Override
    public String cellTableOddRow() {
        return "cellTableOddRow";
    }

    @Override
    public String cellTableOddRowCell() {
        return "cellTableOddRowCell";
    }

    @Override
    public String cellTableSelectedRow() {
        return "cellTableSelectedRow";
    }

    @Override
    public String cellTableSelectedRowCell() {
        return "cellTableSelectedRowCell";
    }

    @Override
    public String cellTableSortableHeader() {
        return "cellTableSortableHeader";
    }

    @Override
    public String cellTableSortedHeaderAscending() {
        return "cellTableSortedHeaderAscending";
    }

    @Override
    public String cellTableSortedHeaderDescending() {
        return "cellTableSortedHeaderDescending";
    }

    @Override
    public String cellTableWidget() {
        return "cellTableWidget";
    }

    @Override
    public boolean ensureInjected() {
        return true;
    }

    @Override
    public String getText() {
        return "DefaulCellTableStyle-Text";
    }

    @Override
    public String getName() {
        return "DefaulCellTableStyle";
    }
}