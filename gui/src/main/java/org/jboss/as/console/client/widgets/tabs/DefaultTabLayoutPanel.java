package org.jboss.as.console.client.widgets.tabs;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

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

    public void insert(Widget child, Widget tab, final int beforeIndex) {

        String text = ((HasText) tab).getText();

        HTML html = new HTML(text) {
            {
                this.sinkEvents(Event.ONKEYDOWN);
                this.sinkEvents(Event.ONMOUSEDOWN);
            }

            @Override
            public void onBrowserEvent(Event event) {

                int type = DOM.eventGetType(event);
                switch (type) {
                    case Event.ONKEYDOWN:
                        if(event.getKeyCode()== KeyCodes.KEY_ENTER)
                        {
                            DefaultTabLayoutPanel.this.selectTab(beforeIndex);
                            event.stopPropagation();
                        }
                        break;
                    case Event.ONMOUSEDOWN:
                        DefaultTabLayoutPanel.this.selectTab(beforeIndex);
                        event.stopPropagation();
                        break;
                    default:
                        return;

                }
            }
        };



        html.getElement().setTabIndex(0);

        super.insert(child, html, beforeIndex);
    }
}
