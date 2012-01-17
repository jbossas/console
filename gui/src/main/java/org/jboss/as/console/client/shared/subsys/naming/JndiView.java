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
package org.jboss.as.console.client.shared.subsys.naming;

import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.cellview.client.TreeNode;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.DisposableViewImpl;
import org.jboss.ballroom.client.layout.RHSContentPanel;
import org.jboss.ballroom.client.widgets.ContentHeaderLabel;

/**
 * @author Heiko Braun
 * @author David Bosschaert
 * @date 7/20/11
 */
public class JndiView extends DisposableViewImpl implements JndiPresenter.MyView {
    private static final String SELECTED_URI_PREFIX = "<b>" + Console.CONSTANTS.subsys_naming_selectedURI() + "</b> ";

    private VerticalPanel container;
    private HTML uriLabel;

    @Override
    public void setPresenter(JndiPresenter presenter) {
    }

    @Override
    public Widget createWidget() {
        RHSContentPanel layout = new RHSContentPanel(Console.CONSTANTS.subsys_naming_jndiView());
        container = new VerticalPanel();
        container.setStyleName("fill-layout");

        layout.add(new ContentHeaderLabel(Console.CONSTANTS.subsys_naming_jndiBindings()));

        uriLabel = new HTML(SELECTED_URI_PREFIX, true);
        layout.add(uriLabel);

        layout.add(container);

        return layout;
    }

    @Override
    public void setJndiTree(CellTree tree, final SingleSelectionModel<JndiEntry> selectionModel) {
        container.clear();
        container.add(tree);

        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                JndiEntry entry = selectionModel.getSelectedObject();
                uriLabel.setHTML(SELECTED_URI_PREFIX + entry.getURI());
            }
        });

        // open first element
        TreeNode rootNode = tree.getRootTreeNode();
        TreeNode firstItem = rootNode.setChildOpen(0, true);

    }

    interface Applicable {
        void apply(TreeNode node);
    }

    class Visitor {
        Applicable applicable;

        Visitor(Applicable applicable) {
            this.applicable = applicable;
        }

        void visit(CellTree tree) {
            walk(tree.getRootTreeNode());
        }

        private void walk(TreeNode node) {
            if(null==node)
                return;

            applicable.apply(node);

            for(int i=0; i<node.getChildCount(); i++) {

                walk(node.setChildOpen(i, true));
            }
        }
    }
}
