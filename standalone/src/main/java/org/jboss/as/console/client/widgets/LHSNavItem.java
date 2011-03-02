package org.jboss.as.console.client.widgets;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.layout.client.Layout;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.LayoutPanel;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.Places;

/**
 * @author Heiko Braun
 * @date 2/21/11
 */
public class LHSNavItem extends LayoutPanel {

    public LHSNavItem(String title, final String token) {

        setStyleName("lhs-nav-item");
        HTML html = addText(token, title);
        setWidgetLeftWidth(html, 15, Style.Unit.PX, 100, Style.Unit.PCT);
    }

    public LHSNavItem(String title, final String token, ImageResource icon) {
        setStyleName("lhs-nav-item");
        Image image = new Image(icon);
        add(image);
        HTML html = addText(token, title);

        setWidgetLeftWidth(image, 15, Style.Unit.PX, 10, Style.Unit.PX);
        setWidgetTopHeight(image, 5, Style.Unit.PX, 10, Style.Unit.PX);
        setWidgetLeftWidth(html, 29, Style.Unit.PX, 100, Style.Unit.PCT);
    }

    private HTML addText(final String token, String title) {
        HTML text = new HTML(title);
        text.getElement().setAttribute("style", "padding-top:5px;");
        text.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Console.MODULES.getPlaceManager().revealPlaceHierarchy(
                        Places.fromString(token)
                );
            }
        });

        add(text);
        return text;
    }
}
