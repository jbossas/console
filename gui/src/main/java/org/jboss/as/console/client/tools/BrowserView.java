package org.jboss.as.console.client.tools;

import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SingleSelectionModel;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.Property;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 6/15/12
 */
public class BrowserView extends SuspendableViewImpl implements BrowserPresenter.MyView {
    private BrowserPresenter presenter;
    private SplitLayoutPanel layout;
    private SingleSelectionModel<Property> selectionModel;

    private VerticalPanel treeContainer;
    private RawView rawView;
    private Tree tree;
    @Override
    public void setPresenter(BrowserPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget createWidget() {

        tree = new Tree();
        tree.addSelectionHandler(new SelectionHandler<TreeItem>() {
            @Override
            public void onSelection(SelectionEvent<TreeItem> selection) {
                System.out.println(">> "+selection.getSelectedItem().getText());
            }
        });
        layout = new SplitLayoutPanel(10);
        treeContainer = new VerticalPanel();
        treeContainer.setStyleName("fill-layout");
        treeContainer.add(tree);

        ScrollPanel scroll = new ScrollPanel(treeContainer);
        layout.addWest(scroll, 300);

        rawView = new RawView();

        layout.add(rawView.asWidget());


        tree.addOpenHandler(new OpenHandler<TreeItem>() {
            @Override
            public void onOpen(OpenEvent<TreeItem> event) {
                onItemOpenend(event.getTarget());
            }
        });

        return layout;
    }

    private void onItemOpenend(TreeItem treeItem) {

        // check if it has a placeholder child
        if(treeItem.getChildCount()>0 && (treeItem.getChild(0) instanceof PlaceholderItem))
        {
            final ModelNode address = deriveAddress(treeItem.getChild(0));
            System.out.println(address);

            final List<ModelNode> path = address.asList();
            final String suffix = path.get(path.size() - 1).asProperty().getValue().asString();
            if(suffix.equals("*"))
            {
                // need to fetch the child types
                presenter.loadDescription(address);
            }

        }
    }

    @Override
    public void setDescription(CompositeDescription desc) {

        System.out.println("Update "+desc.getAddress());

        TreeItem rootItem = null;

        if(desc.getAddress().asList().isEmpty())
        {
            tree.clear();
            rootItem = new DescribedTreeItem("Server Config", desc.getDescription());
            tree.addItem(rootItem);
        }
        else
        {
            rootItem = findTreeItemForAddress(tree.getItem(0), desc.getAddress());
        }

        parseChildren(rootItem, desc);

    }

    @Override
    public void setChildTypes(ModelNode address, List<ModelNode> childTypes) {

        TreeItem rootItem = findTreeItemForAddress(tree.getItem(0), address);

        for(ModelNode childType : childTypes)
        {
            rootItem.addItem(childType.asString());
        }
    }

    private TreeItem findTreeItemForAddress(TreeItem root, ModelNode address) {

        TreeItem next = root;
        final List<ModelNode> pathList = address.asList();
        final Iterator<ModelNode> iterator = pathList.iterator();
        boolean matched = false;

        while(!matched && next!=null)
        {
            if(!iterator.hasNext()) break;
            next = traverse(next, iterator.next().asProperty().getName());
        }

        if(next!=null)
            System.out.println("Tree item @ "+address+" : "+next.getText());

        return next;
    }

    private static TreeItem traverse(TreeItem root, String path)
    {
        TreeItem match = null;
        for(int i=0; i<root.getChildCount(); i++)
        {
            TreeItem child = root.getChild(i);
            if(child.getText().equals(path))
            {
                match = child;
                break;
            }
        }

        return match;
    }

    private void parseChildren(TreeItem root, CompositeDescription desc) {

        assert root!=null : "root node cannot be null";

        // parse children

        if(desc.getChildNames().isEmpty()
                && desc.getDescription().hasDefined("children"))
        {
            final List<Property> children = desc.getDescription().get("children").asPropertyList();
            for(Property child : children)
            {
                DescribedTreeItem childItem = new DescribedTreeItem(child.getName());
                childItem.addItem(new PlaceholderItem("*"));
                root.addItem(childItem);
            }
        }
        else
        {
            root.removeItems();
            for(ModelNode childName : desc.getChildNames())
            {
                root.addItem(childName.asString());
            }
        }

    }

    public static ModelNode deriveAddress(TreeItem item)
    {
        int nestinglevel = 0;
        LinkedList<ModelNode> address = new LinkedList<ModelNode>();
        recurseToTop(nestinglevel, item, address);

        return new ModelNode().set(address);
    }

    private static void recurseToTop(int nestingLevel, TreeItem item, LinkedList<ModelNode> address)
    {
        if(item.getParentItem()!=null)
        {
            nestingLevel++;
            if(nestingLevel%2==0)
                address.addFirst(new ModelNode().set(item.getText(), "*"));
            // TODO else statement

            recurseToTop(nestingLevel, item.getParentItem(), address);
        }
    }

    class DescribedTreeItem extends TreeItem
    {
        private ModelNode description;

        DescribedTreeItem(String name) {
            super(name);
        }

        public boolean isDescribed() {
            return this.description!=null;
        }

        public void setDescription(ModelNode description) {
            this.description = description;
        }

        DescribedTreeItem(String name, ModelNode description) {
            super(name);
            this.description = description;
        }

        public ModelNode getDescription() {
            return description;
        }
    }

    class PlaceholderItem extends TreeItem {
        PlaceholderItem(String html) {
            super(html);
        }
    }
}
