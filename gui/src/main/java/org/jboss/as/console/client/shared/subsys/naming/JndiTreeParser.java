package org.jboss.as.console.client.shared.subsys.naming;

import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import org.jboss.dmr.client.Property;

import java.util.List;
import java.util.Stack;

/**
 * @author Heiko Braun
 * @date 7/21/11
 */
public class JndiTreeParser {


    private Tree tree;
    private Stack<TreeItem> stack = new Stack<TreeItem>();

    public Tree parse(List<Property> model) {
        tree = new Tree();
        stack.push(tree.addItem("JNDI"));
        parseJndiTree(model);
        return tree;
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
    }

    private boolean inc(Property sibling) {

        boolean skipped = sibling.getName().equals("children");

        if(!skipped)
        {
            //dump(sibling);
            TreeItem next = stack.peek().addItem(sibling.getName());
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

}
