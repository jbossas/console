package org.jboss.as.console.client.widgets;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TreeItem;

/**
 * @author Heiko Braun
 * @date 3/24/11
 */
public class LHSTreeItem extends TreeItem {


    public LHSTreeItem(String text, String token) {
        setText(text);
        setStyleName("lhs-tree-item");
        getElement().setAttribute("token", token);
    }

    public LHSTreeItem(String text, ClickHandler handler) {
        HTML html = new HTML(text);
        html.addClickHandler(handler);
        setWidget(html);
        setStyleName("lhs-tree-item");
    }

    public LHSTreeItem(String text, ImageResource icon, String token) {

        Image img = new Image(icon);
        Label label = new Label(text);

        HorizontalPanel horz = new HorizontalPanel();
        horz.getElement().setAttribute("style", "padding:0px;");
        horz.add(img);
        horz.add(label);

        img.getElement().getParentElement().setAttribute("style", "vertical-align:middle;padding-right:5px;");
        label.getElement().getParentElement().setAttribute("style", "vertical-align:middle");

        setWidget(horz);

        setStyleName("lhs-tree-item");
        getElement().setAttribute("token", token);
    }

    public void setSelected(boolean selected) {
        super.setSelected(selected);
        if(selected)
            addStyleName("lhs-tree-item-selected");
        else
            removeStyleName("lhs-tree-item-selected");
    }
}
