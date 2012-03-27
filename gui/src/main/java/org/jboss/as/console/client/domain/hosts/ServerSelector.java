package org.jboss.as.console.client.domain.hosts;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.ballroom.client.widgets.forms.ComboBox;

import java.util.List;

/**
 * A selector for both host and server.
 *
 * @author Heiko Braun
 * @date 11/2/11
 */
public class ServerSelector {

    private ComboBox server;

    public Widget asWidget() {

        HorizontalPanel layout = new HorizontalPanel();
        layout.getElement().setAttribute("style","padding:4px;");

        server = new ComboBox();
        server.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(final ValueChangeEvent<String> event) {

                Scheduler.get().scheduleEntry(new Scheduler.ScheduledCommand() {
                    @Override
                    public void execute() {
                        //Console.getEventBus().fireEvent(new ServerSelectionEvent(event.getValue()));
                    }
                });
            }
        });

        Label hostLabel = new Label("Server:");
        hostLabel.setStyleName("header-label");
        layout.add(hostLabel);
        Widget hWidget = server.asWidget();
        layout.add(hWidget);

        // combo box use all available space
        hWidget.getElement().getParentElement().setAttribute("width", "100%");

        return layout;
    }

    public void setServer(List<String> serverNames)
    {
        server.clearSelection();
        server.setValues(serverNames);
        server.setItemSelected(0, true);
    }

    public void setHosts(List<String> hostNames) {
        //To change body of created methods use File | Settings | File Templates.
    }
}
