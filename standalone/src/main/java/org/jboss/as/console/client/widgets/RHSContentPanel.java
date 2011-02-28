package org.jboss.as.console.client.widgets;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.*;

/**
 * @author Heiko Braun
 * @date 2/22/11
 */
public class RHSContentPanel extends LayoutPanel {

    private VerticalPanel delegate;

    public RHSContentPanel(String title) {

        super();

        /*final TitleBar titleBar = new TitleBar(title);
        super.add(titleBar);*/

        HorizontalPanel stretch = new HorizontalPanel();
        stretch.getElement().setAttribute("style", "width:100%");

        HTML spacerLeft = new HTML("&nbsp;");
        stretch.add(spacerLeft);
        spacerLeft.getElement().getParentElement().setAttribute("style", "border-bottom:1px solid #A7ABB4;");

        stretch.add(new TabHeader(title));

        HTML spacerRight= new HTML("&nbsp;");
        stretch.add(spacerRight);
        spacerRight.getElement().getParentElement().setAttribute("style", "width:100%;border-bottom:1px solid #A7ABB4;");

        super.add(stretch);

        super.setWidgetTopHeight(stretch, 0, Style.Unit.PX, 28, Style.Unit.PX);

        delegate = new VerticalPanel();
        delegate.setStyleName("fill-layout-width");
        delegate.getElement().setAttribute("style", "padding:15px;");

        super.add(delegate);
        super.setWidgetTopHeight(delegate, 35, Style.Unit.PX, 100, Style.Unit.PCT);

    }

    @Override
    public void add(Widget widget) {
        delegate.add(widget);
    }

}
