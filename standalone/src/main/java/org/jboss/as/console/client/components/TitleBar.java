package org.jboss.as.console.client.components;

import com.google.gwt.user.client.Window;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;

/**
 * A title bar to be displayed at the top of a content view - contains a label and/or an icon.
 *
 * @author Ian Springer
 */
public class TitleBar extends ToolStrip {
    private Label label;

    public TitleBar() {
        super();
    }

    public TitleBar(String title) {
        super();

        setWidth100();
        setHeight(37);

        VLayout vLayout = new VLayout();
        vLayout.setAlign(VerticalAlignment.CENTER);
        vLayout.setLayoutMargin(6);

        HLayout hLayout = new HLayout();
        vLayout.addMember(hLayout);

        this.label = new Label();
        this.label.setWidth("*");
        this.label.setAutoHeight();
        hLayout.addMember(this.label);

        setVisible(false);
        addMember(vLayout);

        setTitle(title);

    }

    public TitleBar(String title, String icon) {
        super();

        setWidth100();
        setHeight(37);

        VLayout vLayout = new VLayout();
        vLayout.setAlign(VerticalAlignment.CENTER);
        vLayout.setLayoutMargin(6);

        HLayout hLayout = new HLayout();
        vLayout.addMember(hLayout);

        this.label = new Label();
        this.label.setWidth("*");
        this.label.setIcon(icon);
        this.label.setIconWidth(24);
        this.label.setIconHeight(24);
        this.label.setAutoHeight();
        hLayout.addMember(this.label);

        setVisible(false);
        addMember(vLayout);

        setTitle(title);
    }

    public void setTitle(String title) {
        String normalizedTitle;
        if (title != null) {
            String trimmedTitle = title.trim();
            normalizedTitle = (!trimmedTitle.equals("")) ? trimmedTitle : null;
        } else {
            normalizedTitle = null;
        }
        refresh(normalizedTitle);
    }

    public void setIcon(String icon) {
        this.label.setIcon(icon);
    }

    private void refresh(String title) {
        setVisible(title != null);

        String contents;
        String windowTitle;
        if (title == null) {
            contents = null;
            windowTitle = "JBoss:";
        } else {
            contents = "<span class='HeaderLabel'>" + title + "</span>";
            windowTitle = "JBoss: " + title;
        }
        this.label.setContents(contents);
        Window.setTitle(windowTitle);
    }

}
