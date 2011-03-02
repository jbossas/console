package org.jboss.as.console.client.widgets;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;

/**
 * Collection of feedback windows.
 * Info, Confirmation, Alert, etc.
 * @author Heiko Braun
 * @date 3/2/11
 */
public class Feedback {

    public static void confirm(String title, String message, final ConfirmationHandler handler)
    {
        final DefaultWindow window = new DefaultWindow(title);
        window.setWidth(200);
        window.setHeight(150);
        window.setGlassEnabled(true);

        LayoutPanel panel = new LayoutPanel();
        panel.getElement().setAttribute("style", "width:180px; height:120px; margin:10px");

        HTML text = new HTML(message);

        DefaultButton ok = new DefaultButton("OK");
        ok.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                handler.onConfirmation(true);
                window.hide();
            }
        });

        Label cancel = new Label("Cancel");
        cancel.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                handler.onConfirmation(false);
                window.hide();
            }
        });

        HorizontalPanel options = new HorizontalPanel();
        options.add(ok);
        options.add(cancel);

        panel.add(text);
        panel.add(options);

        panel.setWidgetBottomHeight(text, 30, Style.Unit.PX, 90, Style.Unit.PX);
        panel.setWidgetBottomHeight(options, 0, Style.Unit.PX, 30, Style.Unit.PX);

        window.setWidget(panel);

        window.center();
    }

    public interface ConfirmationHandler
    {
        void onConfirmation(boolean isConformed);
    }
}
