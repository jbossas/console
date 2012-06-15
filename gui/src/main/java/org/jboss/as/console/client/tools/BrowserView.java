package org.jboss.as.console.client.tools;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.gwt.view.client.TreeViewModel;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.widgets.tree.DefaultCellTreeResources;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.ModelType;
import org.jboss.dmr.client.Property;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 6/15/12
 */
public class BrowserView extends SuspendableViewImpl implements BrowserPresenter.MyView {
    private BrowserPresenter presenter;
    private SplitLayoutPanel layout;
    private BrowserTreeModel treeModel;
    private BrowserTree cellTree;
    private SingleSelectionModel<Property> selectionModel;
    private Property root;

    VerticalPanel treeContainer;

    @Override
    public void setPresenter(BrowserPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget createWidget() {

        layout = new SplitLayoutPanel(10);
        treeContainer = new VerticalPanel();
        treeContainer.setStyleName("fill-layout");

        selectionModel = new SingleSelectionModel<Property>();

        ScrollPanel scroll = new ScrollPanel(treeContainer);
        layout.addWest(scroll, 300);
        layout.add(new LayoutPanel());

        return layout;
    }

    @Override
    public void setModel(List<Property> properties) {

        treeContainer.clear();

        if(properties.isEmpty()) return;

        final Property entryPoint = properties.get(12);
        treeModel = new BrowserTreeModel(entryPoint);
        cellTree = new BrowserTree(treeModel, entryPoint);

        treeContainer.add(cellTree);
    }

    class BrowserTreeModel implements TreeViewModel {
        Property rootEntry;

        BrowserTreeModel(Property root) {
            this.rootEntry = root;
        }

        /**
         * Get the {@link NodeInfo} that provides the children
         * of the specified value.
         */
        public <T> NodeInfo<?> getNodeInfo(T value) {

            final ListDataProvider<Property> dataProvider = new ListDataProvider<Property>();

            if (value instanceof Property) {
                Property entry = (Property)value;
                if(entry.getValue().isDefined()
                        && entry.getValue().getType().equals(ModelType.OBJECT))
                {
                    dataProvider.setList(entry.getValue().asPropertyList());
                }

            } else {
                /*setFinish(new Command() {
                    @Override
                    public void execute() {
                        dataProvider.setList(rootEntry.getChildren());
                    }
                });*/
            }

            return new DefaultNodeInfo<Property>(
                    dataProvider,
                    new PropertyCell(),
                    selectionModel,
                    null
            );
        }

        /**
         * Check if the specified value represents a leaf node.
         * Leaf nodes cannot be opened.
         */
        public boolean isLeaf(Object value) {

            boolean isLeaf = false;

            if(value instanceof Property) {
                final Property prop = (Property) value;

                if(prop.getValue().isDefined())
                    System.out.println(prop.getValue().getType() + " ::: "+ prop.getValue());

                /*return prop.getValue().isDefined()
                        && prop.getValue().getType().equals(ModelType.OBJECT)
                        && prop.getValue().asObject().keys().isEmpty();*/

                isLeaf = !hasChildren(prop.getValue());
            }

            return isLeaf;
        }
    }

    class PropertyCell extends AbstractCell<Property> {
        @Override
        public void render(Context context, Property value, SafeHtmlBuilder sb) {
            sb.appendEscaped(value.getName());
        }
    }

    public class BrowserTree extends CellTree {

        public BrowserTree(TreeViewModel treeModel, Property root) {
            super(treeModel, root, new DefaultCellTreeResources());
        }
    }

    private static boolean hasChildren(ModelNode model)
    {
        boolean result = false;
        if(model.getType().equals(ModelType.OBJECT))
        {
            for(String attribute : model.asObject().keys())
            {
                if(model.get(attribute).getType().equals(ModelType.OBJECT))
                {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }
}
