package org.jboss.as.console.client.shared.subsys.jca;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.ballroom.client.widgets.icons.Icons;
import org.jboss.ballroom.client.widgets.window.DefaultWindow;
import org.jboss.ballroom.client.widgets.window.DialogueOptions;
import org.jboss.ballroom.client.widgets.window.WindowContentBuilder;

/**
 * @author Heiko Braun
 * @date 10/21/11
 */
public class ConnectionWindow {

    private DefaultWindow window;

    public ConnectionWindow(String name, boolean isValidConnection) {

        String message = isValidConnection ?
                "Successfully created JDBC connection." : "Failed to create JDBC connection!";


        window = new DefaultWindow("Datasource Connection");

        int width = 320;
        int height = 240;

        window.setWidth(width);
        window.setHeight(height);

        window.setGlassEnabled(true);

        VerticalPanel panel = new VerticalPanel();
        panel.setStyleName("default-window-content");

        HorizontalPanel header = new HorizontalPanel();

        HTML text = new HTML("<h3>"+message+"</h3>");
        ImageResource icon = isValidConnection ? Icons.INSTANCE.info_blue() : Icons.INSTANCE.info_red();
        Image image = new Image(icon);
        header.add(image);
        header.add(text);

        image.getElement().getParentElement().setAttribute("style","padding-right:10px;vertical-align:middle");
        text.getElement().getParentElement().setAttribute("style","vertical-align:middle");
        panel.add(header);

        if(isValidConnection)
            panel.add(new HTML("Successfully connected to database "+ name+"."));
        else
            panel.add(new HTML("Please verify the connection settings for datasource "+ name+"."));

        ClickHandler confirmHandler = new ClickHandler() {

            public void onClick(ClickEvent event) {
                window.hide();
            }
        };

        DialogueOptions options = new DialogueOptions("OK", confirmHandler, "Cancel", confirmHandler);
        Widget content = new WindowContentBuilder(panel, options.showCancel(false)).build();
        window.trapWidget(content);

    }

    public void show() {
        window.center();
    }

    public void hide() {
        window.hide();
    }
}
