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

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableRowElement;

/**
 * @author Heiko Braun
 * @date 3/1/11
 */
public class TableUtils {

    /**
     * Borrowed from CellTable#onBrowserEvent2(Event event)
     *
     * @param target
     * @return
     */
    public static int identifyRow(Element target)
    {
        // Find the cell where the event occurred.
        TableCellElement tableCell = findNearestParentCell(target);
        if (tableCell == null) {
            return -1;
        }

        // Determine if we are in the header, footer, or body. Its possible that
        // the table has been refreshed before the current event fired (ex. change
        // event refreshes before mouseup fires), so we need to check each parent
        // element.
        Element trElem = tableCell.getParentElement();
        if (trElem == null) {
            return -1;
        }
        TableRowElement tr = TableRowElement.as(trElem);
        Element sectionElem = tr.getParentElement();
        if (sectionElem == null) {
            return -1;
        }

        int row = tr.getSectionRowIndex();
        return row;

    }

    private static TableCellElement findNearestParentCell(Element elem) {
        while ((elem != null) ) {
            String tagName = elem.getTagName();
            if ("td".equalsIgnoreCase(tagName) || "th".equalsIgnoreCase(tagName)) {
                return elem.cast();
            }
            elem = elem.getParentElement();
        }
        return null;
    }
}
