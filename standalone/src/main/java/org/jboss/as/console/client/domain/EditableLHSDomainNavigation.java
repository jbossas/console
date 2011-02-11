package org.jboss.as.console.client.domain;

import com.google.gwt.user.client.ui.Widget;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.VisibilityMode;
import com.smartgwt.client.widgets.ImgButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.SectionStack;
import com.smartgwt.client.widgets.layout.SectionStackSection;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeNode;

/**
 * @author Heiko Braun
 * @date 2/11/11
 */
public class EditableLHSDomainNavigation {

    private SectionStack sectionStack;

    public EditableLHSDomainNavigation() {

        final TreeGrid treeGrid = new TreeGrid();
        treeGrid.addStyleName("lhs-treeGrid");
        treeGrid.setTitle("Profiles");
        treeGrid.setLeaveScrollbarGap(false);
        treeGrid.setShowHeader(false);

        TreeNode[] treeNodes = new TreeNode[2];
        final TreeNode treeNode = new TreeNode("default-profile");
        treeNode.setTitle("EE5 Web Profile");
        treeNodes[0] = treeNode;
        final TreeNode treeNode2 = new TreeNode("messaging-profile");
        treeNode2.setTitle("Messaging Profile");
        treeNodes[1] = treeNode2;

        TreeNode rootNode = new TreeNode("profiles" , treeNodes);
        Tree tree = new Tree();
        tree.setRoot(rootNode);
        treeGrid.setData(tree);


        ImgButton addButton = new ImgButton();
        addButton.setSrc("[SKIN]actions/add.png");
        addButton.setSize(16);
        addButton.setShowFocused(false);
        addButton.setShowRollOver(false);
        addButton.setShowDown(false);
        addButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {

            }
        });

        ImgButton removeButton = new ImgButton();
        removeButton.setSrc("[SKIN]actions/remove.png");
        removeButton.setSize(16);
        removeButton.setShowFocused(false);
        removeButton.setShowRollOver(false);
        removeButton.setShowDown(false);
        removeButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {

            }
        });



        /*DynamicForm form = new DynamicForm();
        form.setHeight(1);
        form.setWidth(75);
        form.setNumCols(1);

        SelectItem selectItem = new SelectItem();
        selectItem.setWidth(120);
        selectItem.setShowTitle(false);
        selectItem.setValueMap("Development", "Staging", "Production");
        selectItem.setDefaultValue("Development");
        selectItem.addChangeHandler(new ChangeHandler() {
            public void onChange(ChangeEvent event) {

            }
        });

        form.setFields(selectItem);
        */

        sectionStack = new SectionStack();

        SectionStackSection section1 = new SectionStackSection();
        section1.setTitle("Profiles");
        section1.addItem(treeGrid);
        section1.setControls(addButton, removeButton);
        section1.setExpanded(true);

        SectionStackSection section2 = new SectionStackSection();
        section2.setTitle("Server Groups");
        //section2.setItems(statusReport);
        //section2.setControls(form);
        section2.setExpanded(true);

        sectionStack.setSections(section1, section2);
        sectionStack.setVisibilityMode(VisibilityMode.MULTIPLE);
        sectionStack.setOverflow(Overflow.HIDDEN);
        sectionStack.setShowResizeBar(true);
        sectionStack.setVisibilityMode(VisibilityMode.MULTIPLE);
        sectionStack.setWidth(220);
        sectionStack.setHeight100();


    }

    public Widget asWidget()
    {
        return sectionStack;
    }
}
