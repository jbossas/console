package org.jboss.as.console.client.widgets.tabs;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.TabLayoutPanel;

/**
 * @author Heiko Braun
 * @date 1/31/12
 */
public class DefaultTabLayoutPanel extends TabLayoutPanel {

    private final static boolean isIE = Window.Navigator.getUserAgent().contains("MSIE");

    public DefaultTabLayoutPanel(double barHeight, Style.Unit barUnit) {
        super(barHeight, barUnit);
        addStyleName("default-tabpanel");
    }


    @Override
    protected void onAttach() {
        super.onAttach();

        if(isIE)
        {
            Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                @Override
                public void execute() {
                    forceLayout();
                }
            });
        }
    }
}
