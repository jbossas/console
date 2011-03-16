package org.jboss.as.console.client.debug;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.*;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.widgets.DefaultButton;
import org.jboss.as.console.client.widgets.RHSContentPanel;
import org.jboss.as.console.client.widgets.resource.DefaultTreeResources;
import org.jboss.dmr.client.ModelNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Heiko Braun
 * @date 3/16/11
 */
public class ModelBrowserView extends SuspendableViewImpl implements ModelBrowserPresenter.MyView {

    private ModelBrowserPresenter presenter;
    private Tree tree;
    private TextArea textArea;

    @Override
    public void setPresenter(ModelBrowserPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget createWidget() {

        LayoutPanel layout = new RHSContentPanel("Model Browser");

        Button btn = new DefaultButton("Request Root Model");
        btn.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.requestRootModel();
            }
        });

        layout.add(btn);

        HorizontalPanel horz = new HorizontalPanel();
        horz.setStyleName("fill-layout-width");

        // ---

        textArea = new TextArea();
        textArea.setCharacterWidth(60);
        textArea.setVisibleLines(30);

        tree = new Tree(DefaultTreeResources.INSTANCE);
        horz.add(tree);
        horz.add(textArea);
        tree.getElement().getParentElement().setAttribute("width", "50%");
        layout.add(horz);

        // ---

        tree.addSelectionHandler(new SelectionHandler<TreeItem>() {
            @Override
            public void onSelection(SelectionEvent<TreeItem> event) {
                textArea.setText("");
                AddressableTreeItem selectedItem = (AddressableTreeItem)event.getSelectedItem();
                presenter.onTreeItemSelection(selectedItem);
            }
        });
        return layout;
    }

    @Override
    public void setRoot(ModelNode modelNode) {
        System.out.println("> "+ modelNode.asString());
    }

    @Override
    public void setRootJson(String json) {

        tree.removeItems();

        JSONObject root = JSONParser.parse(json).isObject();

        Set<String> properties = root.keySet();
        for(String prop : properties)
        {
            final TreeItem item = new AddressableTreeItem(prop, prop);
            tree.addItem(item);
        }
    }

    @Override
    public void updateItem(String itemName, String json) {
        TreeItem match = null;
        for(int i=0; i<tree.getItemCount(); i++)
        {
            TreeItem item = tree.getItem(i);
            if(item.getText().equals(itemName))
            {
                match = item;
                break;
            }
        }

        if(match!=null) // graceful
        {
            match.removeItems();

            JSONObject responseObject = JSONParser.parse(json).isObject();
            JSONArray result = responseObject.get("result").isArray();
            for(int x=0;x<result.size(); x++) {
                String value = result.get(x).isString().stringValue();
                match.addItem(new AddressableTreeItem(value, match.getText(), value));
            }
        }
        tree.setSelectedItem(null);
        match.setState(true);
    }


    public class AddressableTreeItem extends TreeItem
    {
        List<String> address = new ArrayList<String>();
        String title;

        AddressableTreeItem(String title, String... addresses) {
            super(title);
            this.title = title;
            for(String a : addresses)
                address.add(a);
        }

        public List<String> getAddress() {
            return address;
        }

        public boolean isTuple() {
            return address.size() % 2 == 0;
        }

        public String addressString() {
            StringBuilder sb = new StringBuilder();
            for(String s: address)
                sb.append("/").append(s);
            return sb.toString();
        }

    }

    @Override
    public void updateResource(String itemName, String json) {
        textArea.setText(pretty(parseJson(json), " "));
    }

    public static native JavaScriptObject parseJson(String jsonStr) /*-{
	  return eval('(' + jsonStr + ')');
	}-*/;

    public static native String pretty(JavaScriptObject obj, String indent)/*-{

        var result = "";
        if (indent == null) indent = "";

        for (var property in obj)
        {
            var value = obj[property];
            if (typeof value == 'string')
                value = "'" + value + "'";
            else if (typeof value == 'object')
            {
                if (value instanceof Array)
                {
                    // Just let JS convert the Array to a string!
                    value = "[ " + value + " ]";
                }
                else
                {
                    // Recursive dump
                    // (replace "  " by "\t" or something else if you prefer)
                    var od = @org.jboss.as.console.client.debug.ModelBrowserView::pretty(Lcom/google/gwt/core/client/JavaScriptObject;Ljava/lang/String;)(value, indent + "\t");
                    // If you like { on the same line as the key
                    //value = "{\n" + od + "\n" + indent + "}";
                    // If you prefer { and } to be aligned
                    value = "\n" + indent + "{\n" + od + "\n" + indent + "}";
                }
            }
            result += indent + "'" + property + "' : " + value + ",\n";
        }
        return result.replace(/,\n$/, "");

    }-*/;
}
