package org.jboss.as.console.client.widgets.nav;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.ballroom.client.widgets.ContentHeaderLabel;
import org.jboss.ballroom.client.widgets.forms.ComboBox;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 11/3/11
 */
public class ServerSwitch {

    private ComboBox serverSelection;

    public Widget asWidget() {
        HorizontalPanel layout = new HorizontalPanel();
        layout.setStyleName("fill-layout-width");

        layout.add(new ContentHeaderLabel("Transaction Metrics"));

        serverSelection= new ComboBox();
        serverSelection.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {

            }
        });

        HorizontalPanel inner = new HorizontalPanel();
        inner.setStyleName("fill-layout-width");

        Label label = new Label("Server: ");
        label.setStyleName("header-label");
        inner.add(label);
        label.getElement().getParentElement().setAttribute("align", "right");

        Widget widget = serverSelection.asWidget();
        inner.add(widget);
        widget.getElement().getParentElement().setAttribute("with", "100%");

        layout.add(inner);

        return layout;
    }

    public void setServerNames(List<String> serverNames) {
        serverSelection.clearSelection();
        serverSelection.setValues(serverNames);
        serverSelection.setItemSelected(0,true);
    }
}
