package org.jboss.as.console.client.domain.profiles;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
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

        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("fill-layout-width");
        layout.addStyleName("lhs-selector");
        layout.getElement().setAttribute("style","padding:4px;");

        profiles = new ComboBox();
        profiles.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(final ValueChangeEvent<String> event) {

                Scheduler.get().scheduleDeferred(
                        new Scheduler.ScheduledCommand() {
                            @Override
                            public void execute() {
                                Console.getEventBus().fireEvent(
                                        new ProfileSelectionEvent(event.getValue())
                                );
                            }
                        });
            }
        });

        Label profileLabel = new Label(Console.CONSTANTS.common_label_profile()+":");
        profileLabel.setStyleName("header-label");
        layout.add(profileLabel);
        Widget hWidget = profiles.asWidget();
        layout.add(hWidget);

        // the combox box use all available space
        hWidget.getElement().getParentElement().setAttribute("width", "100%");


        return layout;
    }


    public void setProfiles(List<String> profileNames)
    {
        profiles.clearSelection();
        profiles.setValues(profileNames);
        profiles.setItemSelected(0, true);


    }
}

