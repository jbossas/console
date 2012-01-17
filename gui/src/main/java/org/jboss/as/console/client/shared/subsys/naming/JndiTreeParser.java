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

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.client.Command;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.gwt.view.client.TreeViewModel;
import org.jboss.as.console.client.widgets.tree.DefaultCellTree;
import org.jboss.dmr.client.Property;

import java.util.List;
import java.util.Stack;

/**
 * really awkward jndi parsing routine.
 *
 * @author Heiko Braun
 * @author David Bosschaert
 * @date 7/21/11
 */
public class JndiTreeParser {
    private Stack<JndiEntry> stack = new Stack<JndiEntry>();
    private JndiEntry root = new JndiEntry("JNDI", "", null);
    private TreeViewModel treeModel = new JndiTreeModel(root);
    private CellTree cellTree = new DefaultCellTree(treeModel, "root");
    private static Command finishCmd = null;
    private SingleSelectionModel<JndiEntry> selectionModel = new SingleSelectionModel<JndiEntry>();

    SingleSelectionModel<JndiEntry> getSelectionModel() {
        return selectionModel;
    }

    public CellTree parse(List<Property> model) {
        stack.push(root);
        parseSiblings(model, "");
        return cellTree;
    }

    private void parseSiblings(List<Property> siblings, String parentURI) {
        boolean skipped = false;
        for (Property sibling : siblings) {
            try {
                List<Property> children = sibling.getValue().asPropertyList();
                skipped = createChild(sibling, parentURI);
                parseSiblings(children, skipped ? parentURI : parentURI + "/" + sibling.getName());
            } catch (IllegalArgumentException e) {
                continue;
            }
        }

        dec(skipped);
    }

    private void dec(boolean skipped) {
        if (!skipped)
            stack.pop();

        if (stack.empty()) {
            assert finishCmd!=null;
            finishCmd.execute();
        }
    }

    /**
     * create actual children
     *
     * @param sibling
     * @return
     */
    private boolean createChild(Property sibling, String parentURI) {
        boolean skipped = sibling.getName().equals("children");

        if (!skipped) {
            //dump(sibling);
            String dataType = null;
            String uri = "";
            if (sibling.getValue().hasDefined("class-name")) {
                dataType = sibling.getValue().get("class-name").asString();
                uri = parentURI + "/" + sibling.getName();

                int idx = uri.indexOf(':');
                if (idx > 0) {
                    int idx2 = uri.lastIndexOf('/', idx);
                    if (idx2 >= 0 && (idx2 + 1) < uri.length())
                        uri = uri.substring(idx2 + 1);
                }
            }

            JndiEntry next = new JndiEntry(sibling.getName(), uri, dataType);
            if (sibling.getValue().hasDefined("value"))
                next.setValue(sibling.getValue().get("value").asString());

            stack.peek().getChildren().add(next);
            stack.push(next);
        }

        return skipped;
    }

    /*
    private void dump(Property sibling) {
        StringBuffer sb = new StringBuffer();
        for (int i=0; i<stack.size(); i++)
            sb.append("\t");

        sb.append(sibling.getName());
        System.out.println(sb.toString());
    }
    */

    class JndiEntryCell extends AbstractCell<JndiEntry> {
        @Override
        public void render(Context context, JndiEntry value, SafeHtmlBuilder sb) {
            sb.appendHtmlConstant("<table width='100%'>");
            sb.appendHtmlConstant("<tr>");
                sb.appendHtmlConstant("<td width='60%'>");
                sb.appendEscaped(value.getName());
                sb.appendHtmlConstant("</td>");

                sb.appendHtmlConstant("<td width='40%' align='right'>");
                sb.appendEscaped(value.getValue());
                sb.appendHtmlConstant("</td>");

            sb.appendHtmlConstant("</tr>");
            sb.appendHtmlConstant("</table>");
        }
    }

    class JndiTreeModel implements TreeViewModel {
        JndiEntry rootEntry;

        JndiTreeModel(JndiEntry root) {
            this.rootEntry = root;
        }

        /**
         * Get the {@link NodeInfo} that provides the children
         * of the specified value.
         */
        public <T> NodeInfo<?> getNodeInfo(T value) {

            final ListDataProvider<JndiEntry> dataProvider = new ListDataProvider<JndiEntry>();

            if (value instanceof JndiEntry) {
                JndiEntry entry = (JndiEntry)value;
                dataProvider.setList(entry.getChildren());
            } else {
                setFinish(new Command() {
                    @Override
                    public void execute() {
                        dataProvider.setList(rootEntry.getChildren());
                    }
                });
            }

            return new DefaultNodeInfo<JndiEntry>(dataProvider, new JndiEntryCell(), selectionModel, null);
        }

        /**
         * Check if the specified value represents a leaf node.
         * Leaf nodes cannot be opened.
         */
        public boolean isLeaf(Object value) {

            if(value instanceof JndiEntry)
                return ((JndiEntry)value).getChildren().isEmpty();
            else
                return false;
        }
    }


    private static void setFinish(Command cmd) {
        finishCmd = cmd;
    }
}


