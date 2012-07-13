package org.jboss.as.console.client.tools;

import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.core.SuspendableViewImpl;

/**
 * @author Heiko Braun
 * @date 6/15/12
 */
public class ToolsView extends SuspendableViewImpl implements ToolsPresenter.MyView {
    private ToolsPresenter presenter;
    LayoutPanel contentCanvas = new LayoutPanel();

    @Override
    public void setPresenter(ToolsPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget createWidget() {

        //setContent(new HTML("<center>This is the workbench. No tool selected.</center>"));
        return contentCanvas;
    }

    @Override
    public void setInSlot(Object slot, Widget content) {

        if (slot == ToolsPresenter.TYPE_MainContent) {
            if(content!=null)
                setContent(content);

        }
    }

    private void setContent(Widget newContent) {
        contentCanvas.clear();
        contentCanvas.add(newContent);
    }
}
