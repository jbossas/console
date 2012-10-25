package org.jboss.as.console.client.debug;

import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.PopupViewImpl;
import org.jboss.ballroom.client.widgets.window.DefaultWindow;

/**
 * @author Heiko Braun
 * @date 10/25/12
 */
public class DebugPresenterView extends PopupViewImpl implements DebugPresenter.MyView {

    private DebugPresenter presenter;
    private DefaultWindow window;
    private DebugPanel debugPanel;

    public DebugPresenterView(EventBus eventBus) {
        super(eventBus);
        create();
    }

    private void create() {
        window = new DefaultWindow("Diagnostics");
        window.setGlassEnabled(true);

        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("window-content");

        debugPanel = new DebugPanel();

        ScrollPanel scroll = new ScrollPanel(debugPanel.asWidget());
        layout.add(scroll);

        window.setWidget(layout);
    }
    @Override
    public void setPresenter(DebugPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget asWidget() {
        return window;
    }
}
