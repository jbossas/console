package org.jboss.as.console.client.shared.subsys.logging;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.core.DisposableViewImpl;

import java.util.List;
import org.jboss.as.console.client.shared.subsys.logging.model.LoggingHandler;

/**
 * @author Stan Silvert
 * @date 3/29/11
 */
public class LoggingView extends DisposableViewImpl implements LoggingPresenter.MyView {

    private LoggingPresenter presenter;

    LayoutPanel layout = null;

    private LoggingEditor loggingEditor;

    @Override
    public Widget createWidget() {

        this.loggingEditor = new LoggingEditor(presenter);

        TabLayoutPanel tabLayoutpanel = new TabLayoutPanel(25, Style.Unit.PX);
        tabLayoutpanel.addStyleName("default-tabpanel");


        tabLayoutpanel.add(loggingEditor.asWidget(), "Logging");

        tabLayoutpanel.selectTab(0);

        return tabLayoutpanel;
    }

    @Override
    public void setPresenter(LoggingPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void updateLoggingHandlers(List<LoggingHandler> handlers) {
        loggingEditor.updateLoggingHandlers(handlers);
    }
}
