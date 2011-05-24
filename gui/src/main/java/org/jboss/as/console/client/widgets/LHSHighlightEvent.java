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

package org.jboss.as.console.client.widgets;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;


/**
 * Fired when LHS navigation is selected.
 * Used to highlight different content sections.
 *
 * @author Heiko Braun
 * @date 2/7/11
 */
public class LHSHighlightEvent extends GwtEvent<LHSHighlightEvent.NavItemSelectionHandler> {

    public static final Type TYPE = new Type<NavItemSelectionHandler>();

    private String treeId, item, category;

    public LHSHighlightEvent(String treeId, String item, String category) {
        super();
        this.treeId = treeId;
        this.item = item;
        this.category = category;
    }

    @Override
    public Type<NavItemSelectionHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(NavItemSelectionHandler listener) {
        listener.onSelectedNavTree(treeId, item, category);
    }

    public String getTreeId() {
        return treeId;
    }

    public String getItem() {
        return item;
    }

    public String getCategory() {
        return category;
    }

    public interface NavItemSelectionHandler extends EventHandler {
        void onSelectedNavTree(String treeId, String item, String category);
    }
}


