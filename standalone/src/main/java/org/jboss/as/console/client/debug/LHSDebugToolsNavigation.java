package org.jboss.as.console.client.debug;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.widgets.LHSNavItem;
import org.jboss.as.console.client.widgets.StackSectionHeader;

/**
 * @author Heiko Braun
 * @date 2/10/11
 */
public class LHSDebugToolsNavigation {

    private StackLayoutPanel stack;
    TreeItem subsysRoot;

    public LHSDebugToolsNavigation() {
        super();

        stack = new StackLayoutPanel(Style.Unit.PX);
        stack.addStyleName("section-stack");
        stack.setWidth("180");

        LayoutPanel commonLayout = new LayoutPanel();
        commonLayout.setStyleName("stack-section");

        LHSNavItem[] commonItems = new LHSNavItem[] {
                new LHSNavItem("Browser", "debug/model-browser"),
                new LHSNavItem("Invocation Metrics", "debug/invocation-metrics")
                //new LHSNavItem("Operations", "debug/model-operations"),
        };

        int i =0;
        for(LHSNavItem item : commonItems)
        {
            commonLayout.add(item);
            commonLayout.setWidgetTopHeight(item, i, Style.Unit.PX, 25, Style.Unit.PX);
            i+=25;
        }

        stack.add(commonLayout, new StackSectionHeader("Domain Model"), 28);

    }

    public Widget asWidget()
    {
        return stack;
    }
}