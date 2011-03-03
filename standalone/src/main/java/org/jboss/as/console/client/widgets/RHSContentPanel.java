package org.jboss.as.console.client.widgets;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Heiko Braun
 * @date 2/22/11
 */
public class RHSContentPanel extends LayoutPanel {

    private VerticalPanel delegate;

    public RHSContentPanel(String title) {

        super();

        TitleBar titleBar = new TitleBar(title);
        super.add(titleBar);
        super.setWidgetTopHeight(titleBar, 0, Style.Unit.PX, 28, Style.Unit.PX);

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
