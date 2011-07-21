package org.jboss.as.console.client.shared.subsys.naming;

import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.cellview.client.TreeNode;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.core.DisposableViewImpl;
import org.jboss.ballroom.client.layout.RHSContentPanel;
import org.jboss.ballroom.client.widgets.ContentHeaderLabel;

/**
 * @author Heiko Braun
 * @date 7/20/11
 */
public class JndiView extends DisposableViewImpl implements JndiPresenter.MyView {

    private JndiPresenter presenter;
    private VerticalPanel container;

    @Override
    public void setPresenter(JndiPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget createWidget() {
        RHSContentPanel layout = new RHSContentPanel("JNDI View");
        container = new VerticalPanel();
        container.setStyleName("fill-layout");

        layout.add(new ContentHeaderLabel("JNDI Bindings"));

        layout.add(container);

        return layout;
    }

    @Override
    public void setJndiTree(CellTree tree) {
        container.clear();
        container.add(tree);

        /*new Visitor(new Applicable() {
            @Override
            public void apply(TreeNode node) {
                System.out.println(node.getValue().getClass());
            }
        }).visit(tree);*/
    }

    interface Applicable
    {
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

                walk((TreeNode) node.setChildOpen(i, true));
            }
        }
    }

}
