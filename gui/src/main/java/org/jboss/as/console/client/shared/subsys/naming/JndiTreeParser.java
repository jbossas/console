package org.jboss.as.console.client.shared.subsys.naming;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.client.Command;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.TreeViewModel;
import org.jboss.dmr.client.Property;

import java.util.List;
import java.util.Stack;

/**
 * really awkward jndi parsing routine.
 *
 * @author Heiko Braun
 * @date 7/21/11
 */
public class JndiTreeParser {


    private Stack<JndiEntry> stack = new Stack<JndiEntry>();

    private JndiEntry root = new JndiEntry("JNDI");
    private TreeViewModel treeModel = new JndiTreeModel(root);
    private CellTree cellTree = new CellTree(treeModel, "root");
    private static Command finishCmd = null;

    public CellTree parse(List<Property> model) {

        stack.push(root);
        parseJndiTree(model);
        return cellTree;
    }

    private void parseJndiTree(List<Property> siblings) {

        boolean skipped = false;
        for(Property sibling : siblings)
        {
            try {
                List<Property> children = sibling.getValue().asPropertyList();
                skipped = inc(sibling);
                parseJndiTree(children);
            } catch (IllegalArgumentException e) {
                continue;
            }
        }

        dec(skipped);

    }

    private void dec(boolean skipped) {
        if(!skipped)
            stack.pop();

        if(stack.empty())
        {
            assert finishCmd!=null;
            finishCmd.execute();
        }
    }

    private boolean inc(Property sibling) {

        boolean skipped = sibling.getName().equals("children");

        if(!skipped)
        {
            //dump(sibling);
            JndiEntry next = new JndiEntry(sibling.getName());
            stack.peek().getChildren().add(next);
            stack.push(next);
        }

        return skipped;
    }

    private void dump(Property sibling) {
        StringBuffer sb = new StringBuffer();
        for(int i=0; i<stack.size(); i++)
            sb.append("\t");

        sb.append(sibling.getName());
        System.out.println(sb.toString());
    }

    class JndiEntryCell extends AbstractCell<JndiEntry> {
        @Override
        public void render(Context context, JndiEntry value, SafeHtmlBuilder sb) {
            sb.appendEscaped(value.getName());
        }
    }

    class JndiTreeModel implements TreeViewModel {

        private JndiEntry rootEntry;

        JndiTreeModel(JndiEntry root) {
            this.rootEntry = root;
        }

        /**
         * Get the {@link NodeInfo} that provides the children
         * of the specified value.
         */
        public <T> NodeInfo<?> getNodeInfo(T value) {

            final ListDataProvider<JndiEntry> dataProvider = new ListDataProvider<JndiEntry>();

            if(value instanceof JndiEntry)
            {
                JndiEntry entry = (JndiEntry)value;
                dataProvider.setList(entry.getChildren());
            }
            else {
                setFinish(new Command() {
                    @Override
                    public void execute() {
                        dataProvider.setList(rootEntry.getChildren());
                    }
                });
            }

            return new DefaultNodeInfo<JndiEntry>(dataProvider, new JndiEntryCell());
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


    private static void setFinish(Command cmd)
    {
        finishCmd = cmd;
    }

}


