package org.jboss.as.console.client.widgets;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import org.jboss.as.console.client.widgets.icons.Icons;

/**
 * @author Heiko Braun
 * @date 2/23/11
 */
public class DefaultWindow extends PopupPanel {

    LayoutPanel content;
    private final static double GOLDEN_RATIO = 1.618;

    int width, height;

    public DefaultWindow(String title) {

        LayoutPanel layout = new LayoutPanel();
        setStyleName("default-window");

        HorizontalPanel header = new HorizontalPanel();
        header.setStyleName("default-window-header");
        header.getElement().setAttribute("cellpadding", "4");

        HTML titleText = new HTML(title);

        Image closeIcon = new Image(Icons.INSTANCE.close());
        closeIcon.setAltText("Close");
        closeIcon.addClickHandler(new ClickHandler(){
            @Override
            public void onClick(ClickEvent clickEvent) {
                hide();
            }
        });

        header.add(titleText);
        header.add(closeIcon);

        // it's just a table ...
        titleText.getElement().getParentElement().setAttribute("width", "100%");
        closeIcon.getElement().getParentElement().setAttribute("width", "16px");

        //header.setWidgetRightWidth(closeIcon, 5, Style.Unit.PX, 16, Style.Unit.PX);
        //header.setWidgetRightWidth(titleText, 21, Style.Unit.PX, 95, Style.Unit.PCT);

        layout.add(header);

        content = new LayoutPanel();
        layout.add(content);

        super.setWidget(layout);

        // default width(height
        int winWidth = (int)(Window.getClientWidth()*0.9);
        int winHeight = (int) ( winWidth / GOLDEN_RATIO );

        setWidth(winWidth);
        setHeight(winHeight);

        layout.setWidgetTopHeight(header, 0, Style.Unit.PX, 25, Style.Unit.PX);
        layout.setWidgetTopHeight(content, 25, Style.Unit.PX, 500, Style.Unit.PX);
    }

    @Override
    public void setWidget(Widget w) {
        content.clear();
        content.add(w);
    }

    @Override
    public void center() {
        setPopupPosition(
                (Window.getClientWidth()/2)-(width/2),
                (Window.getClientHeight()/2)-(height/2)
        );
        show();

        super.setWidth(width+"px");
        super.setHeight(height+"px");
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public void setWidth(String width) {
        throw new IllegalArgumentException("Use the numeric setter!") ;
    }

    @Override
    public void setHeight(String height) {
        throw new IllegalArgumentException("Use the numeric setter!") ;
    }
}
