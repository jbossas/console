package org.jboss.as.console.client.widgets;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasAllMouseHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import org.jboss.as.console.client.widgets.icons.Icons;

public class PopupTitleBar extends Composite implements HasAllMouseHandlers {

    private int origWidth   = -1;
    private int origHeight  = -1;
    private int origTop     = -1;
    private int origLeft    = -1;

    public PopupTitleBar(String title, final PopupPanel callback) {

        final HorizontalPanel header = new HorizontalPanel();
        header.setStyleName("default-window-header");

        HTML titleText = new HTML(title);
        titleText.getElement().setAttribute("style", "padding:5px");

        Image closeIcon = new Image(Icons.INSTANCE.close());
        closeIcon.setAltText("Close");
        closeIcon.addClickHandler(new ClickHandler(){
            @Override
            public void onClick(ClickEvent clickEvent) {
                callback.hide();
            }
        });

        Image maximizeIcon = new Image(Icons.INSTANCE.maximize());
        maximizeIcon.setAltText("Min/Maximize");
        maximizeIcon.addClickHandler(new ClickHandler(){
            @Override
            public void onClick(ClickEvent clickEvent) {

                int width = origWidth;
                int height = origHeight;

                int top = origTop;
                int left = origLeft;

                if(origWidth==-1)
                {
                    origWidth = getOffsetWidth();
                    origHeight = (int) ( origWidth / 1.618 ) +50;// TODO: this fails "getOffsetHeight()";
                    origLeft = getAbsoluteLeft();
                    origTop = getAbsoluteTop();

                    width = Window.getClientWidth() - 50;
                    height = Window.getClientHeight() - 50;

                    top = 25;
                    left = 25;
                }
                else
                {
                    origWidth = -1;
                    origHeight = -1;
                    origLeft = -1;
                    origTop = -1;

                }

                callback.hide();

                callback.setPopupPosition(top, left);
                callback.setWidth(width+"px");
                callback.setHeight(height+"px");

                callback.show();

            }
        });

        header.add(titleText);
        header.add(maximizeIcon);
        header.add(closeIcon);

        initWidget(header);

        titleText.getElement().getParentElement().setAttribute("width", "100%");

        maximizeIcon.getElement().getParentElement().setAttribute("width", "16px");
        maximizeIcon.getElement().getParentElement().setAttribute("style", "width:16px;padding-right:5px");

        closeIcon.getElement().getParentElement().setAttribute("width", "16px");
        closeIcon.getElement().getParentElement().setAttribute("style", "width:16px;padding-right:5px");

    }

    public HandlerRegistration addMouseMoveHandler(MouseMoveHandler handler) {
        return addDomHandler( handler,
                MouseMoveEvent.getType() );
    }

    public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
        return addDomHandler( handler,
                MouseOutEvent.getType() );
    }

    public HandlerRegistration addMouseOverHandler(MouseOverHandler handler) {
        return addDomHandler( handler,
                MouseOverEvent.getType() );
    }

    public HandlerRegistration addMouseUpHandler(MouseUpHandler handler) {
        return addDomHandler( handler,
                MouseUpEvent.getType() );
    }

    public HandlerRegistration addMouseWheelHandler(MouseWheelHandler handler) {
        return addDomHandler( handler,
                MouseWheelEvent.getType() );
    }

    public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
        return addDomHandler( handler,
                MouseDownEvent.getType() );
    }

}