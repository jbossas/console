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

            final List<ModelNode> path = address.asList();
            final String suffix = path.get(path.size() - 1).asProperty().getValue().asString();
            if(suffix.equals("*"))
            {
                // need to fetch the child types
                presenter.readChildrenNames(address);
            }
        }
    }

    @Override
    public void updateChildrenTypes(ModelNode address, List<ModelNode> modelNodes) {

        System.out.println("Update types "+address);

        TreeItem rootItem = null;

        if(address.asList().isEmpty())
        {
            tree.clear();
            rootItem = new TreeItem("Server Config");
            tree.addItem(rootItem);
        }
        else
        {
            rootItem = findTreeItemForAddress(tree.getItem(0), address);
        }

        addChildrenTypes(rootItem, modelNodes);

    }

    @Override
    public void updateChildrenNames(ModelNode address, List<ModelNode> modelNodes) {

        System.out.println("Update names "+address);

        TreeItem rootItem = findTreeItemForAddress(tree.getItem(0), address);

        addChildrenNames(rootItem, modelNodes);

    }

    private void addChildrenTypes(TreeItem rootItem, List<ModelNode> modelNodes) {
        for(ModelNode child : modelNodes)
        {
            TreeItem childItem = new TreeItem(child.asString());
            childItem.addItem(new PlaceholderItem());
            rootItem.addItem(childItem);
        }
    }

    private void addChildrenNames(TreeItem rootItem, List<ModelNode> modelNodes) {

        rootItem.removeItems();

        for(ModelNode child : modelNodes)
        {
            TreeItem childItem = new TreeItem(child.asString());
            childItem.addItem(new PlaceholderItem());
            rootItem.addItem(childItem);
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

    class PlaceholderItem extends TreeItem {
        PlaceholderItem() {
            super("*");
        }
    }
}
