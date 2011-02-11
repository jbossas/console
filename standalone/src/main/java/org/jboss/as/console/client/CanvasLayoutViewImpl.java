package org.jboss.as.console.client;

import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.VLayout;
import org.jboss.as.console.client.util.message.Message;

/**
 * An alternative main layout that builds on SmartGWT Canvas.
 * Doesn't layout correctly at this point.
 *
 * @author Heiko Braun
 * @date 2/8/11
 */
public class CanvasLayoutViewImpl extends ViewImpl implements MainLayoutPresenter.MainLayoutView {

    public static final String CONTENT_CANVAS_ID = "BaseContent";

    private static RootCanvas rootCanvas;
    private static Canvas canvas;

    @Override
    public Widget asWidget() {

        canvas = new Canvas(CONTENT_CANVAS_ID);
        canvas.setWidth100();
        canvas.setHeight100();

        rootCanvas = new RootCanvas();
        rootCanvas.setOverflow(Overflow.HIDDEN);
        rootCanvas.addMember(Console.MODULES.getHeader().asWidget());

        rootCanvas.addMember(Console.MODULES.getMessageBar().asWidget());

        rootCanvas.addMember(canvas);
        rootCanvas.addMember(Console.MODULES.getFooter().asWidget());
        rootCanvas.draw();

        return rootCanvas;
    }

    @Override
    public void setInSlot(Object slot, Widget content) {

        if (slot == MainLayoutPresenter.TYPE_SetMainContent) {
            if(content!=null)
                setContent(content);
        } else {
            Console.MODULES.getMessageCenter().notify(
                    new Message("Unknown slot requested:" + slot)
            );
        }

    }

    public static void setContent(Widget newContent) {

        Canvas contentCanvas = Canvas.getById(CONTENT_CANVAS_ID);

        for (Canvas child : contentCanvas.getChildren()) {
            child.destroy();
        }
        if (newContent != null) {
            contentCanvas.addChild(newContent);
        }
        contentCanvas.markForRedraw();
    }

    private class RootCanvas extends VLayout {

        private RootCanvas() {
            super();
            setWidth100();
            setHeight100();
        }
    }
}
