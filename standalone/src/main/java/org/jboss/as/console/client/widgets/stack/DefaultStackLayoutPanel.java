package org.jboss.as.console.client.widgets.stack;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.StackLayoutPanel;

/**
 * @author Heiko Braun
 * @date 3/28/11
 */
public class DefaultStackLayoutPanel extends StackLayoutPanel {

    public DefaultStackLayoutPanel() {
        super(Style.Unit.PX);

        addStyleName("section-stack");
    }

    @Override
    public void showWidget(int index) {
        super.showWidget(index);

        for(int i=0; i<getWidgetCount(); i++)
        {
            if(index==i)
            {
                getHeaderWidget(i).getElement().addClassName("stack-section-header-selected");
            }
            else
            {
                getHeaderWidget(i).getElement().removeClassName("stack-section-header-selected");
            }
        }
    }

}
