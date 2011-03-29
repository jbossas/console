package org.jboss.as.console.client.widgets.forms;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.widgets.icons.Icons;

/**
 * @author Heiko Braun
 * @date 3/28/11
 */
class InputElementWrapper extends HorizontalPanel {

    private Image img = new Image(Icons.INSTANCE.exclamation());

    public InputElementWrapper(Widget widget, final InputElement input) {
        super();
        add(widget);
        add(img);
        img.setVisible(false);
        img.getElement().getParentElement().setAttribute("style", "vertical-align:middle");

        img.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                PopupPanel popup = new PopupPanel(true);
                popup.setWidget(new Label(input.getErrMessage()));
                popup.setStyleName("popup-hint");
                popup.setPopupPosition(img.getAbsoluteLeft()+16, img.getAbsoluteTop()+16);
                popup.show();
            }
        });
    }

    public void setErroneous(boolean hasErrors)
    {
        img.setVisible(hasErrors);
    }

}
