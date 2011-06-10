package org.jboss.as.console.client.widgets;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Heiko Braun
 * @date 6/9/11
 */
public class WindowContentBuilder {

    private Widget options;
    private Widget content;

    public WindowContentBuilder(Widget contentPanel, Widget options) {
        this.options = options;
        this.content = contentPanel;
    }

    public Widget build() {
        DockLayoutPanel wrapper = new DockLayoutPanel(Style.Unit.PX);
        wrapper.addSouth(options, 35);

        ScrollPanel scroll = new ScrollPanel(content);
        wrapper.add(scroll);

        return wrapper;
    }
}
