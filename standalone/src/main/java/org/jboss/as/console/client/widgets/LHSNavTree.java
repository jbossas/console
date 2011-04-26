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

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.Places;
import org.jboss.as.console.client.widgets.resource.DefaultTreeResources;

/**
 *
 * A tree that's used as a navigation element on the left hand side.<br>
 * It's driven by a token attribute that's associated with the tree item.
 *
 * @see LHSNavTreeItem
 *
 * @author Heiko Braun
 * @date 3/24/11
 */
public class LHSNavTree extends Tree implements LHSHighlightEvent.NavItemSelectionHandler{

    private static final String TREE_ID_ATTRIBUTE = "treeid";

    private String treeId;
    private String category;

    public LHSNavTree(final String category) {
        super(DefaultTreeResources.INSTANCE);

        this.treeId = "lhs-nav-tree_"+HTMLPanel.createUniqueId();
        this.category = category;

        addStyleName("stack-section");

        addSelectionHandler(new SelectionHandler<TreeItem>() {
            @Override
            public void onSelection(SelectionEvent<TreeItem> event) {

                final TreeItem selectedItem = event.getSelectedItem();

                if(selectedItem.getElement().hasAttribute("token"))
                {
                    String token = selectedItem.getElement().getAttribute("token");
                    Console.MODULES.getPlaceManager().revealPlaceHierarchy(
                            Places.fromString(token)
                    );

                }

                // highlight section
                Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand(){
                    @Override
                    public void execute() {
                        Console.MODULES.getEventBus().fireEvent(
                                new LHSHighlightEvent(treeId, selectedItem.getText(), category)
                        );
                    }
                });

            }
        });

        Console.MODULES.getEventBus().addHandler(LHSHighlightEvent.TYPE, this);
    }

    public String getTreeId() {
        return treeId;
    }

    @Override
    public void addItem(TreeItem item) {
        item.getElement().setAttribute(TREE_ID_ATTRIBUTE, treeId);
        super.addItem(item);

    }

    @Override
    public void onSelectedNavTree(String selectedId, final String selectedItem, String selectedCategory) {

        if(category.equals(selectedCategory))
        {
            applyStateChange(new StateChange()
            {
                @Override
                public void applyTo(LHSNavTreeItem treeItem) {
                    boolean isSelected = selectedItem.equals(treeItem.getText());
                    treeItem.setSelected(isSelected);
                }
            });
        }
    }

    void applyStateChange(StateChange stateChange)
    {
        for(int i=0; i<getItemCount(); i++)
        {
            LHSNavTreeItem navItem = (LHSNavTreeItem)getItem(i);
            stateChange.applyTo(navItem);
        }
    }

    interface StateChange {
        void applyTo(LHSNavTreeItem item);
    }
}
