package org.jboss.as.console.client.tools;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.gwt.view.client.TreeViewModel;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.widgets.tree.DefaultCellTreeResources;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.ModelType;
import org.jboss.dmr.client.Property;

import java.util.List;
import static org.jboss.dmr.client.ModelDescriptionConstants.*;

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
    private RawView rawView;

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

        rawView = new RawView();

        layout.add(rawView.asWidget());

        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent selectionChangeEvent) {
                final Property selection = selectionModel.getSelectedObject();
                if(selection !=null)
                {
                    //if(!hasChildren(selection.getValue()))
                        rawView.display(selection);
                }

            }
        });

        return layout;
    }

    @Override
    public void setModel(List<Property> properties) {

        treeContainer.clear();

        if(properties.isEmpty()) return;

        for(Property prop : properties)
        {
            if(RESULT.equals(prop.getName()))
            {
                treeModel = new BrowserTreeModel(prop);
                cellTree = new BrowserTree(treeModel, prop);

                treeContainer.add(cellTree);
            }
        }
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
                isLeaf = !hasChildren(prop.getValue());

                System.out.println("isLeaf? "+prop.getName()+" "+isLeaf);
            }


            return isLeaf;
        }
    }

    class PropertyCell extends AbstractCell<Property> {
        @Override
        public void render(Context context, Property prop, SafeHtmlBuilder sb) {

            String color = "#000000";
            if(!prop.getValue().isDefined()) color = "#cccccc";
            sb.appendHtmlConstant("<div style='color:"+color+"'>");
            sb.appendEscaped(prop.getName());
            sb.appendHtmlConstant("</div>");
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
