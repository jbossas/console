package org.jboss.as.console.client.standalone;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.core.DisposableViewImpl;
import org.jboss.ballroom.client.widgets.tabs.FakeTabPanel;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.TextItem;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;
import org.jboss.ballroom.client.widgets.window.Feedback;

/**
 * @author Heiko Braun
 * @date 6/7/11
 */
public class StandaloneServerView extends DisposableViewImpl implements StandaloneServerPresenter.MyView {

    private StandaloneServerPresenter presenter;
    private Form<StandaloneServer> form ;

    @Override
    public Widget createWidget() {
        LayoutPanel layout = new LayoutPanel();

        FakeTabPanel titleBar = new FakeTabPanel("Standalone Server");
        layout.add(titleBar);

        // ----

        final ToolStrip toolStrip = new ToolStrip();

        toolStrip.addToolButtonRight(new ToolButton("Reload", new ClickHandler(){
            @Override
            public void onClick(ClickEvent event) {
                Feedback.confirm("Reload server configuration",
                        "Do you want ot reload the server configuration for server " + form.getEditedEntity().getName() + "?",
                        new Feedback.ConfirmationHandler() {
                            @Override
                            public void onConfirmation(boolean isConfirmed) {
                                if (isConfirmed) {
                                    presenter.onReloadServerConfig();
                                }
                            }
                        });
            }
        }));


        layout.add(toolStrip);

        // ---

        VerticalPanel panel = new VerticalPanel();
        panel.setStyleName("rhs-content-panel");

        ScrollPanel scrollPanel = new ScrollPanel(panel);
        layout.add(scrollPanel);

        layout.setWidgetTopHeight(titleBar, 0, Style.Unit.PX, 40, Style.Unit.PX);
        layout.setWidgetTopHeight(toolStrip, 40, Style.Unit.PX, 30, Style.Unit.PX);
        layout.setWidgetTopHeight(scrollPanel, 70, Style.Unit.PX, 100, Style.Unit.PCT);


        form = new Form<StandaloneServer>(StandaloneServer.class);
        form.setNumColumns(2);

        TextItem nameItem = new TextItem("name", "Name");
        TextItem socketItem = new TextItem("socketBinding", "Socket Binding");

        form.setFields(nameItem, socketItem);

        panel.add(form.asWidget());
        return layout;
    }

    @Override
    public void setPresenter(StandaloneServerPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void updateFrom(StandaloneServer server) {
        form.edit(server);
    }
}
