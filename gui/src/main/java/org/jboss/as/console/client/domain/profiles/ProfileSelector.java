package org.jboss.as.console.client.domain.profiles;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.domain.events.HostSelectionEvent;
import org.jboss.as.console.client.domain.events.ProfileSelectionEvent;
import org.jboss.ballroom.client.widgets.forms.ComboBox;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 11/2/11
 */
public class ProfileSelector {

    private ComboBox profiles;

    public Widget asWidget() {

        HorizontalPanel layout = new HorizontalPanel();
        layout.getElement().setAttribute("style","padding:4px;");
        profiles = new ComboBox();
        profiles.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(final ValueChangeEvent<String> event) {

                Scheduler.get().scheduleEntry(new Scheduler.ScheduledCommand() {
                    @Override
                    public void execute() {
                        Console.MODULES.getEventBus().fireEvent(new ProfileSelectionEvent(event.getValue()));
                    }
                });
            }
        });

        Label hostLabel = new Label("Profile:");
        hostLabel.setStyleName("header-label");
        layout.add(hostLabel);
        Widget hWidget = profiles.asWidget();
        hWidget.getElement().setAttribute("style", "width:100px;");
        layout.add(hWidget);

        return layout;
    }


    public void setProfiles(List<String> profileNames)
    {
        profiles.clearSelection();
        profiles.setValues(profileNames);
        profiles.setItemSelected(0, true);


    }
}

