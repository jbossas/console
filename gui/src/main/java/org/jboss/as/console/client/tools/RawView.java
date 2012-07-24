package org.jboss.as.console.client.tools;

import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.ballroom.client.widgets.ContentHeaderLabel;
import org.jboss.dmr.client.Property;

/**
 * @author Heiko Braun
 * @date 6/15/12
 */
public class RawView {

    private TextArea dump;
    private TextBox name;
    private TextBox type;
    private ContentHeaderLabel address;

    Widget asWidget() {
        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("fill-layout-width");
        layout.getElement().setAttribute("style", "padding:10px");

        dump = new TextArea();
        dump.setVisibleLines(10);
        dump.setCharacterWidth(200);


        name = new TextBox();
        type = new TextBox();
        address = new ContentHeaderLabel();

        layout.add(address);
        layout.add(name);
        layout.add(type);
        layout.add(dump);

        return layout;
    }

    public void display(Property model)
    {
        name.setText(model.getName());
        type.setText(model.getValue().getType().name());
        dump.setText(model.getValue().toString());
    }

    public void clearDisplay()
       {
           name.setText("");
           type.setText("");
           dump.setText("");
       }
}
