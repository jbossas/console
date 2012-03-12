package org.jboss.as.console.client.shared.viewframework.builder;

import com.google.gwt.user.client.ui.Widget;
import org.jboss.ballroom.client.widgets.window.DefaultWindow;

/**
 * @author Heiko Braun
 * @date 12/1/11
 */
public class ModalWindowLayout {

    private Widget widget;
    private String title = "TITLE";
    private int width = 480;
    private int height = 360;

    public ModalWindowLayout setWidget(Widget widget) {
        this.widget = widget;
        return this;
    }

    public ModalWindowLayout setTitle(String title) {
        this.title = title;
        return this;
    }

    public ModalWindowLayout setSite(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }

    public DefaultWindow build() {
        DefaultWindow window = new DefaultWindow(title);
        window.setWidth(width);
        window.setHeight(height);
        window.trapWidget( widget);
        window.setGlassEnabled(true);
        window.center();

        return window;
    }
}
