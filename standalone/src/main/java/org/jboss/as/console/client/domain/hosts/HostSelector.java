package org.jboss.as.console.client.domain.hosts;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.domain.model.Host;
import org.jboss.as.console.client.widgets.ComboBox;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 3/4/11
 */
public class HostSelector {

    private ComboBox hostSelection;
    private HTMLPanel panel;

    public HostSelector() {


        SafeHtmlBuilder builder = new SafeHtmlBuilder();
        builder.appendHtmlConstant("<div class='host-selector'>");
        builder.appendHtmlConstant("<div class='host-selector-header'>Selected Host:</div>");
        builder.appendHtmlConstant("<div id='host-selector-content'/>");
        builder.appendHtmlConstant("</div>");

        panel = new HTMLPanel(builder.toSafeHtml());

        hostSelection = new ComboBox();
        hostSelection.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                fireHostSelection(event.getValue());
            }
        });
        panel.add(hostSelection.asWidget(), "host-selector-content");

    }

    public Widget asWidget() {
        return panel;
    }

    private void fireHostSelection(String hostName) {
        Console.MODULES.getEventBus().fireEvent(new HostSelectionEvent(hostName));
    }

    public void updateHosts(final List<Host> hostRecords) {

        hostSelection.clearValues();

        for(Host record : hostRecords)
            hostSelection.addItem(record.getName());

        // select first option when updated
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                hostSelection.setItemSelected(0, true);
                fireHostSelection(hostSelection.getSelectedValue());
            }
        });

    }

}
