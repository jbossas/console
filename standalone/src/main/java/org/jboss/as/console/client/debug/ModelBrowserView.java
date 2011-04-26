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

package org.jboss.as.console.client.debug;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.widgets.ComboBox;
import org.jboss.as.console.client.widgets.DefaultButton;
import org.jboss.as.console.client.widgets.RHSContentPanel;
import org.jboss.as.console.client.widgets.resource.DefaultTreeResources;
import org.jboss.dmr.client.ModelDescriptionConstants;
import org.jboss.dmr.client.ModelNode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 3/16/11
 */
public class ModelBrowserView extends SuspendableViewImpl implements ModelBrowserPresenter.MyView {

    private ModelBrowserPresenter presenter;
    private Tree tree;

    private TextArea requestArea;
    private TextArea responseArea;

    @Override
    public void setPresenter(ModelBrowserPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget createWidget() {

        LayoutPanel layout = new RHSContentPanel("Model Browser");

        Button btn = new DefaultButton("Refresh Tree");
        btn.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.reloadRootModel();
            }
        });

        layout.add(btn);

        HorizontalPanel horz = new HorizontalPanel();
        horz.setStyleName("fill-layout-width");

        // ---

        VerticalPanel outputPanel = new VerticalPanel();

        ComboBox comboBox = new ComboBox();
        comboBox.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                presenter.setOperation(event.getValue());
            }
        });

        List<String> options = new ArrayList<String>();
        options.add(ModelDescriptionConstants.READ_RESOURCE_OPERATION);
        options.add(ModelDescriptionConstants.READ_RESOURCE_DESCRIPTION_OPERATION);
        options.add(ModelDescriptionConstants.READ_OPERATION_NAMES_OPERATION);
        options.add(ModelDescriptionConstants.READ_RESOURCE_METRICS);
        options.add(ModelDescriptionConstants.READ_CHILDREN_TYPES_OPERATION);
        options.add(ModelDescriptionConstants.READ_CHILDREN_NAMES_OPERATION);

        comboBox.setValues(options);
        comboBox.setItemSelected(0,true);
        outputPanel.add(comboBox.asWidget());

        requestArea = new TextArea();
        requestArea.setCharacterWidth(60);
        requestArea.setVisibleLines(10);

        responseArea = new TextArea();
        responseArea.setCharacterWidth(60);
        responseArea.setVisibleLines(20);

        outputPanel.add(requestArea);
        outputPanel.add(responseArea);

        tree = new Tree(DefaultTreeResources.INSTANCE);
        horz.add(tree);
        horz.add(outputPanel);
        tree.getElement().getParentElement().setAttribute("width", "30%");
        layout.add(horz);

        // ---

        tree.addSelectionHandler(new SelectionHandler<TreeItem>() {
            @Override
            public void onSelection(SelectionEvent<TreeItem> event) {
                requestArea.setText("");
                responseArea.setText("");
                AddressableTreeItem selectedItem = (AddressableTreeItem)event.getSelectedItem();
                presenter.onTreeItemSelection(selectedItem);
            }
        });
        return layout;
    }

    @Override
    public void addItem(TreeItem item) {
        tree.addItem(item);
    }

    @Override
    public void updateItem(String itemName, String base64) {
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
            ModelNode response = ModelNode.fromBase64(base64);
            List<ModelNode> result = response.get("result").asList();
            for(int x=0;x<result.size(); x++) {
                String value = result.get(x).asString();
                match.addItem(new AddressableTreeItem(value, match.getText(), value));
            }
        }

    }

    @Override
    public void updateRequest(String itemName, String json) {
        requestArea.setText(json);
    }

    public void updateResponse(String itemName, String json) {
        responseArea.setText(json);
    }

   /* private String pretty(String json)
    {
        return JSONUtil.pretty(
                JSONUtil.parseJson(json), " "
        );
    }*/

    @Override
    public void clearTree() {
        tree.removeItems();
    }
}
