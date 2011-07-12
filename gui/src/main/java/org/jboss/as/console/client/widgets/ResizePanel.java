package org.jboss.as.console.client.widgets;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Vince Vice - www.electrosound.tv
 * This is licensed under Apache License Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.html
 *
 * GWT Widget - ResizePanel
 * allows resizing on dragging the bottom right corner
 *
 * emits the signal onResized(width,height)
 */
@SuppressWarnings("unchecked")
public class ResizePanel extends PopupPanel {
    private boolean bDragDrop = false;
    private boolean move = false ;
    private Element movingPanelElement;
    private List panelResizedListeners = new ArrayList();


    public ResizePanel() {
        super();

        //listen to mouse-events
        DOM.sinkEvents(this.getElement(),
                Event.ONMOUSEDOWN |
                        Event.ONMOUSEMOVE |
                        Event.ONMOUSEUP |
                        Event.ONMOUSEOVER
        );
    }

    /**
     * processes the mouse-events to show cursor or change states
     *  - mouseover
     *  - mousedown
     *  - mouseup
     *  - mousemove
     */
    @Override
    public void onBrowserEvent(Event event) {
        final int eventType = DOM.eventGetType(event);
        if (Event.ONMOUSEOVER == eventType) {
            //show different cursors
            if (isCursorResize(event)) {
                DOM.setStyleAttribute(this.getElement(), "cursor", "se-resize");
            } else if(isCursorMove(event)){
                DOM.setStyleAttribute(this.getElement(),"cursor", "se-resize");
            }else {
                DOM.setStyleAttribute(this.getElement(), "cursor", "default");
            }
        }
        if (Event.ONMOUSEDOWN == eventType) {
            if (isCursorResize(event)) {
                //enable/disable resize
                if (bDragDrop == false) {
                    bDragDrop = true;

                    DOM.setCapture(this.getElement());
                }
            }else if(isCursorMove(event)){
                DOM.setCapture(this.getElement());
                move = true;
            }
        } else if (Event.ONMOUSEMOVE == eventType) {
            //reset cursor-type
            if(!isCursorResize(event)&&!isCursorMove(event)){
                DOM.setStyleAttribute(this.getElement(), "cursor", "default");
            }

            //calculate and set the new size
            if (bDragDrop == true) {
                int absX = DOM.eventGetClientX(event);
                int absY = DOM.eventGetClientY(event);
                int originalX = DOM.getAbsoluteLeft(this.getElement());
                int originalY = DOM.getAbsoluteTop(this.getElement());

                //do not allow mirror-functionality
                if(absY>originalY && absX>originalX){
                    Integer height = absY-originalY+2;
                    this.setHeight(height + "px");

                    Integer width = absX-originalX+2;
                    this.setWidth(width + "px");
                    notifyPanelResizedListeners(width, height);
                }
            }else if(move == true){
                RootPanel.get().setWidgetPosition(this, DOM.eventGetClientX(event),DOM.eventGetClientY(event));
            }
        } else if (Event.ONMOUSEUP == eventType) {
            //reset states
            if(move == true){
                move = false;
                DOM.releaseCapture(this.getElement());
            }
            if (bDragDrop == true) {
                bDragDrop = false;
                DOM.releaseCapture(this.getElement());
            }
        }
    }

    /**
     * returns if mousepointer is in region to show cursor-resize
     * @param event
     * @return true if in region
     */
    protected boolean isCursorResize(Event event) {
        int cursorY  = DOM.eventGetClientY(event);
        int initialY = this.getAbsoluteTop();
        int height   = this.getOffsetHeight();

        int cursorX  = DOM.eventGetClientX(event);
        int initialX = this.getAbsoluteLeft();
        int width    = this.getOffsetWidth();

        //only in bottom right corner (area of 10 pixels in square)
        if (((initialX + width - 10) < cursorX && cursorX <= (initialX + width)) &&
                ((initialY + height - 10) < cursorY && cursorY <= (initialY + height)))
            return true;
        else
            return false;
    }

    /**
     * sets the element in panel
     * @param movingPanelElement
     */
    public void setMovingPanelElement(Element movingPanelElement) {
        this.movingPanelElement = movingPanelElement;
    }

    /**
     * is cursor in moving state?
     * @param event event to process
     * @return true if cursor is in movement
     */
    protected boolean isCursorMove(Event event){
        if(movingPanelElement!=null){
            int cursorY = DOM.eventGetClientY(event);
            int initialY = movingPanelElement.getAbsoluteTop();
            int cursorX = DOM.eventGetClientX(event);
            int initialX = movingPanelElement.getAbsoluteLeft();

            if(initialY <= cursorY && initialX <= cursorX)
                return true;
            else
                return false;
        }else
            return false;
    }

    /**
     * Interface function to add a listener to this event
     * @param listener
     */
    public void addPanelResizedListener(PanelResizeListener listener) {
        panelResizedListeners.add(listener);
    }

    /**
     * Interface function to emit signal
     */
    private void notifyPanelResizedListeners(Integer width,Integer height) {
        for (Iterator i = panelResizedListeners.iterator(); i.hasNext(); ) {
            ((PanelResizeListener) i.next()).onResized(width,height);
        }
    }
}
