package org.jboss.as.console.client.widgets;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class LoadingOverlay
{
    static PopupPanel p = null;

    public static void on(Widget parent, boolean loading)
    {
        if(parent !=null && loading)
        {
            int left = parent.getAbsoluteLeft();
            int top = parent.getAbsoluteTop();

            int width = parent.getOffsetWidth();
            int height = parent.getOffsetHeight();

            p = new PopupPanel();
            //p.setStylePrimaryName("bpm-loading-overlay");
            p.setWidget(new Image("images/loading_lite.gif"));
            p.setPopupPosition(left+(width/2)-15, top+(height/2)-15);
            p.show();

        }
        else
        {
            if(p!=null)
            {
                p.hide();
                p = null;
            }
        }
    }

    public static void hide() {
        if(p!=null) p.hide();
    }
}

