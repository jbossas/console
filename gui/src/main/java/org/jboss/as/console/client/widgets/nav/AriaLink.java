package org.jboss.as.console.client.widgets.nav;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HTML;

/**
 * @author Heiko Braun
 * @date 12/15/11
 */
public class AriaLink extends HTML {

    private ClickHandler handler;

    public AriaLink(String title) {
        super("<a href='javascript:void(0)' aria-label='"+ SafeHtmlUtils.htmlEscape(title)+"' style='vertical-align:bottom;padding-left:5px;'>"+title+"</a>");
        getElement().setTabIndex(0);

        this.sinkEvents(Event.ONKEYDOWN);

        addStyleName("inline-link");
    }

    @Override
    public void onBrowserEvent(Event event) {

        int type = DOM.eventGetType(event);
        switch (type) {
            case Event.ONKEYDOWN:
                if(event.getKeyCode()== KeyCodes.KEY_ENTER)
                {
                    if(handler!=null)
                        handler.onClick(null);
                    event.stopPropagation();
                }
                break;
            default:
                return;

        }

    }

    @Override
    public HandlerRegistration addClickHandler(ClickHandler handler) {
        HandlerRegistration handlerRegistration = super.addClickHandler(handler);
        this.handler = handler;
        return handlerRegistration;
    }
}

